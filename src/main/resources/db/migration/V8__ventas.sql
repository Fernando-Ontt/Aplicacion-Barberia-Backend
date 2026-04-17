

CREATE TABLE venta (
                       id_venta SERIAL PRIMARY KEY,
                       id_cliente INT,
                       id_barbero INT,
                       fecha TIMESTAMP,
                       FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                       FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero)
);

CREATE TABLE detalle_venta (
                               id_detalle_venta SERIAL PRIMARY KEY,
                               id_venta INT,
                               id_producto INT,
                               cantidad INT,
                               precio_unitario DECIMAL(10,2),
                               FOREIGN KEY (id_venta) REFERENCES venta(id_venta),
                               FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE historial_venta (
                                 id_historial_venta SERIAL PRIMARY KEY,
                                 id_venta INT,
                                 fecha TIMESTAMP,
                                 FOREIGN KEY (id_venta) REFERENCES venta(id_venta)
);