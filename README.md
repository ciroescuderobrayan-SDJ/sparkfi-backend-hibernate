# SparkFi Backend Hibernate

Backend base para **SparkFi**, una aplicacion enfocada en **educacion financiera**. Esta hecho con Java, Maven, Hibernate/JPA y PostgreSQL.

El objetivo del proyecto es construir la **capa de persistencia** de la app: el modelo de datos, las entidades principales, los repositorios y la conexion a la base de datos. Incluye ademas un pequeno panel interactivo por consola para crear datos de prueba, listarlos y generar un reporte en PDF.

## Tecnologias usadas

- **Java 24**
- **Maven** (gestion de dependencias y build)
- **Hibernate ORM 6.6.2** (mapeo objeto-relacional)
- **Jakarta Persistence API (JPA)**
- **PostgreSQL** (driver 42.7.10)
- **Apache PDFBox 2.0.31** (generacion del reporte PDF)
- **dotenv-java 3.2.0** (lectura de variables desde un archivo `.env`)

## Requisitos previos

- JDK 24 instalado y configurado.
- Maven instalado.
- Una base de datos PostgreSQL accesible (local, en Docker o en un servicio como Prisma Postgres).

## Estructura general

```text
src/main/java/org/sparkfi
+-- Main.java                      # Panel interactivo por consola
+-- model                         # Entidades JPA (tablas)
|   +-- User.java
|   +-- Course.java
|   +-- Challenge.java
|   +-- UserProgress.java
|   +-- UserAchievement.java
|   +-- CommunityPost.java
|   +-- LoginSession.java
|   +-- UserSettings.java
+-- repository                    # Acceso a datos (patron Repository)
|   +-- Repository.java            # Interfaz generica (contrato CRUD)
|   +-- UserRepository.java
|   +-- CourseRepository.java
|   +-- ChallengeRepository.java
|   +-- CommunityPostRepository.java
|   +-- LoginSessionRepository.java
|   +-- UserAchievementRepository.java
|   +-- UserProgressRepository.java
|   +-- UserSettingsRepository.java
|   +-- impl
|       +-- GenericRepositoryImpl.java   # Implementacion CRUD reutilizable
+-- util
    +-- JpaUtil.java               # Crea el EntityManager y carga credenciales

src/main/resources/META-INF/persistence.xml   # Unidad de persistencia "sparkfiPU"
```

## Arquitectura

El proyecto sigue una separacion de responsabilidades sencilla y clara:

1. **Modelo (`model/`):** clases Java anotadas con JPA que representan las tablas de la base de datos.
2. **Repositorios (`repository/`):** encapsulan el acceso a datos usando el **patron Repository**.
3. **Utilidad (`util/JpaUtil.java`):** centraliza la creacion del `EntityManagerFactory` y carga las credenciales de forma segura.
4. **Aplicacion (`Main.java`):** un menu por consola que usa los repositorios para operar sobre los datos.

### Patron Repository

El acceso a datos esta diseniado para evitar codigo repetido:

- `Repository<T, ID>` es una **interfaz generica** que define el contrato CRUD: `save`, `findById`, `findAll`, `update` y `delete`.
- `GenericRepositoryImpl<T, ID>` implementa ese contrato **una sola vez** para cualquier entidad, e incluye el manejo de transacciones (`begin` / `commit` y `rollback` automatico ante errores).
- Cada repositorio concreto (por ejemplo `UserRepository`) solo extiende la implementacion generica indicando su entidad. Asi no hay que reescribir el CRUD para cada tabla.

```java
public class UserRepository extends GenericRepositoryImpl<User, Long> {
    public UserRepository(EntityManager entityManager) {
        super(entityManager, User.class);
    }
}
```

## Entidades principales

| Entidad           | Tabla               | Descripcion                                              | Relaciones                          |
|-------------------|---------------------|----------------------------------------------------------|-------------------------------------|
| `User`            | `users`             | Datos basicos del usuario (nombre, email, hash de clave).| -                                   |
| `Course`          | `courses`           | Cursos de la plataforma (titulo, duracion, nivel).       | -                                   |
| `Challenge`       | `challenges`        | Retos financieros (meta de ahorro, duracion, nivel).     | -                                   |
| `UserProgress`    | `user_progress`     | Progreso de un usuario en un curso (% y si lo completo). | `ManyToOne` a `User` y a `Course`   |
| `UserAchievement` | `user_achievements` | Logros obtenidos por el usuario.                         | `ManyToOne` a `User`                |
| `CommunityPost`   | `community_posts`   | Publicaciones de la comunidad.                           | `ManyToOne` a `User`                |
| `LoginSession`    | `login_sessions`    | Sesiones de inicio de sesion (token, fechas, estado).    | `ManyToOne` a `User`                |
| `UserSettings`    | `user_settings`     | Preferencias (moneda, modo oscuro, idioma, notificaciones). | `OneToOne` con `User`            |

