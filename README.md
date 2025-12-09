# 🏦 Digital Money House - Backend Microservices

Backend de la billetera virtual **Digital Money House**. Este proyecto implementa una arquitectura de microservicios robusta, escalable y segura, diseñada para gestionar usuarios, cuentas bancarias, tarjetas y transacciones en tiempo real.

---

## 📑 Índice de Contenido

1. [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
2. [Stack Tecnológico](#-stack-tecnológico)
3. [Instalación y Ejecución](#-instalación-y-ejecución)
4. [Documentación de API (Endpoints)](#-documentación-de-api-endpoints)
    - [Autenticación (Auth Service)](#1-autenticación-auth-service)
    - [Usuarios (User Service)](#2-usuarios-user-service)
    - [Cuentas (Account Service)](#3-cuentas-account-service)
    - [Tarjetas (Account Service)](#4-tarjetas-account-service)
5. [Casos de Prueba y QA](#-casos-de-prueba-y-qa)

---

## 🧩 Arquitectura del Proyecto

El sistema se divide en dominios funcionales independientes que se comunican a través de **OpenFeign** y **Eureka Discovery Server**.

![Diagrama de Arquitectura](docs/architecture-diagram.png)

### Microservicios Principales

| Servicio | Puerto | Descripción |
| :--- | :--- | :--- |
| **API Gateway** | `8080` | Puerta de enlace única. Enruta el tráfico y gestiona CORS. |
| **Eureka Server** | `8761` | Servidor de descubrimiento (Service Discovery). |
| **Auth Service** | `8081` | Gestión de autenticación y validación de tokens contra Keycloak. |
| **User Service** | `8082` | Registro de usuarios, gestión de perfiles y orquestación. |
| **Account Service** | `8083` | Core bancario: Cuentas, alias, CVU, saldos, tarjetas y transacciones. |

---

## 🚀 Stack Tecnológico

*   **Lenguaje:** Java 21
*   **Framework:** Spring Boot 3 (Spring Cloud Gateway, OpenFeign, Eureka)
*   **Seguridad:** Keycloak (Identity Provider), OAuth2 Resource Server, Spring Security
*   **Base de Datos:** MySQL 8.0
*   **Infraestructura:** Docker, Docker Compose (Keycloak + MySQL contenerizados)
*   **Herramientas:** Lombok, Maven
*   **Documentación:** OpenAPI 3 (Swagger)
---

## ⚡ Instalación y Ejecución

### Requisitos
*   Docker Desktop (o Docker Engine + Compose)
*   Java 21
*   Maven

### Pasos
1.  **Clonar repositorio:**
    ```bash
    git clone https://github.com/jearcode/digital-money-house.git
    ```
2.  **Levantar Infraestructura (BD + Keycloak):**
    ```bash
    docker-compose up -d
    ```
    > *Espere unos minutos a que Keycloak termine de importar la configuración y esté listo.*

3.  **Ejecutar Microservicios:**
    Se recomienda iniciar en este orden: `Eureka` -> `Gateway` -> `Account` -> `User` -> `Auth`.

---


# 📚 Documentación de API (Endpoints)

Todas las peticiones deben dirigirse al **API Gateway (Puerto 8080)**.

---

## 1. Autenticación y Registro

### 1.1 Registrar Nuevo Usuario

Crea el usuario en Keycloak, guarda el perfil en BD y **crea automáticamente una cuenta** con CVU y Alias generados.

- **Endpoint:** `POST /users/register`
- **Acceso:** Público
- **Content-Type:** `application/json`

**Request Body:**

```json
{
  "firstName": "Juan",
  "lastName": "Perez",
  "dni": "12345678",
  "email": "juan.perez@example.com",
  "password": "SecurePassword123!",
  "phone": "+5491112345678"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "email": "juan.perez@example.com",
  "firstName": "Juan",
  "account": {
    "id": 10,
    "cvu": "0000001234567890123456",
    "alias": "word.word.word",
    "balance": 0.00
  }
}
```

**Errores Posibles:**
- `400 Bad Request` - Datos inválidos, campos faltantes o formato JSON incorrecto
- `409 Conflict` - Email o DNI ya registrado

---

### 1.2 Login de Usuario

Autentica al usuario y obtiene los tokens de acceso (JWT) para interactuar con el sistema.

- **Endpoint:** `POST /auth/login`
- **Acceso:** Público
- **Content-Type:** `application/json`

**Request Body:**

```json
{
  "email": "juan.perez@example.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIs...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "token_type": "Bearer"
}
```

**Errores Posibles:**
- `400 Bad Request` - Campos faltantes o formato inválido
- `401 Unauthorized` - Credenciales incorrectas

---

### 1.3 Cerrar Sesión (Logout)

Invalida el token de acceso actual y cierra la sesión del usuario.

- **Endpoint:** `POST /users/logout`
- **Acceso:** Authenticated (Token Bearer)

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Response (200 OK):**

```json
{
  "message": "Logout successful"
}
```

**Errores Posibles:**
- `401 Unauthorized` - Token ausente

---

## 2. Gestión de Perfil

### 2.1 Obtener Perfil del Usuario

Devuelve los datos completos del usuario autenticado.

- **Endpoint:** `GET /users/{id}`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "email": "juan.perez@example.com",
  "firstName": "Juan",
  "lastName": "Perez",
  "dni": "12345678",
  "phone": "+5491112345678",
  "account": {
    "id": 10,
    "cvu": "0000001234567890123456",
    "alias": "word.word.word",
    "balance": 1500.50
  }
}
```

**Errores Posibles:**
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para acceder al recurso
- `404 Not Found` - Usuario no existe

---

### 2.2 Actualizar Usuario

Permite modificar datos personales del usuario.

- **Endpoint:** `PATCH /users/{id}`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN
- **Content-Type:** `application/json`
- **Nota:** El email solo se puede cambiar cada 15 días (Rate Limit)

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Request Body:**

```json
{
  "phone": "+5491199999999",
  "firstName": "Juan Carlos"
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "email": "juan.perez@example.com",
  "firstName": "Juan Carlos",
  "lastName": "Perez",
  "dni": "12345678",
  "phone": "+5491199999999"
}
```

**Errores Posibles:**
- `400 Bad Request` - Datos inválidos o formato JSON incorrecto
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para modificar el recurso
- `404 Not Found` - Usuario no existe
- `409 Conflict` - Email ya está en uso por otro usuario

---

## 3. Gestión de Cuenta

### 3.1 Consultar Cuenta

Obtiene la información completa de la cuenta: saldo, CVU y alias.

- **Endpoint:** `GET /accounts/{id}`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "userId": 15,
  "cvu": "0000001234567890123456",
  "alias": "sun.moon.river",
  "balance": 1500.50
}
```

**Errores Posibles:**
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para acceder a la cuenta
- `404 Not Found` - Cuenta no existe

---

### 3.2 Actualizar Alias de Cuenta

Modifica el alias de la cuenta. Debe ser único en el sistema.

- **Endpoint:** `PATCH /accounts/{id}`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN
- **Content-Type:** `application/json`
- **Validación:** Patrón `palabra.palabra.palabra` (letras o números, mínimo 3 caracteres por palabra)

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Request Body:**

```json
{
  "alias": "mi.nuevo.alias"
}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "userId": 15,
  "cvu": "0000001234567890123456",
  "alias": "mi.nuevo.alias",
  "balance": 1500.50
}
```

**Errores Posibles:**
- `400 Bad Request` - Formato de alias inválido o campos faltantes
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para modificar la cuenta
- `404 Not Found` - Cuenta no existe
- `409 Conflict` - Alias ya está en uso por otra cuenta

---

## 4. Gestión de Tarjetas

### 4.1 Asociar Tarjeta

Vincula una tarjeta de crédito o débito a la cuenta del usuario.

- **Endpoint:** `POST /accounts/{id}/cards`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN
- **Content-Type:** `application/json`

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Request Body:**

```json
{
  "number": "4532015112830366",
  "cardholderName": "JUAN PEREZ",
  "expirationDate": "12/28",
  "cvv": "123",
  "type": "DEBIT"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "accountId": 1,
  "lastFourDigits": "0366",
  "cardholderName": "JUAN PEREZ",
  "expirationDate": "12/28",
  "type": "DEBIT",
  "isActive": true,
  "createdAt": "2025-12-08T16:43:57.151049"
}
```

**Errores Posibles:**
- `400 Bad Request` - Datos de tarjeta inválidos o campos faltantes
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para agregar tarjetas a la cuenta
- `404 Not Found` - Cuenta no existe
- `409 Conflict` - Tarjeta ya registrada en el sistema

---

### 4.2 Listar Tarjetas

Devuelve todas las tarjetas asociadas a una cuenta con numeración enmascarada (solo últimos 4 dígitos).

- **Endpoint:** `GET /accounts/{id}/cards`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "accountId": 1,
    "lastFourDigits": "0366",
    "cardholderName": "JUAN PEREZ",
    "expirationDate": "12/28",
    "type": "DEBIT",
    "isActive": true,
    "createdAt": "2025-12-08T16:43:57.151049"
  },
  {
    "id": 2,
    "accountId": 1,
    "lastFourDigits": "5432",
    "cardholderName": "JUAN PEREZ",
    "expirationDate": "06/27",
    "type": "CREDIT",
    "isActive": true,
    "createdAt": "2025-12-07T10:20:30.541230"
  }
]
```

**Errores Posibles:**
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para acceder a las tarjetas
- `404 Not Found` - Cuenta no existe

---

### 4.3 Obtener Tarjeta Específica

Devuelve los datos de una tarjeta en particular asociada a una cuenta.

- **Endpoint:** `GET /accounts/{accountId}/cards/{cardId}`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Response (200 OK):**

```json
{
  "id": 1,
  "accountId": 1,
  "lastFourDigits": "0366",
  "cardholderName": "JUAN PEREZ",
  "expirationDate": "12/28",
  "type": "DEBIT",
  "isActive": true,
  "createdAt": "2025-12-08T16:43:57.151049"
}
```

**Errores Posibles:**
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para acceder a la tarjeta
- `404 Not Found` - Cuenta o tarjeta no existe

---

### 4.4 Eliminar Tarjeta

Elimina (desvincula) una tarjeta asociada a la cuenta.

- **Endpoint:** `DELETE /accounts/{accountId}/cards/{cardId}`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Response (204 No Content)**

Sin cuerpo de respuesta.

**Errores Posibles:**
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para eliminar la tarjeta
- `404 Not Found` - Cuenta o tarjeta no existe

---

## 5. Transacciones

### 5.1 Consultar Transacciones

Lista el historial de transacciones de la cuenta ordenadas por fecha descendente.

- **Endpoint:** `GET /accounts/{id}/transactions`
- **Acceso:** Authenticated (Token Bearer)
- **Autorización:** Propietario del recurso o rol SERVICE/ADMIN

**Request Headers:**
```
Authorization: Bearer {access_token}
```

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "accountId": 15,
    "amount": 50.00,
    "transactionType": "DEPOSIT",
    "description": "Deposit from card",
    "transactionDate": "2025-12-09T14:35:22Z",
    "balance": 1500.50
  },
  {
    "id": 2,
    "accountId": 15,
    "amount": -25.00,
    "transactionType": "WITHDRAWAL",
    "description": "Transfer to external account",
    "transactionDate": "2025-12-08T10:20:15Z",
    "balance": 1450.50
  }
]
```

**Errores Posibles:**
- `401 Unauthorized` - Token ausente o inválido
- `403 Forbidden` - Sin permisos para acceder a las transacciones
- `404 Not Found` - Cuenta no existe

---

## 📝 Notas Importantes

### Autenticación
- Todos los endpoints marcados como "Authenticated" requieren el header `Authorization: Bearer {access_token}`
- Los tokens tienen una duración de 5 minutos (300 segundos)
- Utilizar el `refresh_token` para obtener nuevos tokens sin requerir login

### Autorización
- **Propietario**: El usuario solo puede acceder a sus propios recursos
- **SERVICE/ADMIN**: Roles especiales con acceso completo a todos los recursos

### Formato de Respuestas de Error

Todos los endpoints devuelven errores en el siguiente formato:

```json
{
  "timestamp": "2025-12-09T14:35:22Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already registered",
  "path": "/users/register"
}
```

# 🧪 Casos de Prueba y QA

Resumen de ejecución automatizada con Apidog (Sprint 2).

---

### 📊 Resumen General

- **Total de casos:** 55
- **Aprobados:** 55 ✅
- **Fallidos:** 0 ❌
- **Tasa de éxito:** 100.0%

---

## 👤 Módulo: Usuarios

### Registro de Usuarios

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-001 | Registro exitoso de usuario con datos válidos | Email y DNI únicos en BD, todos los campos requeridos presentes y con formato válido | `POST /users/register` | 201 Created | ✅ |
| CP-002 | Rechazo de registro por email duplicado | Existe un usuario con el mismo email en la base de datos | `POST /users/register` | 409 Conflict | ✅ |
| CP-003 | Rechazo de registro por DNI duplicado | Existe un usuario con el mismo DNI en la base de datos | `POST /users/register` | 409 Conflict | ✅ |
| CP-004 | Manejo de error por JSON malformado en registro | Request body con sintaxis JSON inválida (comillas faltantes, comas incorrectas, etc.) | `POST /users/register` | 400 Bad Request | ✅ |
| CP-005 | Validación de campos obligatorios en registro | Request con uno o más campos requeridos ausentes o vacíos | `POST /users/register` | 400 Bad Request | ✅ |
| CP-006 | Validación de formato CVU de 22 dígitos | Usuario registrado correctamente, sistema debe generar CVU automáticamente | `POST /users/register` | 201 Created (CVU length = 22) | ✅ |

### Perfil de Usuario

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-012 | Consulta de perfil con token del propietario | Usuario autenticado con JWT válido solicitando su propio perfil | `GET /users/{id}` | 200 OK | ✅ |
| CP-013 | Rechazo de consulta de perfil con token de tercero | Usuario autenticado intentando acceder al perfil de otro usuario sin permisos de administrador | `GET /users/{id}` | 403 Forbidden | ✅ |
| CP-014 | Consulta de perfil con privilegios de administrador | Usuario con rol admin accediendo a perfil de cualquier usuario | `GET /users/{id}` | 200 OK | ✅ |
| CP-015 | Rechazo de consulta sin autenticación | Request sin header Authorization o con token inválido/expirado | `GET /users/{id}` | 401 Unauthorized | ✅ |

### Actualización de Usuario

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-016 | Actualización exitosa de datos de usuario | Usuario autenticado con token válido y datos de actualización con formato correcto | `PATCH /users/{id}` | 200 OK | ✅ |
| CP-017 | Manejo de error por usuario inexistente | ID de usuario no existe en la base de datos | `PATCH /users/{99}` | 404 Not Found | ✅ |
| CP-018 | Rechazo de actualización con token no autorizado | Token JWT válido pero perteneciente a un usuario diferente al que se intenta actualizar | `PATCH /users/{id}` | 403 Forbidden | ✅ |
| CP-019 | Validación de unicidad de email en actualización | Email que se intenta asignar ya está registrado por otro usuario | `PATCH /users/{id}` | 409 Conflict | ✅ |
| CP-020 | Manejo de error por JSON malformado en actualización | Request body con sintaxis JSON inválida | `PATCH /users/{id}` | 400 Bad Request | ✅ |
| CP-021 | Actualización de usuario con privilegios de administrador | Usuario con rol admin puede actualizar cualquier perfil | `PATCH /users/{id}` | 200 OK | ✅ |

### Cierre de Sesión

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-055 | Cierre de sesión exitoso | Usuario autenticado con JWT válido en el header Authorization | `POST /users/logout` | 200 OK | ✅ |

---

## 🔐 Módulo: Autenticación

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-007 | Autenticación exitosa con credenciales válidas | Usuario registrado en BD, credenciales (email/password) correctas | `POST /auth/login` | 200 OK + JWT | ✅ |
| CP-008 | Rechazo de autenticación por contraseña incorrecta | Usuario existe en BD pero la contraseña proporcionada no coincide con el hash almacenado | `POST /auth/login` | 401 Unauthorized | ✅ |
| CP-009 | Validación de campos obligatorios en login | Request body incompleto (falta email o password) | `POST /auth/login` | 400 Bad Request | ✅ |
| CP-010 | Autenticación con usuario de testing | Credenciales de usuario de prueba predefinido en ambiente de desarrollo/QA | `POST /auth/login` | 200 OK | ✅ |
| CP-011 | Autenticación con credenciales de administrador | Usuario con rol admin, credenciales válidas | `POST /auth/login` | 200 OK | ✅ |

---

## 💳 Módulo: Cuentas

### Consulta de Cuenta

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-022 | Consulta exitosa de cuenta por ID | ID de cuenta existe en BD, token del propietario o admin en header | `GET /accounts/{id}` | 200 OK | ✅ |
| CP-023 | Rechazo de consulta con token no autorizado | Token válido pero perteneciente a usuario sin permisos sobre la cuenta solicitada | `GET /accounts/{id}` | 403 Forbidden | ✅ |
| CP-024 | Consulta de cuenta con privilegios de administrador | Token de usuario con rol admin, ID de cuenta válido | `GET /accounts/{id}` | 200 OK | ✅ |
| CP-025 | Manejo de error por cuenta inexistente | ID de cuenta no existe en la base de datos | `GET /accounts/{99}` | 404 Not Found | ✅ |

### Actualización de Alias

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-050 | Actualización exitosa de alias de cuenta | Alias cumple validaciones de formato y no está en uso, token del propietario válido | `PATCH /accounts/{id}` | 200 OK | ✅ |
| CP-051 | Validación de formato de alias | Alias vacío, contiene caracteres no permitidos o excede longitud máxima | `PATCH /accounts/{id}` | 400 Bad Request | ✅ |
| CP-052 | Manejo de error por cuenta inexistente en actualización | ID de cuenta no existe en la base de datos | `PATCH /accounts/{99}` | 404 Not Found | ✅ |
| CP-053 | Validación de unicidad de alias | Alias ya está asignado a otra cuenta en el sistema | `PATCH /accounts/{id}` | 409 Conflict | ✅ |
| CP-054 | Rechazo de actualización con token no autorizado | Token válido pero perteneciente a usuario sin permisos sobre la cuenta | `PATCH /accounts/{id}` | 403 Forbidden | ✅ |

---

## 💸 Módulo: Transacciones

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:-------|
| CP-026 | Listado de transacciones de cuenta con historial | Cuenta existe y tiene al menos una transacción registrada, token autorizado | `GET /accounts/{id}/transactions` | 200 OK | ✅      |
| CP-027 | Rechazo de consulta de transacciones con token no autorizado | Token válido pero perteneciente a usuario sin permisos sobre la cuenta | `GET /accounts/{id}/transactions` | 403 Forbidden | ✅      |
| CP-028 | Manejo de error por cuenta inexistente en transacciones | ID de cuenta no existe en la base de datos | `GET /accounts/{99}/transactions` | 404 Not Found | ✅      |

---

## 💳 Módulo: Tarjetas

### Creación de Tarjetas

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-029 | Creación exitosa de tarjeta | Cuenta existe, usuario no ha alcanzado el límite máximo de tarjetas, datos válidos, token autorizado | `POST /accounts/{id}/cards` | 201 Created | ✅ |
| CP-030 | Rechazo de creación con token no autorizado | Token válido pero perteneciente a usuario sin permisos sobre la cuenta | `POST /accounts/{id}/cards` | 403 Forbidden | ✅ |
| CP-031 | Rechazo de creación sin autenticación | Request sin header Authorization o con token ausente | `POST /accounts/{id}/cards` | 401 Unauthorized | ✅ |
| CP-032 | Validación de duplicidad de tarjeta | Ya existe una tarjeta con el mismo número o identificador único en la cuenta | `POST /accounts/{id}/cards` | 409 Conflict | ✅ |
| CP-033 | Manejo de error por cuenta inexistente en creación | ID de cuenta no existe en la base de datos | `POST /accounts/{99}/cards` | 404 Not Found | ✅ |
| CP-034 | Validación de datos de tarjeta | Campos requeridos ausentes, formatos inválidos (número de tarjeta, CVV, fecha expiración) | `POST /accounts/{id}/cards` | 400 Bad Request | ✅ |

### Listado de Tarjetas

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-035 | Listado exitoso de tarjetas existentes | Cuenta tiene al menos una tarjeta registrada, token autorizado | `GET /accounts/{id}/cards` | 200 OK | ✅ |
| CP-036 | Listado de tarjetas con colección vacía | Cuenta existe pero no tiene tarjetas asociadas, token autorizado | `GET /accounts/{id}/cards` | 200 OK (array vacío) | ✅ |
| CP-037 | Manejo de error por cuenta inexistente en listado | ID de cuenta no existe en la base de datos | `GET /accounts/{99}/cards` | 404 Not Found | ✅ |
| CP-038 | Rechazo de listado con token no autorizado | Token válido pero perteneciente a usuario sin permisos sobre la cuenta | `GET /accounts/{id}/cards` | 403 Forbidden | ✅ |
| CP-039 | Listado de tarjetas con privilegios de administrador | Token de usuario con rol admin, cuenta válida | `GET /accounts/{id}/cards` | 200 OK | ✅ |
| CP-040 | Rechazo de listado sin autenticación | Request sin header Authorization o con token ausente | `GET /accounts/{id}/cards` | 401 Unauthorized | ✅ |

### Consulta de Tarjeta Específica

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-041 | Consulta exitosa de tarjeta por IDs | Cuenta y tarjeta existen, la tarjeta pertenece a la cuenta, token autorizado | `GET /accounts/{id}/cards/{id}` | 200 OK | ✅ |
| CP-042 | Rechazo de consulta con token no autorizado | Token válido pero sin permisos sobre la cuenta propietaria de la tarjeta | `GET /accounts/{id}/cards/{id}` | 403 Forbidden | ✅ |
| CP-043 | Manejo de error por cuenta inexistente | ID de cuenta no existe en la base de datos | `GET /accounts/{99}/cards/{id}` | 404 Not Found | ✅ |
| CP-044 | Manejo de error por tarjeta inexistente | ID de tarjeta no existe en la cuenta especificada | `GET /accounts/{id}/cards/{99}` | 404 Not Found | ✅ |
| CP-045 | Rechazo de consulta sin autenticación | Request sin header Authorization o con token ausente | `GET /accounts/{id}/cards/{id}` | 401 Unauthorized | ✅ |

### Eliminación de Tarjetas

| ID | Caso de Prueba | Precondiciones | Endpoint | Resultado Esperado | Estado |
|:---|:---|:---|:---|:---|:---|
| CP-046 | Eliminación exitosa de tarjeta | Tarjeta existe y pertenece a la cuenta, token autorizado | `DELETE /accounts/{id}/cards/{id}` | 204 No Content | ✅ |
| CP-047 | Rechazo de eliminación con token no autorizado | Token válido pero sin permisos sobre la cuenta propietaria de la tarjeta | `DELETE /accounts/{id}/cards/{id}` | 403 Forbidden | ✅ |
| CP-048 | Manejo de error por tarjeta inexistente en eliminación | ID de tarjeta no existe en la cuenta especificada | `DELETE /accounts/{id}/cards/{99}` | 404 Not Found | ✅ |
| CP-049 | Rechazo de eliminación sin autenticación | Request sin header Authorization o con token ausente | `DELETE /accounts/{id}/cards/{id}` | 401 Unauthorized | ✅ |

---

Hecho con ☕🫘