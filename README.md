# Product-ms

Setup
```bash
# Execute os comandos abaixo no MySQL para criação do DB

CREATE SCHEMA `product_ms` DEFAULT CHARACTER SET utf8 ;
CREATE USER 'product_ms'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON product_ms.* TO product_ms@'%';
