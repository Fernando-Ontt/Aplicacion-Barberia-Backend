
CREATE TABLE permiso (
                         id_permiso SERIAL PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         descripcion VARCHAR(255)
);

CREATE TABLE rol_permiso (
                             id_rol INT NOT NULL,
                             id_permiso INT NOT NULL,

                             PRIMARY KEY (id_rol, id_permiso),

                             CONSTRAINT fk_rol
                                 FOREIGN KEY (id_rol)
                                     REFERENCES rol(id_rol)
                                     ON DELETE CASCADE,

                             CONSTRAINT fk_permiso
                                 FOREIGN KEY (id_permiso)
                                     REFERENCES permiso(id_permiso)
                                     ON DELETE CASCADE
);