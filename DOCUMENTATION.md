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

---

## 6. Especificación de Endpoints de la API REST

### 6.1 Módulo de Autenticación (/auth)
* POST /auth/registro (Público): Recibe nombre, email, password.
* POST /auth/login (Público): Recibe email, password. Retorna Token JWT.

### 6.2 Módulo de Transacciones / Gastos (/gastos, /transacciones)
* GET /gastos (Protegido - Bearer Token): Retorna los gastos del usuario autenticado.
* POST /gastos (Protegido - Bearer Token): Crea un gasto asignado automáticamente al usuario del Token.
* GET /transacciones/busqueda (Protegido - Bearer Token): Filtros por montoMin, montoMax, categoria, etc.

### 6.3 Módulo de Inversiones y Liquidez (/finanzas)
* GET /finanzas/resumen-liquidez (Protegido - Bearer Token): Retorna el balance entre ingresos, gastos fijos pendientes, gastos variables ejecutados y dinero disponible real.
* POST /finanzas/inversiones/dap (Protegido - Bearer Token): Registra un DAP y retorna la ganancia proyectada.
* POST /finanzas/inversiones/fondo-mutuo (Protegido - Bearer Token): Registra la compra de un Fondo Mutuo indicando el número de cuotas y el valor inicial de la cuota.
* PATCH /finanzas/inversiones/fondo-mutuo/{id}/actualizar-cuota (Protegido - Bearer Token): Actualiza el valor actual de la cuota recalculando automáticamente la ganancia total del fondo.
* GET /finanzas/inversiones/fondo-mutuo/rendimiento (Protegido - Bearer Token): Retorna el historial de variación de valor de las cuotas y la rentabilidad acumulada.

---

## 7. Roadmap de Desarrollo (Fases del Proyecto)

* Fase 1: Core Backend & Seguridad (Completada)
    - [x] Configuración de Spring Boot con JPA, Hibernate y PostgreSQL.
    - [x] Implementación de Spring Security, BCryptEncoder y JWT.
    - [x] Despliegue en la nube mediante GitHub y Render.
    - [x] Lectura implícita de usuarios por Token en controladores.

* Fase 2: Extensión Financiera & CORS (En Curso)
    - [ ] Configuración global de CORS en SecurityConfig para soportar peticiones Web.
    - [ ] Creación de las entidades de Ingreso, GastoFijo, Inversion (DAP) y FondoMutuoDetalle (Cuotas).
    - [ ] Implementación de endpoints para cálculo de liquidez, proyecciones de DAP y rendimiento de cuotas.

* Fase 3: Integración con iOS (Atajos de Apple)
    - [ ] Diseño del Webhook en la API para recepción de notificaciones bancarias.
    - [ ] Creación de rutina en iOS Shortcuts para procesar mensajes bancarios y enviar datos a Render.

* Fase 4: Frontend PWA (Web & Mobile)
    - [ ] Desarrollo de la aplicación web en React con Tailwind CSS.
    - [ ] Integración de Service Worker para funcionamiento PWA (Instalable en iPhone).
    - [ ] Gráficos e indicadores interactivos de liquidez y evolución de cuotas.