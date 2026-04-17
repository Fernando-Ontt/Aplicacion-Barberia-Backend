
CREATE TABLE categoria (
                           id_categoria SERIAL PRIMARY KEY,
                           nombre VARCHAR(100),
                           id_padre int  NULL ,
                           FOREIGN KEY (id_padre) REFERENCES categoria(id_categoria)
);

CREATE TABLE cortes (
                        id_corte SERIAL PRIMARY KEY,
                        nombre VARCHAR(100),
                        precio DECIMAL(10,2),
                        id_categoria INT,
                        FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

CREATE TABLE producto (
                          id_producto SERIAL PRIMARY KEY,
                          nombre VARCHAR(100),
                          precio DECIMAL(10,2),
                          stock INT,
                          id_categoria INT,
                          estado BOOLEAN,
                          FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);