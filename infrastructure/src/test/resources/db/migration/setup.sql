create database mango;

create table mango.key_values(
    `key` varchar(255) not null primary key,
    `value` varchar(255) not null,
    unique (`key`)
);

create table mango.orders(
    `id` bigint unsigned primary key auto_increment,
    `uuid` varchar(36) unique not null,
    `status` varchar(50) not null,
    `price` decimal unsigned not null,
    `created_at` timestamp not null default CURRENT_TIMESTAMP,
    `updated_at` timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
);

create table mango.order_items(
    `id` bigint unsigned primary key auto_increment,
    `uuid` varchar(36) unique not null,
    `order_id` bigint not null,
    `price` decimal unsigned not null,
    `created_at` timestamp not null default CURRENT_TIMESTAMP,
    `updated_at` timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
);

create table mango.products(
    `id` bigint unsigned primary key auto_increment,
    `uuid` varchar(36) unique not null,
    `price` decimal unsigned not null,
    `name` varchar(100) not null,
    `created_at` timestamp not null default CURRENT_TIMESTAMP,
    `updated_at` timestamp not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
);
