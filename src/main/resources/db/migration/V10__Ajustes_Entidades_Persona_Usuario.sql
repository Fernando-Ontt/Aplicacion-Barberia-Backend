
ALTER TABLE usuario
DROP CONSTRAINT IF EXISTS usuario_id_persona_fkey;

ALTER TABLE usuario
DROP COLUMN IF EXISTS id_persona;


ALTER TABLE persona
    ADD COLUMN id_usuario INT;

ALTER TABLE persona
    ADD CONSTRAINT fk_persona_usuario
        FOREIGN KEY (id_usuario)
            REFERENCES usuario(id_usuario)
            ON DELETE CASCADE;


ALTER TABLE barbero
    ADD COLUMN sueldo       DECIMAL(10,2),
    ADD COLUMN comision     DECIMAL(5,2),
    ADD COLUMN descripcion  TEXT,
    ADD COLUMN foto_url     VARCHAR(255);

