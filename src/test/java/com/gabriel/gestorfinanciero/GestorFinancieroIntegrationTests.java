package com.gabriel.gestorfinanciero;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Transactional
class GestorFinancieroIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    private String emailAleatorio() {
        return "test" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }

    private String registrarUsuario(String email) throws Exception {
        return mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Test\",\"email\":\"" + email + "\",\"password\":\"pass1234\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String obtenerToken(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"pass1234\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return body.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void registroExitoso() throws Exception {
        String respuesta = registrarUsuario(emailAleatorio());
        org.junit.jupiter.api.Assertions.assertTrue(respuesta.contains("Usuario registrado"));
    }

    @Test
    void registroSinEmailDevuelve400() throws Exception {
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Test\",\"password\":\"pass1234\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registroConEmailDuplicadoDevuelve409() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otra\",\"email\":\"" + email + "\",\"password\":\"otra1234\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void loginConCredencialesValidasRetornaToken() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);
        org.junit.jupiter.api.Assertions.assertNotNull(token);
        org.junit.jupiter.api.Assertions.assertFalse(token.isBlank());
    }

    @Test
    void loginConPasswordIncorrectoDevuelve401() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void gastosSinTokenSonRechazados() throws Exception {
        mockMvc.perform(get("/gastos"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void gastosConTokenDevuelvenListaVacia() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(get("/gastos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void crearGastoAsignaUsuarioAutenticado() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/gastos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monto\":4500,\"descripcion\":\"Almuerzo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto").value(4500))
                .andExpect(jsonPath("$.descripcion").value("Almuerzo"));
    }

    @Test
    void crearGastoMontoCeroDevuelve400() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/gastos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monto\":0,\"descripcion\":\"Sin monto\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void toggleGastoFijoDeOtroUsuarioDevuelve403() throws Exception {
        String emailA = emailAleatorio();
        String emailB = emailAleatorio();
        registrarUsuario(emailA);
        registrarUsuario(emailB);
        String tokenB = obtenerToken(emailB);
        String tokenA = obtenerToken(emailA);

        MvcResult creado = mockMvc.perform(post("/gastos-fijos")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"concepto\":\"Arriendo\",\"monto\":250000,\"frecuencia\":\"MENSUAL\",\"diaCobro\":5}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = creado.getResponse().getContentAsString();
        String id = body.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(patch("/gastos-fijos/" + id + "/toggle-pago")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("No tienes permisos")));
    }

    @Test
    void resumenLiquidezConDatosVacios() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(get("/finanzas/resumen-liquidez")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIngresos").value(0))
                .andExpect(jsonPath("$.gastosFijosPendientes").value(0))
                .andExpect(jsonPath("$.gastosVariablesEjecutados").value(0))
                .andExpect(jsonPath("$.dineroDisponibleReal").value(0));
    }

    @Test
    void resumenLiquidezCalculaBalance() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/ingresos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monto\":500000,\"descripcion\":\"Sueldo\",\"fuente\":\"Trabajo\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/gastos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monto\":100000,\"descripcion\":\"Sueldo\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/gastos-fijos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"concepto\":\"Arriendo\",\"monto\":150000,\"frecuencia\":\"MENSUAL\",\"diaCobro\":5}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/finanzas/resumen-liquidez")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIngresos").value(500000))
                .andExpect(jsonPath("$.gastosFijosPendientes").value(150000))
                .andExpect(jsonPath("$.gastosVariablesEjecutados").value(100000))
                .andExpect(jsonPath("$.dineroDisponibleReal").value(250000));
    }

    @Test
    void crearDapRetornaGananciaProyectada() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/finanzas/inversiones/dap")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"DAP\",\"montoInicial\":1000000,\"tasaInteres\":5.0,\"fechaInicio\":\"2026-01-01\",\"fechaVencimiento\":\"2026-07-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inversion.id").exists())
                .andExpect(jsonPath("$.gananciaProyectada").value(24795));
    }

    @Test
    void crearDapSinTasaDevuelve400() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/finanzas/inversiones/dap")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"DAP\",\"montoInicial\":1000000,\"fechaInicio\":\"2026-01-01\",\"fechaVencimiento\":\"2026-07-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearFondoMutuoAsignaDetalles() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/finanzas/inversiones/fondo-mutuo")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"FONDO_MUTUO\",\"montoInicial\":1000000,\"fechaInicio\":\"2026-01-01\",\"fondoMutuoDetalles\":[{\"nombreFondo\":\"Modelo B\",\"cantidadCuotas\":5,\"valorCuotaCompra\":10000,\"valorCuotaActual\":12000}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fondoMutuoDetalles[0].nombreFondo").value("Modelo B"))
                .andExpect(jsonPath("$.fondoMutuoDetalles[0].id").exists());
    }

    @Test
    void actualizarCuotaDeFondoMutuoDeOtroUsuarioDevuelve403() throws Exception {
        String emailA = emailAleatorio();
        String emailB = emailAleatorio();
        registrarUsuario(emailA);
        registrarUsuario(emailB);
        String tokenA = obtenerToken(emailA);
        String tokenB = obtenerToken(emailB);

        MvcResult creado = mockMvc.perform(post("/finanzas/inversiones/fondo-mutuo")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"FONDO_MUTUO\",\"montoInicial\":1000000,\"fechaInicio\":\"2026-01-01\",\"fondoMutuoDetalles\":[{\"nombreFondo\":\"Modelo B\",\"cantidadCuotas\":5,\"valorCuotaCompra\":10000,\"valorCuotaActual\":12000}]}"))
                .andExpect(status().isOk())
                .andReturn();

        String body = creado.getResponse().getContentAsString();
        String detalleId = body.replaceAll("(?s).*\"fondoMutuoDetalles\".*?\"id\":(\\d+).*", "$1");

        mockMvc.perform(patch("/finanzas/inversiones/fondo-mutuo/" + detalleId + "/actualizar-cuota")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"valorCuotaActual\":15000}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("No tienes permisos")));
    }

    @Test
    void busquedaTransaccionesFiltraPorCategoria() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(get("/transacciones/busqueda")
                        .header("Authorization", "Bearer " + token)
                        .param("categoria", "Sueldo"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    void loginRetornaTokenEnJson() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"pass1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void crearTransaccionAsignaUsuarioAutenticado() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/transacciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Sueldo Septiembre\",\"monto\":800000,\"tipo\":\"INGRESO\",\"categoria\":\"Sueldo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto").value(800000))
                .andExpect(jsonPath("$.tipo").value("INGRESO"))
                .andExpect(jsonPath("$.categoria").value("Sueldo"));
    }

    @Test
    void busquedaTransaccionesEncuentraCategoria() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/transacciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Compra\",\"monto\":5000,\"tipo\":\"GASTO\",\"categoria\":\"Supermercado\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/transacciones/busqueda")
                        .header("Authorization", "Bearer " + token)
                        .param("categoria", "Supermercado")
                        .param("montoMax", "6000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria").value("Supermercado"))
                .andExpect(jsonPath("$[0].monto").value(5000));
    }

    @Test
    void transaccionSinCategoriaDevuelve400() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/transacciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Sueldo\",\"monto\":800000,\"tipo\":\"INGRESO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void jsonInvalidoDevuelve400() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/transacciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Sueldo\",\"monto\":"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void tipoEnumInvalidoDevuelve400() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/transacciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Raro\",\"monto\":5000,\"tipo\":\"DESCONOCIDO\",\"categoria\":\"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void parametroTipoInvalidoEnBusquedaDevuelve400() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(get("/transacciones/busqueda")
                        .header("Authorization", "Bearer " + token)
                        .param("tipo", "INVALIDO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearIngresoNoExponeUsuario() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/ingresos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monto\":100000,\"descripcion\":\"Sueldo\",\"fuente\":\"Trabajo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").doesNotExist());
    }

    @Test
    void crearGastoFijoNoExponeUsuario() throws Exception {
        String email = emailAleatorio();
        registrarUsuario(email);
        String token = obtenerToken(email);

        mockMvc.perform(post("/gastos-fijos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"concepto\":\"Arriendo\",\"monto\":250000,\"frecuencia\":\"MENSUAL\",\"diaCobro\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").doesNotExist());
    }
}