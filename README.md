# Franchises
API REST en Spring Boot para gestionar franquicias, sucursales y productos con control de stock.

## Ejecución con Docker

Requisito: Docker Desktop. El `docker-compose.yml` ya trae todo lo que usan los contenedores.

```bash
docker compose up --build
```

- API: `http://localhost:8080`
- MySQL: `localhost:3307` (base `franchises`, usuario `franchises`, contraseña `franchises`)

La primera ejecución descarga las imágenes y tarda unos minutos. La app espera a que MySQL esté listo antes de arrancar, y las tablas se crean automáticamente.

Para detenerlo, presiona `Ctrl + C` en la terminal donde está corriendo, o ejecuta desde otra terminal:

```bash
docker compose down      # detiene y elimina los contenedores, conserva los datos
docker compose down -v   # además borra los datos de MySQL
```

## Documentación de la API

Con la aplicación en ejecución, la documentación interactiva de todos los endpoints está disponible en Swagger UI:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificación OpenAPI (JSON): `http://localhost:8080/v3/api-docs`

Cada endpoint muestra su descripción, el cuerpo esperado con un ejemplo, los posibles errores y el formato de respuesta. Con el botón **Try it out** se puede probar directamente desde el navegador.

## Pruebas

Requisito: Java 17. No se necesita MySQL ni Docker: los tests usan una base H2 en memoria.

```bash
./mvnw verify
```

Ejecuta todos los tests y verifica que la cobertura de líneas y ramas sea del 100 %; si baja, el build falla. El reporte de cobertura queda en `target/site/jacoco/index.html`.
