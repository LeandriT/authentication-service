# Servicio de Autenticación

Este proyecto incluye un controlador de autenticación que permite registrar usuarios y autenticar su acceso. Utiliza Spring Boot y es parte de un sistema de gestión de autenticación más amplio.

## Requisitos Previos

- **Java:** El microservicio esta desarrollado en Java, por lo que se requiere un entorno de ejecución de Java.
- **Spring Boot:** Utilizan Spring Boot como framework principal.
- **Base de Datos:** Se requiere una base de datos compatible con JPA/Hibernate para persistir los datos de usuarios, tokens.


# AuthenticationController

## Descripción

`AuthenticationController` es un controlador REST que maneja las operaciones de autenticación y registro de usuarios. Utiliza los servicios proporcionados por `AuthenticationService` para realizar las acciones correspondientes.

## Endpoints

### 1. Registro de Usuario

- **URL:** `/register`
- **Método HTTP:** `POST`
- **Descripción:** Registra un nuevo usuario en el sistema.
- **Request Body:**
    - `UserRequest`: contiene la información del usuario que se va a registrar.

- **Respuesta:**
    - **Código HTTP:** `200 OK`
    - **Cuerpo de la respuesta:** `UserResponse`, con los detalles del usuario registrado.

#### Ejemplo de Request Body:
```json
{
  "username": "usuario1",
  "password": "password123",
  "email": "usuario1@example.com"
}
```
### 2. Autenticacion de usuarios

- **URL:** `/login`
- **Método HTTP:** `POST`
- **Descripción:** Autentica a un usuario en el sistema.
- **Request Body:** 
  - `LoginRequest`: un objeto que contiene las credenciales del usuario (nombre de usuario y contraseña).
  
- **Respuesta:**
  - **Código HTTP:** `200 OK` si las credenciales son válidas.
  - **Cuerpo de la respuesta:** `TokenResponse`, que contiene el token JWT necesario para acceder a recursos protegidos del sistema.

#### Ejemplo de Request Body:
```json
{
  "username": "usuario1",
  "password": "password123"
}
```
#### Ejemplo de Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5..."
}
```


## Endpoints

### 1. URL de Login

```bash
curl --location 'https://authentication-service-dbbcfbcb9c78.herokuapp.com/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "gandhycuasapas2",
    "password": "securePassword123"
}'
```
### 2. URL de Registro
```bash
curl --location 'https://authentication-service-dbbcfbcb9c78.herokuapp.com/register' \
--header 'Content-Type: application/json' \
--data '{
"first_name": "John",
"last_name": "Doe",
"username": "gandhycuasapas2",
"password": "securePassword123",
"role": "USER"
}'
```

### 3. URL de Prueba (con Token JWT)

```bash
curl --location 'https://authentication-service-dbbcfbcb9c78.herokuapp.com/user' \
--header 'Authorization: Bearer ***'
```

Nota: Reemplaza *** por el token JWT válido que obtuviste durante el proceso de autenticación.

### Roles de Acceso
- USER
- ADMIN

El token JWT obtenido tras el login debe ser utilizado en la cabecera Authorization para acceder a los endpoints protegidos:

- /user: Requiere un token con rol USER.
- /admin: Requiere un token con rol ADMIN.