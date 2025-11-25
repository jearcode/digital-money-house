# 🏦 Digital Money House - Backend Microservices

Backend de la billetera virtual **Digital Money House**. Este proyecto implementa una arquitectura de microservicios robusta y escalable utilizando **Spring Boot 3**, **Spring Cloud**, **Keycloak (OAuth2/OIDC)** y **Docker**.

---

![architecture_diagram](docs/architecture-diagram.png)


---

## 🚀 Tecnologías Principales

* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3.5.8
* **Microservicios:**
    * `api-gateway`: Puerta de enlace (Spring Cloud Gateway).
    * `eureka-server`: Descubrimiento de servicios (Netflix Eureka).
    * `auth-service`: Autenticación y Proxy de Seguridad.
    * `user-service`: Gestión de usuarios y orquestación de registro.
    * `account-service`: Gestión de cuentas bancarias y saldos.
* **Seguridad:** Keycloak (Identity & Access Management) + OAuth2 (Resource Server).
* **Base de Datos:** MySQL 8.0 (Dockerizado con volúmenes persistentes).
* **Infraestructura:** Docker & Docker Compose.
* **Documentación:** OpenAPI 3 (Swagger).

---

## 🛠️ Requisitos Previos

Antes de empezar, debe tener instalado:

1.  **Java JDK 21+**
2.  **Maven** (o usar el wrapper `./mvnw` incluido)
3.  **Docker Desktop** (o Docker Engine + Compose)
4.  **Git**

---

## ⚡ Guía de Inicio Rápido

Se ha automatizado la infraestructura para que no tenga que configurar nada manualmente.

### 1. Clonar el repositorio
```bash
git clone https://github.com/jearcode/digital-money-house.git
cd digital-money-house
````

### 2\. Levantar la Infraestructura (Docker)

Este comando levantará **MySQL** (creando automáticamente las bases de datos `db_users`, `db_accounts`, `db_keycloak`) y **Keycloak** (importando automáticamente el Realm y Clientes).

```bash
docker-compose up -d
```

> *⏳ **Importante:** Espere unos 60-90 segundos hasta que Keycloak termine de importar la configuración y esté listo.*

### 3\. Ejecutar los Microservicios (Java)

Puede ejecutarlos desde un IDE (IntelliJ/Eclipse) o vía terminal. El orden recomendado de arranque es:

1.  **Eureka Server** (Puerto `8761`)
2.  **API Gateway** (Puerto `8080`)
3.  **Account Service** (Puerto `8083`)
4.  **User Service** (Puerto `8082`)
5.  **Auth Service** (Puerto `8081`)

-----

## 🔗 Endpoints Principales (API Gateway)

Toda la interacción debe hacerse a través del **Puerto 8080** (Gateway).

### 📝 Registro y Autenticación

* **Registro:** `POST http://localhost:8080/users/register`
    * *Crea usuario en Keycloak + Perfil en BD + Cuenta Bancaria (CVU/Alias).*
* **Login:** `POST http://localhost:8080/auth/login`
    * *Devuelve JWT Access Token y Refresh Token.*
* **Logout:** `POST http://localhost:8080/auth/logout`
    * *Requiere `refreshToken` en el body.*

### 💰 Cuentas (Requiere Token)

* **Crear Cuenta (Interno):** `POST http://localhost:8080/accounts`
    * *Protegido: Solo accesible por Role SERVICE (Comunicación entre microservicios).*

-----

## 🖥️ Pruebas Rápidas con cURL

Puede probar el flujo completo copiando y pegando estos comandos:

### 1\. Registrar Usuario (Crea Usuario + Cuenta)

```bash
curl -X POST http://localhost:8080/users/register \
-H "Content-Type: application/json" \
-d '{
  "firstName": "Juan",
  "lastName": "Perez",
  "email": "juan@test.com",
  "password": "123",
  "dni": "12345678",
  "phone": "5551234"
}'
```

### 2\. Iniciar Sesión (Obtener Tokens)

```bash
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "juan@test.com",
  "password": "123"
}'
```

*(Debe copiar el `refresh_token` de la respuesta para el siguiente paso).*

### 3\. Cerrar Sesión (Logout)

```bash
curl -X POST http://localhost:8080/auth/logout \
-H "Content-Type: application/json" \
-d '{
  "refreshToken": "REFRESH_TOKEN"
}'
```

-----

## 📚 Documentación API (Swagger UI)

Puede explorar los endpoints visualmente en las siguientes URLs (asegúrese de tener los servicios corriendo):

* **User Service:** [http://localhost:8082/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8082/swagger-ui/index.html)
* **Auth Service:** [http://localhost:8081/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8081/swagger-ui/index.html)
* **Account Service:** [http://localhost:8083/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8083/swagger-ui/index.html)

-----

Hecho con ☕🫘