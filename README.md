# SparkFi Backend Hibernate

Backend base para SparkFi, hecho con Java, Maven, Hibernate/JPA y PostgreSQL.

La idea de este proyecto es ir construyendo la capa de persistencia de una app enfocada en educacion financiera. Por ahora el repositorio se centra en el modelo de datos, las entidades principales y la configuracion inicial para conectarse a una base de datos PostgreSQL usando Hibernate.

## Tecnologias usadas

- Java 24
- Maven
- Hibernate ORM 6.6.2
- Jakarta Persistence API
- PostgreSQL

## Estructura general

```text
src/main/java/org/sparkfi
+-- Main.java
+-- model
|   +-- Achievement.java
|   +-- Challenge.java
|   +-- CommunityPost.java
|   +-- Course.java
|   +-- LoginSession.java
|   +-- User.java
|   +-- UserProgress.java
|   +-- UserSettings.java
+-- util
    +-- JpaUtil.java
```

Tambien se incluye el archivo `persistence.xml`, donde se registra la unidad de persistencia `sparkfiPU` y las entidades que Hibernate debe manejar.

## Entidades principales

El proyecto actualmente tiene estas entidades:

- `User`: informacion basica del usuario.
- `Course`: cursos disponibles dentro de la plataforma.
- `UserProgress`: progreso de un usuario en un curso.
- `LoginSession`: sesiones de inicio de sesion.
- `Challenge`: retos financieros.
- `CommunityPost`: publicaciones de comunidad.
- `Achievement`: logros obtenidos por los usuarios.
- `UserSettings`: preferencias de configuracion del usuario.

## Configuracion de base de datos

Las credenciales no estan escritas directamente en el codigo. `JpaUtil` las toma desde variables de entorno:

```text
SPARKFI_DB_URL
SPARKFI_DB_USER
SPARKFI_DB_PASSWORD
```

Ejemplo en PowerShell:

```powershell
$env:SPARKFI_DB_URL="jdbc:postgresql://localhost:5432/sparkfi"
$env:SPARKFI_DB_USER="postgres"
$env:SPARKFI_DB_PASSWORD="tu_password"
```

La URL puede cambiar dependiendo de si la base de datos esta local, en Docker o en un servicio externo.

## Como ejecutar o compilar

Primero hay que clonar el proyecto:

```bash
git clone https://github.com/ciroescuderobrayan-SDJ/sparkfi-backend-hibernate.git
cd sparkfi-backend-hibernate
```

Despues se configuran las variables de entorno de PostgreSQL y se compila con Maven:

```bash
mvn clean compile
```

Si todo esta bien configurado, Maven deberia descargar las dependencias y compilar el proyecto sin errores.

## Notas del proyecto

- `target/` no se sube al repositorio porque es generado por Maven.
- `.env` esta ignorado para evitar subir credenciales.
- La configuracion de VS Code tambien esta ignorada.
- Hibernate esta configurado con `hibernate.hbm2ddl.auto=update`, asi que puede actualizar las tablas segun las entidades durante el desarrollo.

## Estado actual

Este proyecto todavia esta en desarrollo. La base ya tiene las entidades principales y la conexion preparada para PostgreSQL. Los siguientes pasos naturales serian agregar repositorios o DAOs, servicios, pruebas y una forma mas completa de ejecutar la aplicacion.
