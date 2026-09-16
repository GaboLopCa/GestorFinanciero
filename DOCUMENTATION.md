# Documentación del Proyecto: GestorFinanciero API & PWA

## 1. Visión y Enfoque del Proyecto

### 1.1 Visión Generatriz
GestorFinanciero es un ecosistema de finanzas personales diseñado para ofrecer un control total, automatizado y transparente de la economía individual y familiar. Nace con el propósito de resolver la fragmentación del dinero actual (cuentas corrientes, inversiones, depósitos a plazo, deudas y gastos recurrentes) centralizando la liquidez real en tiempo real desde dispositivos móviles (iOS / Android) y la web.

### 1.2 Enfoque Técnico y Filosofía de Arquitectura
* Costo Cero ($0 USD/año): Uso de capas gratuitas en la nube (Render para Backend/PostgreSQL, Vercel/Netlify para Frontend) y tecnologías multiplataforma accesibles sin necesidad de hardware dedicado de Apple (Mac) ni cuentas de desarrollador de pago.
* Privacidad First & Modelo Personal: Enfocado inicialmente en uso personal/entorno cercano, garantizando el aislamiento absoluto de los datos mediante cifrado y tokens de sesión de estándar industrial.
* Arquitectura Desacoplada: Separación total entre el backend (API RESTful en Java/Spring Boot) y el cliente (PWA en React/Vue o automatizaciones mediante accesos nativos del sistema operativo).
* Orientación a la Automatización: Minimizar la carga manual de datos delegando la captura de gastos e ingresos a flujos de integración en segundo plano (Shortcuts/Atajos en iOS y webhooks).

---

## 2. Objetivos del sistema

### 2.1 Objetivos Principales
* Visibilidad de Liquidez Real: Calcular con precisión el dinero "libre" o "sobrante" tras descontar compromisos financieros fijos y proyectados del mes.
* Centralización Multi-Activo: Consolidar en un solo lugar ingresos (sueldo, dividendos, ganancias), gastos (fijos y variables) e inversiones (Depósitos a Plazo, Fondos Mutuos).
* Seguimiento Automatizado de Fondos Mutuos: Monitoreo de la variación del valor de cuotas en Fondos Mutuos, calculando automáticamente las ganancias o pérdidas acumuladas según la cantidad de cuotas que posea el usuario.
* Ingreso Ágil de Datos: Reducir a un solo toque la confirmación de registros financieros detectados por notificaciones o alertas bancarias.

### 2.2 Objetivos Secundarios
* Proveer métricas avanzadas y consultas complejas para toma de decisiones financieras (ej. "filtrar gastos mayores a $20.000" o "compras en categoría inversiones").
* Generar proyecciones automatizadas de retornos sobre inversiones a plazo fijo (DAP).
* Adaptabilidad multiplataforma mediante PWA (Progressive Web App) con experiencia nativa en iOS (Safari) y Android (Chrome).

---

## 3. Criterios de Éxito Funcional

* 1. Autenticación y Aislamiento: Tokens JWT + BCrypt + SSL en Render
* 2. Control de Liquidez Real: Ingresos - (Gastos Fijos + Variables)
* 3. Captura Automática iOS: SMS/Push Banco -> Atajos iOS -> API REST
* 4. Proyección y Variación Inversiones: Cálculo automático de retorno en DAP y ganancias por cuotas de Fondos Mutuos

El proyecto se considerará funcionalmente exitoso al cumplir los siguientes hitos:

1. Gestión de Identidad y Seguridad:
    * Registro, login y emisión de tokens JWT.
    * Encriptación de contraseñas mediante BCrypt.
    * Imposibilidad de que un usuario acceda, edite o consulte transacciones de otro usuario.

2. Gestión de Transacciones y Categorización:
    * Registrar gastos variables asociándolos automáticamente al usuario autenticado.
    * Categorizar ingresos por tipo (Sueldo, Inversión, Venta, Otro).
    * Configurar reglas de Gastos Fijos (semanales, mensuales) con indicación de estado de pago.

