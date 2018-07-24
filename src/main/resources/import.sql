INSERT INTO rol (name) VALUES ('CLIENT_ROL'), ('ADMIN_ROL');
insert into user (id, surname, name, password, username) values (1, 'Surname admin', 'Admin', '$2a$11$xV7Qt3OIgwXg6efZQxQfUOavr/22WFK2Sm5KFEnZ/NCGZc/2TxtRm', 'admin');
insert into user_rol (usr_id, rol_id) values (1, 2);