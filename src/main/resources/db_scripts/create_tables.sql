PRAGMA foreign_keys=true;

CREATE TABLE IF NOT EXISTS currency (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    sign VARCHAR(10) NOT NULL
);

CREATE INDEX IF NOT EXISTS currency_index_id ON currency(id);
CREATE UNIQUE INDEX IF NOT EXISTS currency_index_code ON currency(code);

INSERT INTO currency (code, full_name, sign) VALUES ('RUB', 'Рубль', '₽');
INSERT INTO currency (code, full_name, sign)VALUES ('EUR', 'Евро', '€');
INSERT INTO currency (code, full_name, sign) VALUES ('USD', 'Доллар', '$');
INSERT INTO currency (code, full_name, sign) VALUES ('TRY', 'Лира', '₺');
INSERT INTO currency (code, full_name, sign) VALUES ('JPY', 'Иена', '¥');
INSERT INTO currency (code, full_name, sign) VALUES ('KZT', 'Тенге', '₸');
INSERT INTO currency (code, full_name, sign) VALUES ('GBP', 'Фунт', '£');
INSERT INTO currency (code, full_name, sign) VALUES ('ILS', 'Шекель', '₪');
INSERT INTO currency (code, full_name, sign) VALUES ('CNY', 'Юань', '¥');
INSERT INTO currency (code, full_name, sign) VALUES ('AZN', 'Манат', '₼');

CREATE TABLE IF NOT EXISTS exchange_rates (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    base_currency_id INTEGER NOT NULL,
    target_currency_id INTEGER NOT NULL,
    rate DECIMAL(6,3) NOT NULL,
    CONSTRAINT fk_base_currency_id
        FOREIGN KEY(base_currency_id)
            REFERENCES currency(id),
    CONSTRAINT fk_target_currency_id
        FOREIGN KEY(target_currency_id)
            REFERENCES currency(id)
);

CREATE INDEX IF NOT EXISTS exchange_rates_index_id ON exchange_rates(id);
CREATE UNIQUE INDEX IF NOT EXISTS exchange_rates_index_base_target ON exchange_rates(base_currency_id, target_currency_id);

INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (3, 1, 90.80);
INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (1, 2, 0.0099);
INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (4, 6, 16.53);
INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (5, 7, 0.0055);
INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (3, 7, 0.78);

select e.id, b.id, b.code, b.full_name, b.sign, t.id, t.code, t.full_name, t.sign, e.rate from exchange_rates as e
 JOIN currency as b ON e.base_currency_id = b.id and e.id = ?
 JOIN currency as t ON e.target_currency_id = t.id ;

-- select e.id, (b.code || t.code) as code, (t.code || b.code) as rev_code, e.rate from exchange_rates as e
--  JOIN currency as b ON e.base_currency_id = b.id
--  JOIN currency as t ON e.target_currency_id = t.id
-- WHERE code = ? or rev_code = ?;

--склейка строк, ищет по склейке
-- select e.id, (b.code || t.code) as codex, e.rate from exchange_rates as e
--  JOIN currency as b ON e.base_currency_id = b.id
--  JOIN currency as t ON e.target_currency_id = t.id
-- WHERE codex = ?;

--обратная склейка кодов и обратный rate
-- select e.id, (t.code || b.code) as rev_codex, round((1/e.rate), 6) as rev_rate from exchange_rates as e
--   JOIN currency as b ON e.base_currency_id = b.id
--   JOIN currency as t ON e.target_currency_id = t.id
-- WHERE rev_codex = ?;


-- select e.id, b.code, t.code, e.rate from exchange_rates as e
--   JOIN currency as b ON e.base_currency_id = b.id
--   JOIN currency as t ON e.target_currency_id = t.id
-- WHERE (b.code || t.code) = 'USDRUB';

-- select e.id, b.id, b.code, b.full_name, b.sign, t.id, t.code, t.full_name, t.sign, e.rate from exchange_rates as e
--     JOIN currency as b ON e.base_currency_id = b.id
--     JOIN currency as t ON e.target_currency_id = t.id and (b.code || t.code) = ?;

-- select e.id, t.id, t.code, t.full_name, t.sign, b.id, b.code, b.full_name, b.sign, round((1/e.rate), 6) as rev_rate from exchange_rates as e
-- JOIN currency as b ON e.base_currency_id = b.id
-- JOIN currency as t ON e.target_currency_id = t.id and (t.code || b.code) = ?;
