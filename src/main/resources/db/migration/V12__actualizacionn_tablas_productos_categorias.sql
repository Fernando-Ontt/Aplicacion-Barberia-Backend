-- Cambios en categoria
ALTER TABLE categoria
ADD COLUMN descripcion TEXT;

ALTER TABLE categoria
ADD COLUMN estado BOOLEAN DEFAULT true;

-- Cambios en producto
ALTER TABLE producto
ADD COLUMN descripcion TEXT;

ALTER TABLE producto
ADD COLUMN publicado_ecommerce BOOLEAN;

-- Nueva tabla
CREATE TABLE producto_multimedia(
    id_producto INT,
    url_recurso VARCHAR(255),
    PRIMARY KEY (id_producto, url_recurso),
    FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

ALTER TABLE categoria
ADD COLUMN tipo VARCHAR(20);

UPDATE categoria
SET tipo = 'PRODUCTO'
WHERE tipo IS NULL;

ALTER TABLE categoria
ADD CONSTRAINT chk_categoria_tipo
CHECK (tipo IN ('PRODUCTO', 'SERVICIO'));

ALTER TABLE categoria
ALTER COLUMN tipo SET NOT NULL;

CREATE INDEX idx_categoria_padre ON categoria(id_padre);