3. Cálculo de Liquidez y Proyecciones:
    * Mostrar el indicador de Liquidez Disponible en tiempo real.
    * Calcular el rendimiento esperado de un Depósito a Plazo (DAP) según la fórmula:
      Monto Final = Monto Inicial * (1 + Tasa)^Período
    * Calcular automáticamente el rendimiento de Fondos Mutuos:
      Valor Actual = Cantidad de Cuotas * Valor Cuota Actual
      Ganancia / Pérdida = Valor Actual - Monto Invertido Inicial

4. Integración con iOS (iPhone):
    * Recepción exitosa de peticiones provenientes de Atajos de iOS (Shortcuts) para procesar notificaciones bancarias sin intervención manual en la app web.

---

## 4. Arquitectura de Software y Componentes

```text
[ CLIENTES ]
 ├── iPhone / iOS (Atajos/Shortcuts)   ──> JSON / Bearer JWT
 ├── Navegador Web (PWA Responsive)   ──> HTTPS / CORS
 └── Android Device (Web App / Chrome) ──> JSON / JWT
       │
       ▼
[ SPRING BOOT BACKEND (Render) ]
 ├── SecurityConfig (CORS / CSRF)
 ├── JwtFilter (Autenticación Interceptor)
 └── Controllers (Auth, Gastos, Inversiones)
       │
       ▼ JDBC / HikariCP
[ POSTGRESQL DATABASE (Render Cloud) ]
```
---

## 5. Especificaciones del Modelo de Datos (Base de Datos)

### 5.1 Entidades Principales

* Usuario: id (PK), nombre, email (UNIQUE), password (BCrypt).
* Transaccion: id (PK), descripcion, monto, fecha, tipo (GASTO/INGRESO), categoria, usuario_id (FK).
* GastoFijo: id (PK), concepto, monto, frecuencia (SEMANAL/MENSUAL), dia_cobro, usuario_id (FK).
* Inversion: id (PK), tipo (DAP/FONDO_MUTUO), monto_inicial, tasa_interes (para DAP), fecha_inicio, fecha_vencimiento, usuario_id (FK).
* FondoMutuoDetalle: id (PK), nombre_fondo, cantidad_cuotas, valor_cuota_compra, valor_cuota_actual, fecha_ultimo_calculo, inversion_id (FK).

### 5.2 Estado Actual de la Base de Datos de Desarrollo (H2)

> Estado documentado de `data/gestordb.mv.db` (H2 embebida, usuario `sa`, sin contraseña). Diagnóstico inicial 2026-09-15; saneamiento de datos aplicado 2026-09-16.

**Tablas existentes (7):**

| Tabla | Columnas | Filas |
|---|---|---|
| `USUARIOS` | id (BIGINT), email (UNIQUE), nombre, password | 2 |
| `GASTO` | id (BIGINT), descripcion, monto (INTEGER), usuario_id | 0 |
| `INGRESOS` | id, descripcion, fecha, fuente, monto (INTEGER), usuario_id | 0 |
| `GASTOS_FIJOS` | id, concepto, dia_cobro, frecuencia, monto (INTEGER), pagado, usuario_id | 0 |
| `TRANSACCIONES` | id, descripcion, monto (INTEGER), fecha, tipo, categoria, usuario_id | 0 |
| `INVERSIONES` | id, tipo (DAP/FONDO_MUTUO), monto_inicial (INTEGER), tasa_interes, fecha_inicio, fecha_vencimiento, usuario_id | 0 |
| `FONDO_MUTUO_DETALLES` | id, nombre_fondo, cantidad_cuotas, valor_cuota_compra, valor_cuota_actual, fecha_ultimo_calculo, inversion_id | 0 |

