package com.gabriel.gestorfinanciero;

import com.gabriel.gestorfinanciero.model.TipoTransaccion;
import com.gabriel.gestorfinanciero.service.ParsedSms;
import com.gabriel.gestorfinanciero.service.parser.BciSmsParser;
import com.gabriel.gestorfinanciero.service.parser.GenericSmsParser;
import com.gabriel.gestorfinanciero.service.parser.SantanderSmsParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmsParserTests {

    private final GenericSmsParser genericParser = new GenericSmsParser();
    private final SantanderSmsParser santanderParser = new SantanderSmsParser("", "", "");
    private final BciSmsParser bciParser = new BciSmsParser("", "", "");

    // ── Genérico: Compra ──

    @Test
    void compraGenericaDetectaMontoYCategoría() {
        ParsedSms result = genericParser.parse("Compra por $25.000 en LIDER");
        assertNotNull(result);
        assertEquals(25000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
        assertEquals("Compras", result.getCategoria());
        assertTrue(result.getDescripcion().contains("LIDER"));
    }

    @Test
    void compraGenericaConMontoGrande() {
        ParsedSms result = genericParser.parse("Compra por $2.500.000 en CASA MATRIZ");
        assertNotNull(result);
        assertEquals(2500000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
    }

    @Test
    void compraGenericaMinusculas() {
        ParsedSms result = genericParser.parse("compra por $15.000 en farmacia ahumada");
        assertNotNull(result);
        assertEquals(15000, result.getMonto());
        assertEquals("Compras", result.getCategoria());
    }

    // ── Genérico: Transferencia saliente ──

    @Test
    void transferenciaSalienteDetectaMontoYDestinatario() {
        ParsedSms result = genericParser.parse("Transferiste $15.000 a Juan Perez.");
        assertNotNull(result);
        assertEquals(15000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
        assertEquals("Transferencias", result.getCategoria());
        assertTrue(result.getDescripcion().contains("Juan Perez"));
    }

    @Test
    void enviasteDetectaTransferenciaSaliente() {
        ParsedSms result = genericParser.parse("Enviaste $50.000 a Maria Rodriguez");
        assertNotNull(result);
        assertEquals(50000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
    }

    // ── Genérico: Transferencia entrante / Depósito ──

    @Test
    void recibisteDetectaIngreso() {
        ParsedSms result = genericParser.parse("Recibiste $800.000 de Sueldo");
        assertNotNull(result);
        assertEquals(800000, result.getMonto());
        assertEquals(TipoTransaccion.INGRESO, result.getTipo());
        assertEquals("Ingresos", result.getCategoria());
    }

    @Test
    void depositoDetectaIngreso() {
        ParsedSms result = genericParser.parse("Depósito de $100.000 recibido");
        assertNotNull(result);
        assertEquals(100000, result.getMonto());
        assertEquals(TipoTransaccion.INGRESO, result.getTipo());
    }

    @Test
    void abonoDetectaIngreso() {
        ParsedSms result = genericParser.parse("Abonaste $15.000 en tu cuenta");
        assertNotNull(result);
        assertEquals(15000, result.getMonto());
        assertEquals(TipoTransaccion.INGRESO, result.getTipo());
    }

    // ── Genérico: Débito ──

    @Test
    void debitoDetectaGasto() {
        ParsedSms result = genericParser.parse("Débito de $12.000 por servicio de internet");
        assertNotNull(result);
        assertEquals(12000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
        assertEquals("Gastos Fijos", result.getCategoria());
    }

    // ── Genérico: Fallback ──

    @Test
    void mensajeSinPatronConocidoExtraeMontoSimple() {
        ParsedSms result = genericParser.parse("Tu saldo es $50.000");
        assertNotNull(result);
        assertEquals(50000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
        assertEquals("Sin categoría", result.getCategoria());
    }

    @Test
    void mensajeVacioRetornaNull() {
        assertNull(genericParser.parse(""));
        assertNull(genericParser.parse(null));
        assertNull(genericParser.parse("   "));
    }

    @Test
    void mensajeSinMontoRetornaNull() {
        assertNull(genericParser.parse("No hay información de monto"));
    }

    // ── Santander ──

    @Test
    void santanderCompraDetectaMonto() {
        ParsedSms result = santanderParser.parse("Santander: Compra por $35.000 en JUMBO");
        assertNotNull(result);
        assertEquals(35000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
        assertEquals("Compras", result.getCategoria());
    }

    @Test
    void santanderTransferenciaDetectaMonto() {
        ParsedSms result = santanderParser.parse("Santander: Transferiste $20.000 a Pedro Gomez");
        assertNotNull(result);
        assertEquals(20000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
        assertEquals("Transferencias", result.getCategoria());
    }

    @Test
    void santanderDepositoDetectaIngreso() {
        ParsedSms result = santanderParser.parse("Santander: Recibiste $500.000");
        assertNotNull(result);
        assertEquals(500000, result.getMonto());
        assertEquals(TipoTransaccion.INGRESO, result.getTipo());
    }

    @Test
    void santanderConCuentaDetectaCompra() {
        ParsedSms result = santanderParser.parse("Tu cuenta Corriente debito por $18.500 en FARMACIA Cruz Verde");
        assertNotNull(result);
        assertEquals(18500, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
        assertEquals("Compras", result.getCategoria());
    }

    // ── BCI ──

    @Test
    void bciCompraDetectaMonto() {
        ParsedSms result = bciParser.parse("BCI Compra por $42.000 en RIPLEY");
        assertNotNull(result);
        assertEquals(42000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
        assertEquals("Compras", result.getCategoria());
    }

    @Test
    void bciTransferenciaDetectaMonto() {
        ParsedSms result = bciParser.parse("BCI Transferiste $30.000 a Carlos Lopez");
        assertNotNull(result);
        assertEquals(30000, result.getMonto());
        assertEquals(TipoTransaccion.GASTO, result.getTipo());
    }

    @Test
    void bciDepositoDetectaIngreso() {
        ParsedSms result = bciParser.parse("BCI Recibiste $600.000");
        assertNotNull(result);
        assertEquals(600000, result.getMonto());
        assertEquals(TipoTransaccion.INGRESO, result.getTipo());
    }
}
