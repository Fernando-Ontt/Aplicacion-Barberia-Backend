

CREATE TABLE pago (
                      id_pago SERIAL PRIMARY KEY,
                      id_cliente INT,
                      id_barbero INT,
                      id_reservas INT,
                      id_venta INT,
                      monto DECIMAL(10,2),
                      metodo VARCHAR(50),
                      fecha TIMESTAMP,
                      tipo VARCHAR(50),
                      FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                      FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero),
                      FOREIGN KEY (id_reservas) REFERENCES reservas(id_reservas),
                      FOREIGN KEY (id_venta) REFERENCES venta(id_venta)
);

CREATE TABLE historial_pago (
                                id_historial_pago SERIAL PRIMARY KEY,
                                id_pago INT,
                                id_cliente INT,
                                fecha TIMESTAMP,
                                FOREIGN KEY (id_pago) REFERENCES pago(id_pago),
                                FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);