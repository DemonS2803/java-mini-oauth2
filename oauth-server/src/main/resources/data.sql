
insert into users (username, password_hash, info, roles)
values ('test_username', '$2a$12$xvyBUZdxI6qwph5FncLXSO1yyOk14w0JRID7okJO6ywwquT0EarsW', '', 'viewer editor'),
       ('test_reader', '$2a$12$xvyBUZdxI6qwph5FncLXSO1yyOk14w0JRID7okJO6ywwquT0EarsW', '', 'viewer');


insert into clients (client_id, secret_hash, info)
values ('test client', '$2a$12$gh99z.eQRvyJaV4Ay/D69OjeEtNThLOZu5oCYoUrUfzcP9fePSm2W', '');

