# 🧪 Plan de Pruebas - Sprint 1

> **Proyecto:** Digital Money House - Backend
> **Versión:** 1.0

---

## 1. Testing Kickoff

**Objetivo:**
Validar la correcta implementación de la arquitectura de microservicios, la seguridad con Keycloak y los flujos principales de usuario (Registro, Login, Logout y Creación de Cuenta).

**Alcance:**
* Microservicios: API Gateway (8080), Auth, User, Account.
* Infraestructura: Docker, Keycloak, MySQL.

**Herramientas:**
* ApiDog (API Testing).
* DBeaver (Validación de Datos).
* Swagger UI.

---

## 2. Suite de Casos de Prueba (Testing Manual)

| ID | Caso de Prueba | Precondiciones | Pasos | Resultado Esperado | Estado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **CP-01** | **Registro Exitoso** | Docker UP. BD Limpia. | `POST /users/register` con email nuevo. | **201 Created**. JSON retorna ID, CVU y Alias. | ✅ Pasó |
| **CP-02** | **Email Duplicado** | Usuario `juan@test.com` existe. | `POST /users/register` con email `juan@test.com`. | **400/500 Error**. Mensaje de validación. | ✅ Pasó |
| **CP-03** | **Cuenta Automática** | Ninguna. | Verificar tabla `accounts` tras registro. | Existe registro con `user_id` correcto y saldo 0. | ✅ Pasó |
| **CP-04** | **Login Exitoso** | Usuario registrado. | `POST /auth/login` con credenciales OK. | **200 OK**. Retorna `access_token` y `refresh_token`. | ✅ Pasó |
| **CP-05** | **Login Fallido** | Ninguna. | `POST /auth/login` con pass erróneo. | **401 Unauthorized**. | ✅ Pasó |
| **CP-06** | **Seguridad Endpoint** | Ninguna. | `POST /accounts` sin token de admin. | **403 Forbidden**. | ✅ Pasó |
| **CP-07** | **Logout** | Sesión activa. | `POST /auth/logout` con `refreshToken`. | **200 OK**. | ✅ Pasó |

---

## 3. Testing Exploratorio

**Sesión #1 - Seguridad y Resiliencia**
* **Tester:** Jeremias
* **Enfoque:** Seguridad en comunicación entre microservicios y validación de integridad.

### 🛡️ Hallazgo Crítico: Seguridad en Creación de Cuentas (Mitigado)
* **Observación:** Durante las pruebas iniciales del microservicio `account-service`, identifiqué una vulnerabilidad de tipo **IDOR (Insecure Direct Object Reference)**. Si el endpoint `POST /accounts` se dejaba abierto a cualquier usuario autenticado (`ROLE_USER`), un usuario malintencionado podría enviar un JSON con un `userId` ajeno (ej: `{ "userId": 999 }`) y crear cuentas fraudulentas o huérfanas.
* **Acción Correctiva:** Se implementó una restricción de seguridad estricta a nivel de **Roles**. Ahora, el endpoint `POST /accounts` **rechaza (403 Forbidden)** cualquier petición que no provenga de un administrador o del propio sistema.
* **Implementación:** Se configuró el `user-service` para autenticarse contra Keycloak usando credenciales de cliente (*Service Account*), obteniendo un token especial con el rol `SERVICE`. El `account-cervice` valida este rol antes de permitir la creación.
* **Resultado:** La creación de cuentas es segura y solo puede ser disparada por el flujo de registro oficial.

### 🧟 Hallazgo: Ciclo de Vida del Token (Token Zombi)
* **Observación:** Al realizar el Logout, se confirmó que el `refresh_token` es invalidado correctamente en Keycloak ("Session not active"). Sin embargo, el `access_token` actual sigue siendo válido hasta su expiración natural (TTL).
* **Conclusión:** Es el comportamiento esperado en una arquitectura **Stateless** con JWT. Se confirma que la seguridad depende de tiempos de expiración cortos para el Access Token.

### ⚙️ Hallazgo: Infraestructura y Persistencia
* **Observación:** Se validó el script de inicialización `init.sql` en Docker. Al levantar la infraestructura desde cero (`docker-compose down -v`), las bases de datos `db_users` y `db_accounts` se recrean automáticamente, y Keycloak importa la configuración sin intervención manual.
* **Resultado:** El entorno es reproducible y portable.