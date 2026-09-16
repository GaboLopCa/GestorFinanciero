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

> ⚠️ En producción (Render) **debe** definirse `JWT_SECRET` con un valor robusto (≥ 32 bytes). Si no se define, la app usa un secret por defecto apto solo para desarrollo.
>
> ℹ️ Hibernate detecta el dialecto automáticamente desde la URL JDBC. `open-in-view` está desactivado (`spring.jpa.open-in-view=false`); las asociaciones perezosas se cargan con `JOIN FETCH` en los repositorios y no fuera de transacción.

### 6.5 Formato Uniforme de Respuestas de Error

Todas las respuestas de error usan el formato JSON `{ "status", "error", "message", "timestamp" }` gestionado por `GlobalExceptionHandler`:

| Código | Caso | Origen |
|---|---|---|
| 400 | Validación falló (`@Valid`), JSON/enum inválido o parámetro mal tipado | `MethodArgumentNotValidException` / `BadRequestException` / `HttpMessageNotReadableException` / `MethodArgumentTypeMismatchException` |
| 401 | Credenciales inválidas en login | `UnauthorizedException` |
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
- [x] Agregar tests de integración para controllers y seguridad → **Hecho: `GestorFinancieroIntegrationTests` (17 → 26 tests MockMvc; dependencia `spring-boot-starter-webmvc-test`)**.
- [x] Agregar validación de entrada con `@Valid` en controllers → **Hecho: `spring-boot-starter-validation` + anotaciones en modelos y `AuthRequest`**.

### Fase 3: Integración con iOS (Atajos de Apple) ⏳ Pendiente

- [ ] Diseño del Webhook en la API para recepción de notificaciones bancarias.
- [ ] Creación de rutina en iOS Shortcuts para procesar mensajes bancarios y enviar datos a Render.

### Fase 4: Frontend PWA (Web & Mobile) ⏳ Pendiente

- [ ] Desarrollo de la aplicación web en React con Tailwind CSS.
- [ ] Integración de Service Worker para funcionamiento PWA (Instalable en iPhone).
- [ ] Gráficos e indicadores interactivos de liquidez y evolución de cuotas.