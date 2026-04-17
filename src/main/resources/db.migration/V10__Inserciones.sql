INSERT INTO persona (nombre, apellido, telefono, email)
VALUES
    ('Juan', 'Perez', '987654321', 'juan@gmail.com'),
    ('Luis', 'Gomez', '912345678', 'luis@gmail.com'),
    ('Carlos', 'Ramirez', '998877665', 'carlos@gmail.com'),
    ('Ana', 'Torres', '955443322', 'ana@gmail.com');

INSERT INTO barbero (id_persona, experiencia, fecha_ingreso, estado)
VALUES
    (1, 5, '2022-03-15', 'Activo'),
    (2, 3, '2023-07-10', 'Activo');

INSERT INTO cliente (id_persona, fecha_registro)
VALUES
    (3, '2024-01-10'),
    (4, '2024-02-20');