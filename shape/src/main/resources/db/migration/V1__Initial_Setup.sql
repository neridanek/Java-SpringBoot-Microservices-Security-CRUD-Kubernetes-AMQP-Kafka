

-- Create user table

CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       login VARCHAR(255) NOT NULL,
                       lastLoginDate TIMESTAMP,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(255) NOT NULL
);

-- Create table shapes

CREATE TABLE shapes (
                        id SERIAL PRIMARY KEY,
                        type VARCHAR(255) NOT NULL,
                        createdAt DATE,
                        created_by BIGINT,
                        parameters DOUBLE PRECISION[],
                        area double precision,
                        lastModifiedAt DATE,
                        lastModifiedBy VARCHAR(255),
                        perimeter double precision,
                        version BIGINT
);
-- Add the List of Shapes column to the users table
ALTER TABLE users ADD COLUMN shapes JSONB;
ALTER TABLE users ALTER COLUMN shapes SET DATA TYPE JSONB USING shapes::JSONB;

-- Add check condition for types enum
ALTER TABLE shapes ADD CONSTRAINT shapes_type_check CHECK (type IN ('CIRCLE', 'RECTANGLE', 'SQUARE'));

-- Create shapes-users relationship
ALTER TABLE shapes
    ADD CONSTRAINT fk_created_by
        FOREIGN KEY (created_by)
            REFERENCES users (id);

-- Create squares table
CREATE TABLE squares (
    id SERIAL PRIMARY KEY,
    CONSTRAINT squares_fk FOREIGN KEY (id) REFERENCES shapes (id)
);
-- Create random user
INSERT INTO users (login, lastLoginDate, password, role, shapes)
VALUES (
           'Wiktor',
           '2023-05-06 10:30:00',
           'asd',
           'CREATOR',
           '[{"type": "SQUARE", "parameters": [4.0], "area": 100.0, "perimeter": 40.0}]'
       );
-- Create random square
INSERT INTO shapes (type, created_by, createdAt, parameters, area, perimeter, version)
VALUES ('SQUARE', 1, '2023-05-05', ARRAY[4.0], 100.0, 40.0, 1);


-- Create rectangles table
CREATE TABLE rectangles (
 id SERIAL PRIMARY KEY,
CONSTRAINT rectangles_fk FOREIGN KEY (id) REFERENCES shapes (id)
);
-- Create random rectangle
INSERT INTO shapes (type, created_by, createdAt, parameters, area, perimeter, version)
VALUES ('RECTANGLE',1, '2023-05-05', ARRAY[5.0, 10.0], 50.0, 30.0, 1);

INSERT INTO squares (id) VALUES (1);

-- Create table circles
CREATE TABLE circles (
                         id SERIAL PRIMARY KEY,
                         CONSTRAINT circles_fk FOREIGN KEY (id) REFERENCES shapes (id)
);

-- Create random circle
INSERT INTO shapes (type, created_by, createdAt, parameters, area, perimeter, version)
VALUES ('CIRCLE', 1, '2023-05-05', ARRAY[5.0], 78.53981633974483, 31.41592653589793, 1);



