

CREATE TABLE usuario (
                         id_usuario SERIAL PRIMARY KEY,
                         id_persona INT,
                         qr_token VARCHAR(255),
                         usuario VARCHAR(50),
                         password VARCHAR(255),
                         id_rol INT,
                         FOREIGN KEY (id_persona) REFERENCES persona(id_persona),
                         FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);