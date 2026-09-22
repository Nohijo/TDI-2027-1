-- Practica 2 - Tecnologias para Desarrollos en Internet.
-- Ejecutar una vez en MySQL Server 8.0 antes de correr el proyecto
-- (por ejemplo desde MySQL Workbench o `mysql -u root -p < esquema.sql`).

CREATE DATABASE IF NOT EXISTS libreria_online
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE libreria_online;

CREATE TABLE IF NOT EXISTS libros (
    id     INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    autor  VARCHAR(150) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL
);

-- Datos de ejemplo para que el catalogo no arranque vacio.
INSERT INTO libros (nombre, autor, precio) VALUES
    ('Cien anios de soledad', 'Gabriel Garcia Marquez', 259.00),
    ('1984', 'George Orwell', 189.90),
    ('El Principito', 'Antoine de Saint-Exupery', 99.50),
    ('Rayuela', 'Julio Cortazar', 329.00);

-- Usuario para la aplicacion (coincide con src/main/resources/db.properties).
-- En una instalacion nueva de MySQL 8.0, 'root'@'localhost' suele usar el
-- plugin auth_socket, que solo deja entrar al usuario del sistema operativo
-- llamado "root" -- por eso la app no debe conectarse como root, sino con
-- este usuario dedicado con contrasena.
CREATE USER IF NOT EXISTS 'libreria_app'@'localhost'
    IDENTIFIED WITH mysql_native_password BY 'libreria_pass';
GRANT ALL PRIVILEGES ON libreria_online.* TO 'libreria_app'@'localhost';
FLUSH PRIVILEGES;
