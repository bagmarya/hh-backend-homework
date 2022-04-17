drop table vacancy;
drop table employer;
drop table area;

CREATE TABLE IF NOT EXISTS area (
    area_id INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS employer (
    employer_id  INTEGER PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    area_id INTEGER,
    comment text,
    views_count INTEGER default 1,
    date_create timestamp DEFAULT now(),
    FOREIGN KEY (area_id) REFERENCES area(area_id)
    );


CREATE TABLE IF NOT EXISTS vacancy (
   vacancy_id  INTEGER PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   created_at TEXT,
   area_id INTEGER,
   employer_id INTEGER,
   comment TEXT,
   views_count INTEGER default 1,
   date_create timestamp DEFAULT now(),
   salary_from INTEGER,
   salary_to INTEGER,
   salary_gross boolean,
   salary_currency VARCHAR(4),
   FOREIGN KEY (area_id) REFERENCES area(area_id),
   FOREIGN KEY (employer_id) REFERENCES employer(employer_id)
);

