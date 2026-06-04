drop table if exists revocation;
drop table if exists refresh_index;
drop table if exists clients;
drop table if exists users;
drop type if exists token_type;

create table if not exists users (
    username varchar(255) primary key,
    password_hash varchar(255) not null,
    info text
);

create table if not exists clients (
    client_id varchar(255) primary key,
    secret_hash varchar(255) not null,
    info text
);

create table if not exists refresh_index (
    refresh_id uuid primary key,
    username varchar(255) references users(username),
    client_id varchar(255) references clients(client_id),
    exp timestamp,
    rotated boolean default false
);

create type token_type as enum ('AT', 'RT');

create table if not exists revocation (
    type token_type not null ,
    token_id uuid not null,
    revoked_at timestamp default current_timestamp,
    primary key (type, token_id)
);