> ℹ️ `INGRESOS` y `GASTOS_FIJOS` fueron creadas por la ejecución de tests (Hibernate `ddl-auto=update`, 2026-09-16). `TRANSACCIONES`, `INVERSIONES` y `FONDO_MUTUO_DETALLES` se crearon junto a los nuevos endpoints de la Fase 2.3 (2026-09-16). No contienen datos todavía. Los montos monetarios usan el tipo `INTEGER` (CLP) cuando corresponden a valores en pesos.

**Estado / Hallazgos (2026-09-16):**

* ✅ **Datos de prueba saneados:** se eliminaron el usuario id=1 (password en texto plano `secreta123`) y su gasto de prueba. Solo quedan usuarios con hash BCrypt válido.
    * `gabriel@test.com` (id=2) — password hash BCrypt.
    * `demo@test.com` (id=4) — **Usuario Demo**, hash BCrypt de `demo1234` (generado para prueba de login).
* ✅ **Constraint UNIQUE aplicado:** `uk_usuarios_email` sobre `USUARIOS.EMAIL` + `@Column(unique=true)` en la entidad (aplica también a PostgreSQL vía Hibernate).
* ✅ **Fase 2 completada (2026-09-16):** entidades `TRANSACCIONES`, `INVERSIONES` y `FONDO_MUTUO_DETALLES` creadas con sus repositorios y endpoints (`/transacciones/busqueda`, `/finanzas/*`).
* ℹ️ La tabla `GASTO` ya no contiene datos (fue purgada junto al usuario id=1).

---

## 6. Especificación de Endpoints de la API REST

### 6.1 Módulo de Autenticación (/auth)
* POST /auth/registro (Público): Recibe nombre, email, password.
* POST /auth/login (Público): Recibe email, password. Retorna JSON `{ "token": "..." }` (JWT).

### 6.2 Módulo de Transacciones / Gastos (/gastos, /transacciones)
* GET /gastos (Protegido - Bearer Token): Retorna los gastos del usuario autenticado.
* POST /gastos (Protegido - Bearer Token): Crea un gasto asignado automáticamente al usuario del Token.
* POST /transacciones (Protegido - Bearer Token): Crea una transacción (descripcion, monto, tipo GASTO/INGRESO, categoria, fecha opcional).
* GET /transacciones/busqueda (Protegido - Bearer Token): Filtros por montoMin, montoMax, categoria, tipo, fechaDesde, fechaHasta.

### 6.3 Módulo de Inversiones y Liquidez (/finanzas)
* GET /finanzas/resumen-liquidez (Protegido - Bearer Token): Retorna el balance entre ingresos, gastos fijos pendientes, gastos variables ejecutados y dinero disponible real.
* POST /finanzas/inversiones/dap (Protegido - Bearer Token): Registra un DAP y retorna la ganancia proyectada.
* POST /finanzas/inversiones/fondo-mutuo (Protegido - Bearer Token): Registra la compra de un Fondo Mutuo indicando el número de cuotas y el valor inicial de la cuota.
* PATCH /finanzas/inversiones/fondo-mutuo/{id}/actualizar-cuota (Protegido - Bearer Token): Actualiza el valor actual de la cuota recalculando automáticamente la ganancia total del fondo.
* GET /finanzas/inversiones/fondo-mutuo/rendimiento (Protegido - Bearer Token): Retorna el historial de variación de valor de las cuotas y la rentabilidad acumulada.

### 6.3b Módulo Webhook iOS (/webhook)
* POST /webhook/transacciones (Semi-público — auth híbrida: JWT en `Authorization: Bearer` **o** API key en header `X-Webhook-Token`): Recibe un SMS bancario, lo parsea y crea una transacción automáticamente. Body: `{ "mensaje": "<texto del SMS>", "banco": "SANTANDER|BCI|GENERICO", "fecha": "opcional" }`. Retorna `{ "transaccion": {...}, "parsed": { monto, descripcion, categoria, tipo } }`.

### 6.4 Variables de Entorno Requeridas

