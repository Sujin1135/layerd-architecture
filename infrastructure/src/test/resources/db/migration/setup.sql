create database mango;

create table mango.key_values(
    `key` varchar(255) not null primary key,
    `value` varchar(255) not null,
    unique (`key`)
);
