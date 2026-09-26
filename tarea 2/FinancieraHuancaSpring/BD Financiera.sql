-- Tarea 2 - Tecnologias para Desarrollos en Internet.
--
-- Crea la base de datos donde la aplicacion guarda las solicitudes de credito.
-- Ejecutar UNA sola vez antes de correr el proyecto:
--
--     mysql -u root -p < "BD Financiera.sql"
--
-- (en Linux, con una instalacion nueva de MySQL 8:  sudo mysql -u root < ... )

CREATE DATABASE IF NOT EXISTS financiera_huanca
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE financiera_huanca;

DROP TABLE IF EXISTS `creditos`;

CREATE TABLE `creditos` (
  `id`                int(11)        NOT NULL AUTO_INCREMENT,
  -- Datos personales (pestana "Datos personales" del formulario)
  `nombres`           varchar(100)   NOT NULL,
  `apellidos`         varchar(100)   NOT NULL,
  `dni`               varchar(8)     NOT NULL,
  `correo`            varchar(150)   DEFAULT NULL,
  -- Datos del credito (pestana "Datos del credito")
  `fecha`             date           NOT NULL,
  `moneda`            varchar(10)    NOT NULL,
  `monto`             decimal(12,2)  NOT NULL,
  `periodo`           int(11)        NOT NULL,
  `tea`               decimal(6,2)   NOT NULL,
  -- Lo que calcula la aplicacion
  `cuota`             decimal(12,2)  NOT NULL,
  `fecha_vencimiento` date           NOT NULL,
  -- Momento en que se registro la solicitud
  `registrado`        timestamp      DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Usuario de la aplicacion (coincide con src/servicio.properties).
-- En una instalacion nueva de MySQL 8, 'root'@'localhost' usa el plugin
-- auth_socket, que solo deja entrar al usuario del sistema operativo llamado
-- "root" -- por eso la aplicacion no se conecta como root, sino con este.
CREATE USER IF NOT EXISTS 'huanca_app'@'localhost'
    IDENTIFIED WITH mysql_native_password BY 'huanca_pass';
GRANT ALL PRIVILEGES ON financiera_huanca.* TO 'huanca_app'@'localhost';
FLUSH PRIVILEGES;