| Variable | Uso | Ejemplo (local) |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL de la base de datos | `jdbc:h2:file:./data/gestordb` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la DB | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Password de la DB | *(vacío)* |
| `SPRING_DATASOURCE_DRIVER` | Driver JDBC | `org.h2.Driver` / `org.postgresql.Driver` |
| `SPRING_JPA_DIALECT` | Dialecto Hibernate (auto-detected por defecto) | `org.hibernate.dialect.H2Dialect` / `PostgreSQLDialect` |
| `SPRING_SHOW_SQL` | Log de SQL (opcional, default `true` local) | `true` / `false` |
| `SPRING_H2_CONSOLE` | Consola H2 (solo desarrollo, default `true`) | `true` / `false` |
| `JWT_SECRET` | Clave de firma HS256 (mín. 32 bytes) | Se usa un valor por defecto solo para desarrollo local |
| `WEBHOOK_SECRET` | API key del webhook iOS (header `X-Webhook-Token`) | `dev_webhook_secret_local` |
| `WEBHOOK_DEFAULT_EMAIL` | Usuario destino cuando autentica solo con API key | `demo@test.com` |

> ⚠️ En producción (Render) **debe** definirse `JWT_SECRET` con un valor robusto (≥ 32 bytes). Si no se define, la app usa un secret por defecto apto solo para desarrollo.
>
> ⚠️ En producción **debe** definirse `WEBHOOK_SECRET` con un valor secreto largo (ej. UUID). `WEBHOOK_DEFAULT_EMAIL` debe apuntar a tu usuario real (ej. `gabriel@test.com`).
>
> ℹ️ Hibernate detecta el dialecto automáticamente desde la URL JDBC. `open-in-view` está desactivado (`spring.jpa.open-in-view=false`); las asociaciones perezosas se cargan con `JOIN FETCH` en los repositorios y no fuera de transacción.

### 6.5 Formato Uniforme de Respuestas de Error

Todas las respuestas de error usan el formato JSON `{ "status", "error", "message", "timestamp" }` gestionado por `GlobalExceptionHandler`:

| Código | Caso | Origen |
|---|---|---|
| 400 | Validación falló (`@Valid`), JSON/enum inválido o parámetro mal tipado | `MethodArgumentNotValidException` / `BadRequestException` / `HttpMessageNotReadableException` / `MethodArgumentTypeMismatchException` |
| 401 | Credenciales inválidas en login o auth del webhook inválida | `UnauthorizedException` |
| 403 | Acceso a recurso de otro usuario | `ForbiddenException` |
| 404 | Usuario o recurso no encontrado | `ResourceNotFoundException` |
| 409 | Email duplicado u otra restricción de integridad | `DataIntegrityViolationException` |

---

## 7. Roadmap de Desarrollo (Fases del Proyecto)

### Fase 1: Core Backend & Seguridad ✅ Completada

- [x] Configuración de Spring Boot 4.1.0 con JPA, Hibernate y PostgreSQL (H2 para desarrollo local).
- [x] Implementación de Spring Security, BCryptEncoder y JWT (JJWT 0.11.5).
- [x] Despliegue en la nube mediante GitHub y Render (Docker multi-stage).
- [x] Lectura implícita de usuarios por Token en controladores.
- [x] Configuración de persistencia con variables de entorno (SPRING_DATASOURCE_*).

### Fase 2: Extensión Financiera & CORS ✅ Completada

**Sub-fase 2.1: CORS y Controllers básicos ✅**
- [x] Configuración global de CORS en SecurityConfig para soportar peticiones Web.
- [x] Controller de Gastos (`/gastos` — GET, POST).
- [x] Controller de Ingresos (`/ingresos` — GET, POST).
- [x] Controller de Gastos Fijos (`/gastos-fijos` — GET, POST, PATCH toggle-pago).

