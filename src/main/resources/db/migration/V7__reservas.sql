

CREATE TABLE reservas (
                          id_reservas SERIAL PRIMARY KEY,
                          id_cliente INT,
                          id_barbero INT,
                          fecha TIMESTAMP,
                          estado VARCHAR(50),
                          tipo_reservas VARCHAR(50),
                          FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                          FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero)
);

CREATE TABLE detalle_reservas (
                                  id_detalle_reserva SERIAL PRIMARY KEY,
                                  id_reservas INT,
                                  id_corte INT,
                                  FOREIGN KEY (id_reservas) REFERENCES reservas(id_reservas),
                                  FOREIGN KEY (id_corte) REFERENCES cortes(id_corte)
);

CREATE TABLE historial_cortes (
                                  id_historial SERIAL PRIMARY KEY,
                                  id_cliente INT,
                                  id_detalle_reserva INT,
                                  fecha TIMESTAMP,
                                  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                                  FOREIGN KEY (id_detalle_reserva) REFERENCES detalle_reservas(id_detalle_reserva)
);