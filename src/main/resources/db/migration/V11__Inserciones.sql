INSERT INTO rol (nombre) VALUES
                             ('admin'),
                             ('barbero'),
                             ('cliente');



INSERT INTO usuario (qr_token, usuario, password, id_rol) VALUES
                                                              ('token-admin-001',   'admin1',   'admin123',   1),  -- admin
                                                              ('token-barbero-001', 'juan123',  'juan123',    2),  -- barbero
                                                              ('token-barbero-002', 'luis123',  'luis123',    2),  -- barbero
                                                              ('token-cliente-001', 'carlos123','carlos123',  3),  -- cliente
                                                              ('token-cliente-002', 'ana123',   'ana123',     3);  -- cliente



INSERT INTO persona (id_usuario, nombre, apellido, telefono, email) VALUES
                                                                        (1, 'Admin',   'Sistema',  '900000000', 'admin@gmail.com'),
                                                                        (2, 'Juan',    'Perez',    '987654321', 'juan@gmail.com'),
                                                                        (3, 'Luis',    'Gomez',    '912345678', 'luis@gmail.com'),
                                                                        (4, 'Carlos',  'Ramirez',  '998877665', 'carlos@gmail.com'),
                                                                        (5, 'Ana',     'Torres',   '955443322', 'ana@gmail.com');



INSERT INTO barbero (id_persona, experiencia, fecha_ingreso, ocupado, sueldo, comision, descripcion, foto_url) VALUES
                                                                                                                   (2, 5, '2022-03-15', false, 1500.00, 10.00, 'Especialista en cortes clásicos', null),
                                                                                                                   (3, 3, '2023-07-10', false, 1200.00, 8.00,  'Especialista en degradados',      null);



INSERT INTO cliente (id_persona, fecha_registro) VALUES
                                                     (4, '2024-01-10'),
                                                     (5, '2024-02-20');