**Sub-fase 2.2: Entidades financieras ✅**
- [x] Crear entidad `Transaccion` (con campo `categoria` para clasificación avanzada).
- [x] Crear entidad `Inversion` (tipo DAP/FONDO_MUTUO, monto_inicial, tasa_interes, fechas).
- [x] Crear entidad `FondoMutuoDetalle` (nombre_fondo, cuotas, valor_cuota_compra, valor_cuota_actual).
- [x] Crear repositorios correspondientes.

**Sub-fase 2.3: Endpoints de finanzas ✅**
- [x] `GET /transacciones/busqueda` — filtros por montoMin, montoMax, categoria, etc.
- [x] `POST /transacciones` — crear transacciones (añadido 2026-09-16 para alimentar la búsqueda).
- [x] `GET /finanzas/resumen-liquidez` — balance entre ingresos, gastos fijos, gastos variables y dinero disponible.
- [x] `POST /finanzas/inversiones/dap` — registrar DAP y retornar ganancia proyectada.
- [x] `POST /finanzas/inversiones/fondo-mutuo` — registrar compra de cuotas.
- [x] `PATCH /finanzas/inversiones/fondo-mutuo/{id}/actualizar-cuota` — actualizar valor cuota y recalcular ganancia.
- [x] `GET /finanzas/inversiones/fondo-mutuo/rendimiento` — historial de variación y rentabilidad acumulada.

**Mejoras de calidad aplicadas (2026-09-16):**
- [x] `@JsonIgnore` + `FetchType.LAZY` en `Ingreso.usuario` y `GastoFijo.usuario` (antes exponían el usuario en las respuestas JSON y eran EAGER).
- [x] Sumas de `resumen-liquidez` delegadas a queries agregadas (`SUM` en repositorios) en vez de cargar listas completas.
- [x] `spring.jpa.open-in-view=false` + `JOIN FETCH` en repos (`findByIdConUsuario`, `findByIdConInversionYUsuario`, `findByUsuarioIdConDetalles`) para evitar LazyInitializationException fuera de transacción.
- [x] Dialecto Hibernate auto-detectado (se eliminó la propiedad explícita).
- [x] 400 para JSON malformado, enum inválido y parámetros mal tipados (`HttpMessageNotReadableException`, `MethodArgumentTypeMismatchException`).
- [x] Login retorna JSON `{ "token": ... }` (antes `text/plain` con el token puro).
- [x] Configuraciones `SPRING_SHOW_SQL` y `SPRING_H2_CONSOLE` vía variables de entorno.

### Fase 2.5: Consolidación y Calidad del Código ✅ Completada

- [x] Corregir inconsistencia de tipos: unificar `monto` (actualmente `Double` en GastoFijo vs `int` en Gasto/Ingreso) → **Hecho (2026-09-16): `GastoFijo.monto` ahora es `int` y la columna H2 es `INTEGER`**.
- [x] Limpiar datos de prueba en H2: eliminar usuario con password en texto plano (id=1) y consolidar duplicado de `gabriel@test.com` → **Hecho (2026-09-16)**.
- [x] Garantizar constraint UNIQUE sobre `email` en la base de datos (actualmente no se aplica en H2) → **Hecho: `uk_usuarios_email` + `@Column(unique=true)`**.
- [x] Sincronizar el esquema ejecutando la app (Hibernate `ddl-auto=update`) para crear las tablas `INGRESO` y `GASTO_FIJOS`; generar datos de prueba cifrados con BCrypt → **Hecho (2026-09-16): tablas creadas por tests + usuario demo con hash BCrypt insertado**.
- [x] Proteger o eliminar endpoint `/usuarios` (exponía todos los usuarios sin autenticación) → **Eliminado**.
- [x] Mover secret JWT de `JwtUtils.java` a variable de entorno (`JWT_SECRET`).
- [x] Crear clases de respuesta uniforme (evitar `RuntimeException` — usar respuestas HTTP correctas 401/403/400) → **Hecho: paquete `exception` (GlobalExceptionHandler + excepciones 400/401/403/404/409)**.
- [x] Limpiar y recompilar `target/` (clases compiladas estaban desactualizadas) → **Resuelto: `mvnw compile` + `mvnw test` regeneran las clases (2026-09-16)**.
- [x] Agregar tests de integración para controllers y seguridad → **Hecho: `GestorFinancieroIntegrationTests` (36 tests MockMvc + `SmsParserTests` 19 unitarios; dependencia `spring-boot-starter-webmvc-test`)**.
- [x] Agregar validación de entrada con `@Valid` en controllers → **Hecho: `spring-boot-starter-validation` + anotaciones en modelos y `AuthRequest`**.

