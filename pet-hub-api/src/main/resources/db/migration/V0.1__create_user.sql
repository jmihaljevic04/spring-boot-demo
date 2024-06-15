create table app_user
(
    id              bigint generated always as identity primary key,
    email           text      not null unique,
    password        text      not null,
    first_name      text      not null,
    last_name       text      not null,
    created_at      timestamp not null default current_timestamp,
    created_by      text      not null,
    last_updated_at timestamp,
    last_updated_by text,
    version         int       not null default 0
);
