-- Tarea 3 - Tecnologias para Desarrollos en Internet.
--
-- Crea la base de datos de la aplicacion: la tabla de solicitudes de credito
-- (la de la Tarea 2) y la tabla de pagos con tarjeta de credito (Tarea 3).
-- Ejecutar antes de correr el proyecto:
--
--     mysql -u root -p < "BD Financiera.sql"
--
-- (en Linux, con una instalacion nueva de MySQL 8:  sudo mysql -u root < ... )
--
-- Se puede volver a ejecutar: las solicitudes ya guardadas se conservan y la
-- tabla de pagos regresa a sus datos iniciales.

CREATE DATABASE IF NOT EXISTS financiera_huanca
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE financiera_huanca;

-- ---------------------------------------------------------------------------
-- Solicitudes de credito (formulario, igual que en la Tarea 2)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `creditos` (
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

-- ---------------------------------------------------------------------------
-- Pagos con tarjeta de credito (Tarea 3)
--
-- No esta relacionada con "creditos" por llave foranea: asi lo pide la
-- especificacion. Fecha, mes, tarjeta, transaccion y gastos vienen precargados
-- y en la aplicacion son de solo lectura; "pagos" empieza vacio y es la unica
-- columna que el usuario edita (se guarda al oprimir ACEPTAR).
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS `pagos_tarjeta`;

CREATE TABLE `pagos_tarjeta` (
  `id`           int(11)        NOT NULL AUTO_INCREMENT,
  `fecha`        date           NOT NULL,
  `mes`          varchar(15)    NOT NULL,
  `tarjeta`      varchar(20)    NOT NULL,
  `transaccion`  varchar(100)   NOT NULL,
  `gastos`       decimal(12,2)  DEFAULT NULL,
  `pagos`        decimal(12,2)  DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Los movimientos de la tabla del enunciado
INSERT INTO `pagos_tarjeta` (`fecha`, `mes`, `tarjeta`, `transaccion`, `gastos`) VALUES
  ('2021-01-01', 'enero',   'VISA',       'Peluquería',          1000.00),
  ('2021-01-02', 'enero',   'VISA',       'TV plasma (1 de 6)',  2500.00),
  ('2021-01-03', 'enero',   'MASTERCARD', 'Vajilla Kit',         2500.00),
  ('2021-01-04', 'enero',   'MASTERCARD', 'YouTube Premium',      170.00),
  ('2021-01-05', 'enero',   'VISA',       'Pasaje Londres',     25000.00),
  ('2021-01-06', 'enero',   'MASTERCARD', 'Pago Mastercard',         NULL),
  ('2021-02-07', 'febrero', 'VISA',       'Teatro',               150.00),
  ('2021-02-08', 'febrero', 'VISA',       'Pago VISA',               NULL),
  ('2021-02-09', 'febrero', 'MASTERCARD', 'Cine',                  23.00),
  ('2021-02-10', 'febrero', 'MASTERCARD', 'Libros x 5',          1400.00);

-- Usuario de la aplicacion (coincide con src/servicio.properties).
-- En una instalacion nueva de MySQL 8, 'root'@'localhost' usa el plugin
-- auth_socket, que solo deja entrar al usuario del sistema operativo llamado
-- "root" -- por eso la aplicacion no se conecta como root, sino con este.
CREATE USER IF NOT EXISTS 'huanca_app'@'localhost'
    IDENTIFIED WITH mysql_native_password BY 'huanca_pass';
GRANT ALL PRIVILEGES ON financiera_huanca.* TO 'huanca_app'@'localhost';
FLUSH PRIVILEGES;
