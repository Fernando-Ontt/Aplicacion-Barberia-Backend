CREATE SEQUENCE seq_comprobante_b001 START WITH 1;
CREATE SEQUENCE seq_comprobante_f001 START WITH 1;

-- Si ya tienes ventas, arranca después del último número usado:
SELECT setval('seq_comprobante_b001', COALESCE((SELECT MAX(numero_comprobante::bigint) FROM venta WHERE serie_comprobante = 'B001'), 0) + 1, false);
SELECT setval('seq_comprobante_f001', COALESCE((SELECT MAX(numero_comprobante::bigint) FROM venta WHERE serie_comprobante = 'F001'), 0) + 1, false);