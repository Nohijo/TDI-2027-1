-- Practica 3 - Tecnologias para Desarrollos en Internet.
-- Ejecutar una vez en MySQL Server 8 antes de levantar el proyecto:
--     sudo mysql -u root < esquema.sql
-- Es la misma base de la Practica 2; se puede ejecutar aunque ya exista,
-- porque no duplica los libros ni toca los que ya estan guardados.

SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS libreria_online
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE libreria_online;

CREATE TABLE IF NOT EXISTS libros (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    autor  VARCHAR(150) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL
);

-- Datos de ejemplo, solo si la tabla esta vacia.
INSERT INTO libros (nombre, autor, precio)
SELECT ejemplo.nombre, ejemplo.autor, ejemplo.precio
FROM (
    SELECT 'Cien años de soledad' AS nombre, 'Gabriel García Márquez' AS autor, 259.00 AS precio
    UNION ALL SELECT '1984', 'George Orwell', 189.90
    UNION ALL SELECT 'El Principito', 'Antoine de Saint-Exupéry', 99.50
    UNION ALL SELECT 'Rayuela', 'Julio Cortázar', 329.00
) AS ejemplo
WHERE NOT EXISTS (SELECT 1 FROM libros);

-- Usuario de la aplicacion (coincide con application.properties). En una
-- instalacion nueva de MySQL, 'root'@'localhost' suele usar auth_socket, asi
-- que la aplicacion no se conecta como root.
CREATE USER IF NOT EXISTS 'libreria_app'@'localhost' IDENTIFIED BY 'libreria_pass';
GRANT ALL PRIVILEGES ON libreria_online.* TO 'libreria_app'@'localhost';
FLUSH PRIVILEGES;