### Fase 3: Integración con iOS (Atajos de Apple) 🚧 En Desarrollo

**Sub-fase 3.1: Webhook en la API ✅**
- [x] Diseño del Webhook en la API para recepción de notificaciones bancarias → **Hecho (2026-09-16): `POST /webhook/transacciones`**.
- [x] Autenticación híbrida: Bearer JWT **o** API key `X-Webhook-Token` (`WebhookAuthService`).
- [x] Parser de SMS genérico + override por banco (Santander, BCI) con patrones configurables en properties (`webhook.parsers.*`).
- [x] Persistencia automática de la transacción parseada en la tabla `TRANSACCIONES`.
- [x] Tests: 19 unitarios de parsing + 10 de integración del webhook → **56 tests totales OK**.
- [ ] Creación de rutina en iOS Shortcuts para procesar mensajes bancarios y enviar datos a Render → **Guía en la sección 8; la rutina se crea manualmente en el iPhone**.

### Fase 4: Frontend PWA (Web & Mobile) ⏳ Pendiente

- [ ] Desarrollo de la aplicación web en React con Tailwind CSS.
- [ ] Integración de Service Worker para funcionamiento PWA (Instalable en iPhone).
- [ ] Gráficos e indicadores interactivos de liquidez y evolución de cuotas.

---

## 8. Guía de Integración con iOS (Atajos / Shortcuts)

### 8.1 Flujo General

El iPhone no puede "enviar" los SMS automáticamente. Flujo real:

```text
[Llega SMS bancario]
        │
        ▼
[Usuario abre Atajo "Registrar Compra"]  ← manual (o Automación personal si se habilita)
        │
        ▼
[Shortcut copia el texto del SMS → llama POST /webhook/transacciones]
        │
        ▼
[API parsea el mensaje + guarda Transaccion → responde {transaccion, parsed}]
        │
        ▼
[Shortcut muestra confirmación / se completa]
```

### 8.2 Endpoint del Webhook

* **URL (Render):** `https://<tu-app>.onrender.com/webhook/transacciones`
* **URL (local):** `http://localhost:8080/webhook/transacciones`
* **Método:** `POST`
* **Headers requeridos:** `Content-Type: application/json` + **una** de estas auth:

| Modo | Header | Valor |
|---|---|---|
| API key (recomendado para Shortcuts) | `X-Webhook-Token` | `WEBHOOK_SECRET` de producción |
| Bearer JWT | `Authorization` | `Bearer <token de /auth/login>` |

* **Body (JSON):**
```json
{
  "mensaje": "Compra por $25.000 en LIDER",
  "banco": "GENERICO",
  "fecha": "2026-09-16"
}
```

### 8.3 Formatos de SMS soportados

| Banco | Campo `banco` | Patrón | Ejemplo |
|---|---|---|---|
| General | `GENERICO` | Compra | `Compra por $25.000 en LIDER` |
| General | `GENERICO` | Transferencia saliente | `Transferiste $15.000 a Juan Perez` |
| General | `GENERICO` | Ingreso | `Recibiste $800.000 de Sueldo` |
| General | `GENERICO` | Depósito | `Depósito de $100.000 recibido` |
| General | `GENERICO` | Débito | `Débito de $12.000 por internet` |
| Santander | `SANTANDER` | Compra/Débito | `Tu cuenta Corriente debito por $18.500 en FARMACIA Cruz Verde` |
| Santander | `SANTANDER` | Transferencia | `Santander: Transferiste $20.000 a Pedro Gomez` |
| Santander | `SANTANDER` | Ingreso | `Santander: Recibiste $500.000` |
| BCI | `BCI` | Compra | `BCI Compra por $42.000 en RIPLEY` |
| BCI | `BCI` | Transferencia | `BCI Transferiste $30.000 a Carlos Lopez` |
| BCI | `BCI` | Ingreso | `BCI Recibiste $600.000` |

