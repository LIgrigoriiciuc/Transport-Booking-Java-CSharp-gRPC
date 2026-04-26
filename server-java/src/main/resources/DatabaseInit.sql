DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS offices;

CREATE TABLE IF NOT EXISTS offices (
                                       id      INTEGER PRIMARY KEY AUTOINCREMENT,
                                       address TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
                                     id       INTEGER PRIMARY KEY AUTOINCREMENT,
                                     username TEXT    NOT NULL UNIQUE,
                                     password TEXT    NOT NULL,
                                     fullName TEXT,
                                     officeId INTEGER,
                                     FOREIGN KEY (officeId) REFERENCES offices(id)
);

CREATE TABLE IF NOT EXISTS trips (
                                     id          INTEGER PRIMARY KEY AUTOINCREMENT,
                                     destination TEXT    NOT NULL,
                                     time        TEXT    NOT NULL,
                                     busNumber   TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
                                            id              INTEGER PRIMARY KEY AUTOINCREMENT,
                                            clientName      TEXT    NOT NULL,
                                            userId          INTEGER NOT NULL,
                                            reservationTime TEXT    NOT NULL,
                                            FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS seats (
                                     id             INTEGER PRIMARY KEY AUTOINCREMENT,
                                     number         INTEGER NOT NULL,
                                     isReserved     INTEGER NOT NULL DEFAULT 0,
                                     trip_id        INTEGER NOT NULL,
                                     reservation_id INTEGER,
                                     FOREIGN KEY (trip_id)        REFERENCES trips(id),
                                     FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);
INSERT INTO offices (address) VALUES
                                  ('12 Victory St, London'),
                                  ('5 Heroes Blvd, Manchester'),
                                  ('3 Union Square, Birmingham'),
                                  ('8 Knight St, Liverpool');

INSERT INTO users (username, password, fullName, officeId) VALUES
                                                               ('admin',  'admin123', 'Main Administrator', 1),
                                                               ('john',   'pass123',  'John Miller', 2),
                                                               ('mary',   'pass456',  'Mary Smith', 3),
                                                               ('andrew', 'pass789',  'Andrew Jones', 4);


INSERT INTO trips (destination, time, busNumber) VALUES
                                                     ('London',     '2026-06-10 08:00', 'LN-01-AAA'),
                                                     ('London',     '2026-06-10 14:30', 'LN-02-BBB'),
                                                     ('Manchester', '2026-06-11 07:00', 'MN-03-CCC'),
                                                     ('Manchester', '2026-06-12 09:15', 'MN-04-DDD'),
                                                     ('Birmingham', '2026-06-11 10:00', 'BM-05-EEE'),
                                                     ('Liverpool',  '2026-06-13 06:30', 'LV-06-FFF'),
                                                     ('Bristol',    '2026-06-14 08:45', 'BR-07-GGG'),
                                                     ('Leeds',      '2026-06-15 11:00', 'LD-08-HHH');
INSERT INTO reservations (clientName, userId, reservationTime) VALUES
                                                                   ('George Stamper', 1, '2026-06-01T10:00:00'),
                                                                   ('Ellen Brown',    1, '2026-06-02T11:30:00'),
                                                                   ('Michael Ross',   1, '2026-06-03T09:15:00'),
                                                                   ('Anne Cook',      1, '2026-06-04T14:00:00'),
                                                                   ('Ray Flowers',    1, '2026-06-05T16:45:00');

INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES
                                                                    (1,0,1,NULL),(2,0,1,NULL),(3,0,1,NULL),(4,0,1,NULL),(5,0,1,NULL),(6,0,1,NULL),
                                                                    (7,0,1,NULL),(8,0,1,NULL),(9,0,1,NULL),(10,0,1,NULL),(11,0,1,NULL),(12,0,1,NULL),
                                                                    (13,0,1,NULL),(14,0,1,NULL),(15,0,1,NULL),(16,0,1,NULL),(17,0,1,NULL),(18,0,1,NULL);

INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES
                                                                    (1,0,2,NULL),(2,0,2,NULL),(3,0,2,NULL),(4,0,2,NULL),(5,0,2,NULL),(6,0,2,NULL),
                                                                    (7,0,2,NULL),(8,0,2,NULL),(9,0,2,NULL),(10,0,2,NULL),(11,0,2,NULL),(12,0,2,NULL),
                                                                    (13,0,2,NULL),(14,0,2,NULL),(15,0,2,NULL),(16,0,2,NULL),(17,0,2,NULL),(18,0,2,NULL);

INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES
                                                                    (1,0,3,NULL),(2,0,3,NULL),(3,0,3,NULL),(4,0,3,NULL),(5,0,3,NULL),(6,0,3,NULL),
                                                                    (7,0,3,NULL),(8,0,3,NULL),(9,0,3,NULL),(10,0,3,NULL),(11,0,3,NULL),(12,0,3,NULL),
                                                                    (13,0,3,NULL),(14,0,3,NULL),(15,0,3,NULL),(16,0,3,NULL),(17,0,3,NULL),(18,0,3,NULL);

INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES
                                                                    (1,0,4,NULL),(2,0,4,NULL),(3,0,4,NULL),(4,0,4,NULL),(5,0,4,NULL),(6,0,4,NULL),
                                                                    (7,0,4,NULL),(8,0,4,NULL),(9,0,4,NULL),(10,0,4,NULL),(11,0,4,NULL),(12,0,4,NULL),
                                                                    (13,0,4,NULL),(14,0,4,NULL),(15,0,4,NULL),(16,0,4,NULL),(17,0,4,NULL),(18,0,4,NULL);

INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES
                                                                    (1,0,5,NULL),(2,0,5,NULL),(3,0,5,NULL),(4,0,5,NULL),(5,0,5,NULL),(6,0,5,NULL),
                                                                    (7,0,5,NULL),(8,0,5,NULL),(9,0,5,NULL),(10,0,5,NULL),(11,0,5,NULL),(12,0,5,NULL),
                                                                    (13,0,5,NULL),(14,0,5,NULL),(15,0,5,NULL),(16,0,5,NULL),(17,0,5,NULL),(18,0,5,NULL);

INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES
                                                                    (1,0,6,NULL),(2,0,6,NULL),(3,0,6,NULL),(4,0,6,NULL),(5,0,6,NULL),(6,0,6,NULL),
                                                                    (7,0,6,NULL),(8,0,6,NULL),(9,0,6,NULL),(10,0,6,NULL),(11,0,6,NULL),(12,0,6,NULL),
                                                                    (13,0,6,NULL),(14,0,6,NULL),(15,0,6,NULL),(16,0,6,NULL),(17,0,6,NULL),(18,0,6,NULL);

INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES
                                                                    (1,0,7,NULL),(2,0,7,NULL),(3,0,7,NULL),(4,0,7,NULL),(5,0,7,NULL),(6,0,7,NULL),
                                                                    (7,0,7,NULL),(8,0,7,NULL),(9,0,7,NULL),(10,0,7,NULL),(11,0,7,NULL),(12,0,7,NULL),
                                                                    (13,0,7,NULL),(14,0,7,NULL),(15,0,7,NULL),(16,0,7,NULL),(17,0,7,NULL),(18,0,7,NULL);

INSERT INTO seats (number, isReserved, trip_id, reservation_id) VALUES
                                                                    (1,0,8,NULL),(2,0,8,NULL),(3,0,8,NULL),(4,0,8,NULL),(5,0,8,NULL),(6,0,8,NULL),
                                                                    (7,0,8,NULL),(8,0,8,NULL),(9,0,8,NULL),(10,0,8,NULL),(11,0,8,NULL),(12,0,8,NULL),
                                                                    (13,0,8,NULL),(14,0,8,NULL),(15,0,8,NULL),(16,0,8,NULL),(17,0,8,NULL),(18,0,8,NULL);


UPDATE seats SET isReserved = 1, reservation_id = 1 WHERE trip_id = 1 AND number IN (1,2,3);
UPDATE seats SET isReserved = 1, reservation_id = 2 WHERE trip_id = 1 AND number IN (4,5);
UPDATE seats SET isReserved = 1, reservation_id = 3 WHERE trip_id = 3 AND number IN (1,2,3,4);
UPDATE seats SET isReserved = 1, reservation_id = 4 WHERE trip_id = 5 AND number IN (1,2);
UPDATE seats SET isReserved = 1, reservation_id = 5 WHERE trip_id = 2 AND number IN (10,11);