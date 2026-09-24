-- Tarea 2 - Tecnologias para Desarrollos en Internet.
--
-- Crea la base de datos donde la aplicacion de Spring guarda las solicitudes
-- de credito. Ejecutar UNA sola vez antes de correr el proyecto:
--
--     sudo mysql -u root < esquema.sql

CREATE DATABASE IF NOT EXISTS financiera_huanca
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE financiera_huanca;

CREATE TABLE IF NOT EXISTS creditos (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    -- Datos personales (pestana "Datos personales" del formulario)
    nombres           VARCHAR(100) NOT NULL,
    apellidos         VARCHAR(100) NOT NULL,
    dni               VARCHAR(8)   NOT NULL,
    correo            VARCHAR(150),
    -- Datos del credito (pestana "Datos del credito")
    fecha             DATE           NOT NULL,
    moneda            VARCHAR(10)    NOT NULL,
    monto             DECIMAL(12, 2) NOT NULL,
    periodo           INT            NOT NULL,
    tea               DECIMAL(6, 2)  NOT NULL,
    -- Resultados que calcula la aplicacion
    cuota             DECIMAL(12, 2) NOT NULL,
    fecha_vencimiento DATE           NOT NULL,
    -- Momento en que se registro la solicitud
    registrado        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Usuario para la aplicacion (coincide con src/java/db.properties).
-- En una instalacion nueva de MySQL 8.0, 'root'@'localhost' usa el plugin
-- auth_socket, que solo deja entrar al usuario del sistema operativo llamado
-- "root" -- por eso la app no se conecta como root, sino con este usuario.
CREATE USER IF NOT EXISTS 'huanca_app'@'localhost'
    IDENTIFIED WITH mysql_native_password BY 'huanca_pass';
GRANT ALL PRIVILEGES ON financiera_huanca.* TO 'huanca_app'@'localhost';
FLUSH PRIVILEGES;
