create table customer (
    customer_id uuid not null primary key,
    name varchar(255) not null
);

create table gasto (
    gasto_id uuid not null primary key,
    description varchar(255) not null,
    date_time timestamp with time zone not null,
    amount numeric(19,2) not null,
    currency varchar(3) not null,
    tags varchar(255) null,
    customer_id uuid not null,
    CONSTRAINT customerid_fkey FOREIGN KEY (customer_id) references customer (customer_id)
);