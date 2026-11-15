# Asados al Carbón — Prototipo Spring Boot + MySQL/MariaDB

Prototipo funcional de un sistema de **menú digital y gestión de pedidos** para el restaurante
"Asados al Carbón". Expone una **API REST** para administrar categorías, productos, variantes,
adicionales, mesas y pedidos, y guarda toda la información en **MySQL** (o **MariaDB**, como el
que trae XAMPP).

## Cumplimiento de los requisitos de la actividad

| Requerimiento | Implementación |
|---|---|
| Proyecto Spring Boot | Gradle + Spring Boot 4.1.2, Java 17 |
| Conexión a MySQL | Driver oficial `com.mysql:mysql-connector-j` (y `mariadb-java-client`) + `spring.datasource.url` en `application.properties` |
| Al menos 3 entidades relacionadas | 9 tablas relacionadas con claves foráneas: `categoria`, `producto`, `variante_producto`, `adicional`, `mesa`, `pedido`, `detalle_pedido`, `detalle_pedido_adicional`, `usuario` |
| Capa Entity | `com.restaurante.menu.entity` |
| Capa Repository | `com.restaurante.menu.repository` (JDBC con `JdbcTemplate`) |
| Capa Service | `com.restaurante.menu.service` (reglas de negocio y validaciones) |
| Capa Controller | `com.restaurante.menu.controller` (`@RestController` para la API y `@Controller` para las vistas) |
| Operaciones CRUD | CRUD completo en categorías, productos, adicionales, mesas y variantes. Pedidos: crear, consultar, listar y cambiar estado |
| API REST | Endpoints JSON bajo `/api/**` |
| Persistencia en MySQL/MariaDB | `schema.sql` + consultas JDBC contra la base `menu_digital` |
| Pruebas de endpoints | Colección Postman en `postman/` + 17 pruebas automatizadas |

## Tecnologías

- Java 17
- Spring Boot 4.1.2 (Web MVC, Security, JDBC, Thymeleaf)
- MySQL 8 (driver oficial `mysql-connector-j`) o MariaDB 10.4 de XAMPP (`mariadb-java-client`)
- Gradle
- JUnit 5 / Mockito / MockMvc
- Postman

## Arquitectura por capas

```
src/main/java/com/restaurante/menu
├── entity        Entidades del dominio (POJO)
├── repository    Acceso a datos con JdbcTemplate
├── service       Reglas de negocio y validaciones
├── controller    Endpoints REST y controladores de vistas
├── dto           Objetos de transferencia (pedido completo, menú)
└── config        Seguridad y administrador inicial
```

El flujo de una petición es siempre:

```
Controller  ->  Service  ->  Repository  ->  MySQL / MariaDB
```

## Puesta en marcha

### 1. Crear e importar la base de datos (solo la primera vez)

> **Importante:** si ya tienes la base `menu_digital` creada con datos (por ejemplo en tu XAMPP),
> **no vuelvas a ejecutar `schema.sql`**, porque borra y vuelve a crear las tablas. Salta al paso 2.

Con el servidor MySQL/MariaDB iniciado, ejecuta el script incluido:

```bash
mysql -u root -p < src/main/resources/schema.sql
```

En PowerShell:

```powershell
cmd /c "mysql -u root -p < src\main\resources\schema.sql"
```

También puedes abrir `src/main/resources/schema.sql` en MySQL Workbench o HeidiSQL y ejecutarlo.
El script:

- crea la base de datos `menu_digital`,
- crea las 9 tablas con sus claves foráneas,
- inserta categorías, productos, adicionales y mesas de ejemplo.

### 2. Configurar la conexión

Los valores por defecto están en `src/main/resources/application.properties` y apuntan al
MariaDB de XAMPP (usuario `root` sin contraseña):

```properties
spring.datasource.url=${DB_URL:jdbc:mariadb://localhost:3306/menu_digital}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=${DB_DRIVER:org.mariadb.jdbc.Driver}
```

