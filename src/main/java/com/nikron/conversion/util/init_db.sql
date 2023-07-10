create database conversion_db ENCODING 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8';
create role conversion_user password 'kek123';
grant all on database conversion_db to conversion_user;
grant connect on database conversion_db to conversion_user;
create table currencies (
                            currencies_id INT GENERATED ALWAYS AS IDENTITY primary key,
                            code varchar(20) not null unique,
                            full_name varchar(100) not null,
                            sing varchar(10) not null
);

create table exchange_rates (
                                id INT GENERATED ALWAYS AS IDENTITY primary key,
                                base_currency_id int ,
                                target_currency_id int ,
                                rate decimal(6) not null,
                                CONSTRAINT fk_base_currency_id
                                    FOREIGN KEY(base_currency_id)
                                        REFERENCES currencies(currencies_id),
                                CONSTRAINT fk_target_currency_id
                                    FOREIGN KEY(target_currency_id)
                                        REFERENCES currencies(currencies_id)
);

-- insert into currencies(code, full_name, sing) values ('AUD', 'Australian dollar', 'A$');
-- insert into currencies(code, full_name, sing) values ('USD', 'US Dollar', 'USD$');
-- insert into currencies(code, full_name, sing) values ('EUR', 'Euro', '&euro;');
--
-- insert into exchange_rates(base_currency_id, target_currency_id, rate) values (1, 2, 0.99);
-- insert into exchange_rates(base_currency_id, target_currency_id, rate) values (2, 3, 0.75);
-- insert into exchange_rates(base_currency_id, target_currency_id, rate) values (1, 3, 0.5);