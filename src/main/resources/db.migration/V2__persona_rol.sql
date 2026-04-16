
CREATE TABLE persona (
                         id_persona SERIAL PRIMARY KEY,
                         nombre VARCHAR(100),
                         apellido VARCHAR(100),
                         telefono VARCHAR(20),
                         email VARCHAR(150)
);

CREATE TABLE rol (
                     id_rol SERIAL PRIMARY KEY,
                     nombre VARCHAR(50)
);