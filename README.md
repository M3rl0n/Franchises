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