Todas las entidades estan registradas en `persistence.xml` dentro de la unidad de persistencia `sparkfiPU`.

## Configuracion de la base de datos

Las credenciales **no** estan escritas en el codigo. `JpaUtil` las busca primero en las **variables de entorno del sistema** y, si no las encuentra, las lee de un archivo **`.env`** local (gracias a dotenv-java). Si falta alguna, la aplicacion falla con un mensaje claro.

Variables necesarias:

```text
SPARKFI_DB_URL
SPARKFI_DB_USER
SPARKFI_DB_PASSWORD
```

Copia `.env.example` como `.env` y reemplaza los valores por tus credenciales reales. El archivo `.env` esta ignorado en `.gitignore`, asi que no se sube a GitHub.

> **Importante:** nunca pongas credenciales reales en `.env.example`, porque ese archivo **si** se sube al repositorio. Usa siempre valores de ejemplo.

Ejemplo de `.env`:

```text
SPARKFI_DB_URL=jdbc:postgresql://localhost:5432/sparkfi
SPARKFI_DB_USER=postgres
SPARKFI_DB_PASSWORD=tu_password
```

Tambien puedes definir las variables directamente en PowerShell:

```powershell
$env:SPARKFI_DB_URL="jdbc:postgresql://localhost:5432/sparkfi"
$env:SPARKFI_DB_USER="postgres"
$env:SPARKFI_DB_PASSWORD="tu_password"
```

La URL JDBC puede variar segun donde este la base de datos. Para servicios en la nube suele requerir `?sslmode=require`.

## Como compilar y ejecutar

Clonar el proyecto:

```bash
git clone https://github.com/ciroescuderobrayan-SDJ/sparkfi-backend-hibernate.git
cd sparkfi-backend-hibernate
```

Configurar las variables de entorno (o el archivo `.env`) y compilar con Maven:

```bash
mvn clean compile
```

Ejecutar el panel interactivo por consola:

```bash
mvn exec:java -Dexec.mainClass="org.sparkfi.Main"
```

## Panel interactivo

Al ejecutar `Main`, se muestra un menu con estas opciones:

```text
===== SPARKFI BACKEND =====
1. Crear datos de prueba
2. Listar usuarios
3. Listar cursos
4. Listar retos
5. Generar reporte PDF
6. Limpiar datos de prueba
0. Salir
```

- **Crear datos de prueba:** inserta usuarios, 10 cursos de ejemplo, un reto, una sesion, una publicacion, un logro, varios progresos y la configuracion de un usuario.
- **Listar:** muestra por consola los usuarios, cursos o retos guardados.
- **Generar reporte PDF:** crea `sparkfi-report.pdf` con un resumen general, los cursos y retos disponibles, y el progreso por usuario. El PDF tiene estilos visuales (cabecera de color, tarjetas de resumen y estados en color verde/naranja).
- **Limpiar datos de prueba:** elimina todos los registros (pide confirmacion escribiendo `SI`). Borra en el orden correcto para respetar las relaciones entre tablas.

## Notas del proyecto

- `target/` y los `*.class` no se suben porque los genera Maven.
- `.env` esta ignorado para no exponer credenciales.
- `sparkfi-report.pdf` esta ignorado porque lo genera la aplicacion.
- La configuracion de VS Code (`.vscode/`) tambien esta ignorada.
- Hibernate usa `hibernate.hbm2ddl.auto=update`, asi que crea y actualiza las tablas automaticamente segun las entidades durante el desarrollo.
- `hibernate.show_sql=true` y `format_sql=true` muestran en consola el SQL que ejecuta Hibernate (util para aprender y depurar).

## Estado actual

El proyecto esta **en desarrollo**. Ya cuenta con:

- Las 8 entidades principales con sus relaciones.
- Conexion configurada para PostgreSQL con carga segura de credenciales.
- Un patron Repository generico reutilizable y repositorios por entidad.
- Un panel interactivo por consola que inserta datos de ejemplo y genera un reporte PDF.

Los siguientes pasos naturales serian aniadir una capa de servicios, validaciones, pruebas y, eventualmente, exponer una API REST.
