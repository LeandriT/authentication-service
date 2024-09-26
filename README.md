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
