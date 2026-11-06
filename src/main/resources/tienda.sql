CREATE SCHEMA IF NOT EXISTS tienda_db;
USE tienda_db;

CREATE TABLE IF NOT EXISTS roles(
  id int primary key not null auto_increment,
  name varchar(50) not null
);

CREATE TABLE IF NOT EXISTS users(
  id int primary key not null auto_increment,
  username varchar(50) not null unique,
  email varchar(255) not null unique,
  password varchar(255) not null,
  active boolean not null default true,
  rol_id int not null,
  foreign key (rol_id) references roles (id)
);

CREATE TABLE IF NOT EXISTS categories(
  id int primary key not null auto_increment,
  name varchar(100) not null unique
);

CREATE TABLE IF NOT EXISTS products(
  id int primary key not null auto_increment,
  name varchar(150) not null,
  description varchar(500),
  price decimal(12,2) not null,
  stock int not null,
  category_id int not null,
  seller_id int not null,
  foreign key (category_id) references categories (id),
  foreign key (seller_id) references users (id)
);

CREATE TABLE IF NOT EXISTS orders(
  id int primary key not null auto_increment,
  client_id int not null,
  product_id int not null,
  quantity int not null,
  total decimal(12,2) not null,
  status varchar(20) not null default 'PENDIENTE',
  created_at datetime not null default current_timestamp,
  foreign key (client_id) references users (id),
  foreign key (product_id) references products (id)
);

INSERT IGNORE INTO roles (id, name) VALUES
  (1, 'ADMIN'),
  (2, 'CLIENTE'),
  (3, 'VENDEDOR');

-- Usuario inicial: admin / password. Cambiar la contraseña despues del primer acceso.
INSERT INTO users (username, email, password, active, rol_id)
VALUES ('admin', 'admin@tienda.com', '$2a$10$oyJ7CwE603ryZPEjeOl2J.4pH8YtumpSdhgjfq5CyDTd1cvqB23OO', true, 1)
ON DUPLICATE KEY UPDATE password = VALUES(password), active = true, rol_id = 1;
