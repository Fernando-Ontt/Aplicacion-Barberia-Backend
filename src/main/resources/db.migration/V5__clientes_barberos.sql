
CREATE TABLE barbero (
                         id_barbero SERIAL PRIMARY KEY,
                         id_persona INT,
                         experiencia INT,
                         fecha_ingreso DATE,
                         estado VARCHAR(20),
                         FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
);

CREATE TABLE cliente (
                         id_cliente SERIAL PRIMARY KEY,
                         id_persona INT,
                         fecha_registro DATE,
                         FOREIGN KEY (id_persona) REFERENCES persona(id_persona)
);

CREATE TABLE recompensa (
                            id_recompensa SERIAL PRIMARY KEY,
                            id_cliente INT,
                            cortes_acumulados INT,
                            cortes_gratis INT,
                            fecha_actualizacion TIMESTAMP,
                            FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);