Puedes cambiar usuario y contraseña directamente en el archivo o mediante variables de entorno:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_clave"
```

> **¿Usas MySQL 8 en lugar de MariaDB?** El proyecto también incluye el driver oficial de MySQL.
> Configura:
>
> ```powershell
> $env:DB_URL="jdbc:mysql://localhost:3306/menu_digital?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
> $env:DB_DRIVER="com.mysql.cj.jdbc.Driver"
> ```

### 3. Ejecutar la aplicación

```powershell
.\gradlew.bat bootRun
```

La aplicación queda disponible en **http://localhost:8085**.

Credenciales del administrador inicial (configurables con `MENU_ADMIN_USERNAME` y
`MENU_ADMIN_PASSWORD`):

- Usuario: `jonatan`
- Contraseña: `jonatan123`

### 4. Fotos de los productos

En **Panel → Productos** puedes subir una foto para cada plato (JPG, PNG, WEBP, GIF o AVIF,
máximo 5 MB). La vista previa se muestra antes de guardar y la carta pública la presenta como
portada de la tarjeta.

- Las imágenes se guardan en `uploads/productos/` (relativa al directorio de ejecución).
- Puedes cambiar la carpeta con la variable de entorno `MENU_UPLOADS_DIR`.
- La base de datos solo almacena la ruta pública, por ejemplo
  `/uploads/productos/3f2c...a1.png`; el campo `imagen` de la API sigue funcionando igual.
- Al reemplazar o quitar una foto, el archivo anterior se elimina automáticamente.
- La carpeta `uploads/` está en `.gitignore`; respáldala junto con la base de datos.

## API REST

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| GET | `/api/menu` | Menú público con categorías, productos y variantes | Público |
| GET | `/api/mesas/codigo/{codigo}` | Resuelve la mesa por el código del QR | Público |
| GET | `/api/categorias` | Lista categorías (`?soloActivas=true`) | ADMIN |
| GET | `/api/categorias/{id}` | Obtiene una categoría | ADMIN |
| POST | `/api/categorias` | Crea una categoría | ADMIN |
| PUT | `/api/categorias/{id}` | Actualiza una categoría | ADMIN |
| DELETE | `/api/categorias/{id}` | Elimina una categoría | ADMIN |
| GET | `/api/productos` | Lista productos (`?categoriaId=&soloDisponibles=`) | ADMIN |
| GET | `/api/productos/{id}` | Obtiene un producto | ADMIN |
| POST | `/api/productos` | Crea un producto | ADMIN |
| PUT | `/api/productos/{id}` | Actualiza un producto | ADMIN |
| PATCH | `/api/productos/{id}/disponibilidad?disponible=` | Marca disponible/agotado | ADMIN |
| DELETE | `/api/productos/{id}` | Elimina un producto | ADMIN |
| GET | `/api/adicionales` | Lista adicionales (`?categoriaId=&soloDisponibles=`) | ADMIN |
| GET | `/api/adicionales/{id}` | Obtiene un adicional | ADMIN |
| POST | `/api/adicionales` | Crea un adicional | ADMIN |
| PUT | `/api/adicionales/{id}` | Actualiza un adicional | ADMIN |
| DELETE | `/api/adicionales/{id}` | Elimina un adicional | ADMIN |
| GET | `/api/mesas` | Lista mesas | ADMIN |
| GET | `/api/mesas/{id}` | Obtiene una mesa | ADMIN |
| POST | `/api/mesas` | Crea una mesa y genera su código QR | ADMIN |
| PUT | `/api/mesas/{id}` | Actualiza una mesa | ADMIN |
| PATCH | `/api/mesas/{id}/codigo` | Regenera el código QR | ADMIN |
| DELETE | `/api/mesas/{id}` | Elimina una mesa | ADMIN |
| GET | `/api/productos/{id}/variantes` | Lista variantes de un producto | ADMIN |
| POST | `/api/productos/{id}/variantes` | Crea una variante | ADMIN |
| GET | `/api/variantes/{id}` | Obtiene una variante | ADMIN |
| PUT | `/api/variantes/{id}` | Actualiza una variante | ADMIN |
| DELETE | `/api/variantes/{id}` | Elimina una variante | ADMIN |
| POST | `/api/pedidos` | Crea un pedido (el servidor calcula precios y total) | Público |
| GET | `/api/pedidos` | Lista pedidos activos (`?estado=`) | ADMIN/COCINA/MESERO |
| GET | `/api/pedidos/{id}` | Obtiene un pedido completo | ADMIN/COCINA/MESERO |
| PATCH | `/api/pedidos/{id}/estado?estado=` | Cambia el estado del pedido | ADMIN/COCINA/MESERO |

### Autenticación de la API

- Las rutas protegidas usan **HTTP Basic** (también funciona el login por formulario del panel web).
- En Postman, la colección ya incluye autenticación Basic a nivel de colección.
- `POST /api/pedidos`, `/api/menu` y `/api/mesas/codigo/**` son públicos.

### Ejemplo con cURL

```bash
# Menú público
curl http://localhost:8085/api/menu

# Listar productos con autenticación básica
curl -u jonatan:jonatan123 http://localhost:8085/api/productos

# Crear un producto
curl -u jonatan:jonatan123 -X POST http://localhost:8085/api/productos \
  -H "Content-Type: application/json" \
  -d '{"categoriaId":1,"nombre":"Burguer Especial","descripcion":"Con tocineta","precio":18000,"disponible":true}'
```

## Reportes de ventas

En **Panel → Reportes** (`/admin/reportes`) puedes consultar:

- **Día, mes o año** con un selector de fecha y navegación al periodo anterior/siguiente.
- **Ganancias, pedidos, ticket promedio y productos vendidos**, comparados con el periodo anterior.
- **Gráfica de ingresos** por hora (día), día del mes (mes) o mes (año).
- **Ranking de los platos más vendidos** con unidades y valor generado.
- **Registro de pedidos recientes** con fecha, cliente, platos, total y estado.

Los pedidos **CANCELADO** no suman a las ganancias ni al ranking, pero sí se muestran en el
registro. No se requieren tablas nuevas: todo se calcula sobre `pedido`, `detalle_pedido` y
`detalle_pedido_adicional`.

## Pruebas

### Postman

1. Abre Postman e importa:
   - `postman/Asados-al-Carbon.postman_collection.json`
   - `postman/Asados-al-Carbon.postman_environment.json`
2. Selecciona el environment **Asados al Carbón - Local**.
3. Verifica que la aplicación esté corriendo en `http://localhost:8085`.
4. Ejecuta la colección completa con **Run collection** o carpeta por carpeta.

La colección incluye peticiones de creación, consulta, actualización y eliminación, además de
scripts que guardan los IDs creados para reutilizarlos en las siguientes peticiones. La carpeta
**Pedidos** descubre automáticamente una mesa activa, un producto disponible y un adicional válido
antes de crear el pedido, así que funciona tanto con tus datos actuales como con el `schema.sql`.

### Pruebas automatizadas

```powershell
.\gradlew.bat test
```

Las pruebas no requieren base de datos porque usan Mockito y `MockMvc`:

- `ProductoServiceTest`: validaciones y reglas de negocio (5 pruebas).
- `PedidoServiceTest`: consulta y cambio de estado (3 pruebas).
- `ProductoControllerTest`: endpoints GET, POST, PUT y DELETE (7 pruebas).
- `CategoriaControllerTest`: listado y creación (2 pruebas).

## Modelo de datos

```
categoria 1---N producto 1---N variante_producto
categoria 1---N adicional
mesa      1---N pedido   1---N detalle_pedido 1---N detalle_pedido_adicional
producto  1---N detalle_pedido
adicional 1---N detalle_pedido_adicional
usuario (independiente, para el panel administrativo)
```

## Notas

- El esquema **no** se modifica automáticamente: `spring.jpa.hibernate.ddl-auto=none` y
  `spring.sql.init.mode=never`. La base se prepara una vez con `schema.sql`.
- La contraseña de los usuarios se almacena cifrada con BCrypt.
- El panel web (Thymeleaf) está disponible en `/`, `/menu`, `/login` y `/admin/**`.

## Cambio en rama de reportes

Esta línea fue agregada desde la rama `feature/reportes` como parte del ejercicio de Git y GitHub.
