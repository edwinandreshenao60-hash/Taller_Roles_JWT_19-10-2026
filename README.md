# TiendaApp

API REST de una tienda con usuarios, roles, productos y pedidos. El proyecto usa Java 17, Spring Boot, Spring Data JPA y MySQL. La autenticación se implementa con BCrypt y JWT mediante filtro e interceptor, sin activar Spring Security completo.

## Base de datos

1. Abre src/main/resources/tienda.sql en MySQL Workbench y ejecútalo.
2. El script crea tienda_db, las tablas, los tres roles y el ADMIN inicial.
3. El script es repetible. Si ya existe admin, lo deja con la contraseña inicial de demostración.

Credenciales iniciales:

- Usuario: admin
- Contraseña: password

Es una credencial local para el taller; cámbiala en la base de datos antes de exponer la aplicación en una red.

El archivo src/main/resources/application.yaml usa MySQL en localhost:3306, usuario root y contraseña vacía. Si tu usuario MySQL tiene contraseña, escríbela en spring.datasource.password en tu copia local.

## Variables JWT y ejecución

En PowerShell, genera una clave Base64 de 32 bytes y establece la expiración de diez minutos:

    $bytes = New-Object byte[] 32
    [Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
    Set-Item Env:env.jwt.secret-key ([Convert]::ToBase64String($bytes))
    Set-Item Env:env.jwt.token-expiration "600000"

Desde la carpeta del proyecto ejecuta:

    .\mvnw.cmd spring-boot:run

La API queda en http://localhost:9090/api/v1/tienda.

## Rutas

| Grupo | Rutas |
|---|---|
| Autenticación | POST /auth/register, POST /auth/login, POST /auth/refreshtoken |
| Usuarios | GET /users/me, GET /users, POST /users, PATCH /users/{id}/role, PATCH /users/{id}/status |
| Categorías | GET /categories, POST /categories, DELETE /categories/{id} |
| Productos | GET /products, GET /products/{id}, GET /products/mine, POST /products, PUT /products/{id}, DELETE /products/{id} |
| Pedidos | POST /orders, GET /orders/mine, GET /orders/sales, GET /orders, PATCH /orders/{id}/status, PATCH /orders/{id}/cancel |

Los productos del catálogo se consultan sin token. Las demás rutas protegidas requieren Authorization: Bearer <jwt>. Los usuarios y pedidos se devuelven mediante DTO que no incluyen la contraseña.

## Postman

Importa:

- postman/TiendaApp.postman_collection.json
- postman/TiendaApp Local.postman_environment.json

La colección incluye una solicitud para cada endpoint y casos de evidencia para token ausente y expirado (401), rol no permitido (403), vendedor modificando el producto de otro vendedor (403) y pedido sin stock (400). Ejecuta primero los logins de administrador, vendedor y cliente; el script de login guarda el JWT en la variable correspondiente. La variable jwtSecret del environment debe coincidir con env.jwt.secret-key para crear un JWT expirado firmado.

La colección usa identificadores de recursos guardados en variables de entorno; crea usuarios, categorías y productos antes de ejecutar las solicitudes que dependen de ellos. Las operaciones de eliminar pueden requerir recursos de prueba sin pedidos relacionados.