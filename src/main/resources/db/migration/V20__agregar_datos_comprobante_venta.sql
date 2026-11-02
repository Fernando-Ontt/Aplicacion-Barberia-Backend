ALTER TABLE venta
    ADD COLUMN IF NOT EXISTS serie_comprobante VARCHAR(10),
    ADD COLUMN IF NOT EXISTS numero_comprobante VARCHAR(20),
    ADD COLUMN IF NOT EXISTS subtotal DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS igv DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS total DECIMAL(10,2),
    ADD COLUMN IF NOT EXISTS fecha_emision TIMESTAMP;

UPDATE venta
SET
    serie_comprobante = CASE
                            WHEN tipo_comprobante = 'FACTURA' THEN 'F001'
                            ELSE 'B001'
        END,
    numero_comprobante = LPAD(id_venta::TEXT, 8, '0'),
    subtotal = COALESCE(subtotal, total, 0),
    igv = COALESCE(igv, 0),
    total = COALESCE(total, 0),
    fecha_emision = COALESCE(fecha_emision, fecha, CURRENT_TIMESTAMP)
WHERE serie_comprobante IS NULL;

ALTER TABLE venta
    ALTER COLUMN serie_comprobante SET NOT NULL,
ALTER COLUMN numero_comprobante SET NOT NULL,
    ALTER COLUMN subtotal SET NOT NULL,
    ALTER COLUMN igv SET NOT NULL,
    ALTER COLUMN total SET NOT NULL,
    ALTER COLUMN fecha_emision SET NOT NULL;

ALTER TABLE venta
    ADD CONSTRAINT uk_venta_serie_numero
        UNIQUE (serie_comprobante, numero_comprobante);

ALTER TABLE venta
    ADD CONSTRAINT chk_venta_montos_no_negativos
        CHECK (subtotal >= 0 AND igv >= 0 AND total >= 0);