> ℹ️ Si `banco` no coincide con ninguno conocido, se usa el parser genérico automáticamente.
> Los patrones de Santander/BCI se pueden sobreescribir sin recompilar vía `webhook.parsers.santander.*` y `webhook.parsers.bci.*` en properties/env vars.

### 8.4 Categorías que se asignan automáticamente

| Tipo de mensaje | Tipo transacción | Categoría |
|---|---|---|
| Compra | GASTO | `Compras` |
| Transferencia saliente | GASTO | `Transferencias` |
| Débito | GASTO | `Gastos Fijos` |
| Ingreso / Depósito | INGRESO | `Ingresos` |
| Mensaje genérico (fallback) | GASTO | `Sin categoría` |

### 8.5 Creación de la rutina en iOS Shortcuts (paso a paso)

> Requisitos: iPhone con iOS 16 o superior, app **Atajos** (nativa).

1. Abrir **Atajos** → toco el ícono `+` → **Nuevo atajo**.
2. Nombre sugerido: `Registrar compra GestorFinanciero`.
3. Agregar acción **Texto en el portapapeles** (opcional: primero copias el SMS desde Mensajes).
4. Agregar acción **Obtener texto de entrada**:
   - *Alto: texto* ✅, botón *Continuar*.
5. Agregar acción **Variable de texto** para armar el JSON:
   - Valor:
     ```
     {"mensaje":"<TEXTO DEL SMS>","banco":"GENERICO"}
     ```
   - (reemplazar `<TEXTO DEL SMS>` por la variable del paso 4 arrastrándola dentro del texto).
6. Agregar acción **Obtener contenido de la URL**:
   - *Tipo:* `POST`.
   - *URL:* `https://<tu-app>.onrender.com/webhook/transacciones`.
   - *Encabezados:* `Content-Type: application/json` y `X-Webhook-Token: <WEBHOOK_SECRET>`.
   - *Cuerpo:* `Solicitud>Body: El campo siguiente` + arrastrar la variable JSON del paso 5.
   - *Continua en app* off.
7. Agregar acción **Mostrar resultado** (opcional): arrastrar la variable de *Contenido de URL* para ver la respuesta JSON del servidor.
8. Tocar **Listo** y, para probar: pones un SMS de ejemplo en el portapapeles → ejecutas el atajo → debe aparecer la transacción creada.

**Automatización (opcional):** en la pestaña **Automatización** crear una **Automatización personal**:
* Condición: `Abre la app` → app Mensajes (o una hora).
* Acción: ejecutar el atajo `Registrar compra GestorFinanciero`.
* iOS pedirá confirmación la primera vez; en versiones nuevas se puede desactivar "Preguntar antes de ejecutar".

### 8.6 Prueba rápida local (curl / PowerShell)

```powershell
$body = '{"mensaje":"Compra por $25.000 en LIDER","banco":"GENERICO"}'
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/webhook/transacciones" `
  -Headers @{ "Content-Type" = "application/json"; "X-Webhook-Token" = "dev_webhook_secret_local" } `
  -Body $body
```

### 8.7 Errores posibles

| Respuesta | Motivo |
|---|---|
| 400 | JSON malformado, `mensaje` vacío o SMS sin monto interpretable |
| 401 | Falta header de auth o API key/JWT inválido |
| 401 (usuario) | `WEBHOOK_DEFAULT_EMAIL` no existe en la DB (modo API key) |
| 200 | Mensaje parseado y transacción guardada correctamente |