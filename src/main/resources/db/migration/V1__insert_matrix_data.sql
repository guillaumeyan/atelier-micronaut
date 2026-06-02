CREATE TABLE vaisseau
(
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       nom              VARCHAR(100) NOT NULL UNIQUE,
                       type_vaisseau    VARCHAR(30)  NOT NULL,
                       score_de_combat  INTEGER      NOT NULL DEFAULT 0,
                       date_de_creation TIMESTAMP,
                       date_de_maj      TIMESTAMP
);

CREATE TABLE personnage
(
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            nom              VARCHAR(100) NOT NULL,
                            alias VARCHAR(100),
                            role VARCHAR(30) NOT NULL,
                            vaisseau_id      UUID         REFERENCES vaisseau (id) ON DELETE SET NULL,
                            score_de_combat  INTEGER      NOT NULL DEFAULT 0,
                            date_de_creation TIMESTAMP,
                            date_de_maj      TIMESTAMP

);

CREATE INDEX idx_personnage_vaisseau_id ON personnage (vaisseau_id);

CREATE TABLE competence
(
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        nom              VARCHAR(100) NOT NULL UNIQUE,
                        description      VARCHAR(500),
                        date_de_creation TIMESTAMP,
                        date_de_maj      TIMESTAMP
);

CREATE TABLE personnage_competence
(
    personnage_id UUID NOT NULL REFERENCES personnage (id) ON DELETE CASCADE,
    competence_id UUID NOT NULL REFERENCES competence (id) ON DELETE CASCADE,
    PRIMARY KEY (personnage_id, competence_id)
);

-- vaisseau
INSERT INTO vaisseau (id, nom, type_vaisseau, score_de_combat, date_de_creation, date_de_maj)
VALUES ('a0000000-0000-0000-0000-000000000001', 'Nebuchadnezzar', 'HOVERCRAFT', 85, NOW(), NOW()),
       ('a0000000-0000-0000-0000-000000000002', 'Logos', 'HOVERCRAFT', 75, NOW(), NOW()),
       ('a0000000-0000-0000-0000-000000000003', 'Hammer', 'HOVERCRAFT', 80, NOW(), NOW());

-- competence
INSERT INTO competence (id, nom, description, date_de_creation, date_de_maj)
VALUES ('c0000000-0000-0000-0000-000000000001', 'Kung Fu', 'Advanced martial arts downloaded from the Construct', NOW(), NOW()),
       ('c0000000-0000-0000-0000-000000000002', 'Piloting', 'Hovercraft navigation through the sewers of the real world', NOW(), NOW()),
       ('c0000000-0000-0000-0000-000000000003', 'Hacking', 'Matrix code manipulation and system infiltration', NOW(), NOW()),
       ('c0000000-0000-0000-0000-000000000004', 'Weapons', 'Proficiency with firearms and heavy weapons', NOW(), NOW()),
       ('c0000000-0000-0000-0000-000000000005', 'Code Vision', 'Ability to perceive the Matrix as raw code', NOW(), NOW()),
       ('c0000000-0000-0000-0000-000000000006', 'Duplication', 'Self-replication within the Matrix', NOW(), NOW()),
       ('c0000000-0000-0000-0000-000000000007', 'Precognition', 'Foresight of future events within the Matrix', NOW(), NOW());

-- personnage
INSERT INTO personnage (id, nom, alias, role, vaisseau_id, score_de_combat, date_de_creation, date_de_maj)
VALUES ('b0000000-0000-0000-0000-000000000001', 'Thomas Anderson', 'Neo', 'HUMAIN', 'a0000000-0000-0000-0000-000000000001', 100, NOW(), NOW()),
       ('b0000000-0000-0000-0000-000000000002', 'Morpheus', NULL, 'HUMAIN', 'a0000000-0000-0000-0000-000000000001', 85, NOW(), NOW()),
       ('b0000000-0000-0000-0000-000000000003', 'Trinity', NULL, 'HUMAIN', 'a0000000-0000-0000-0000-000000000001', 80, NOW(), NOW()),
       ('b0000000-0000-0000-0000-000000000004', 'Niobe', NULL, 'HUMAIN', 'a0000000-0000-0000-0000-000000000002', 78, NOW(), NOW()),
       ('b0000000-0000-0000-0000-000000000005', 'Agent Smith', NULL, 'PROGRAMME', NULL, 95, NOW(), NOW()),
       ('b0000000-0000-0000-0000-000000000006', 'The Oracle', NULL, 'PROGRAMME', NULL, 40, NOW(), NOW()),
       ('b0000000-0000-0000-0000-000000000007', 'The Merovingian', NULL, 'EXILE', NULL, 65, NOW(), NOW());

-- Character competence (many-to-many)
INSERT INTO personnage_competence (personnage_id, competence_id)
VALUES
                                                          -- Neo
                                                          ('b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000001'),
                                                          ('b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000004'),
                                                          ('b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000005'),
                                                          ('b0000000-0000-0000-0000-000000000001', 'c0000000-0000-0000-0000-000000000003'),

                                                          -- Morpheus
                                                          ('b0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000001'),
                                                          ('b0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000004'),
                                                          ('b0000000-0000-0000-0000-000000000002', 'c0000000-0000-0000-0000-000000000002'),

                                                          -- Trinity
                                                          ('b0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000001'),
                                                          ('b0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000003'),
                                                          ('b0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000004'),
                                                          ('b0000000-0000-0000-0000-000000000003', 'c0000000-0000-0000-0000-000000000002'),

                                                          -- Niobe
                                                          ('b0000000-0000-0000-0000-000000000004', 'c0000000-0000-0000-0000-000000000002'),
                                                          ('b0000000-0000-0000-0000-000000000004', 'c0000000-0000-0000-0000-000000000004'),

                                                          -- Agent Smith
                                                          ('b0000000-0000-0000-0000-000000000005', 'c0000000-0000-0000-0000-000000000001'),
                                                          ('b0000000-0000-0000-0000-000000000005', 'c0000000-0000-0000-0000-000000000004'),
                                                          ('b0000000-0000-0000-0000-000000000005', 'c0000000-0000-0000-0000-000000000006'),

                                                          -- The Oracle
                                                          ('b0000000-0000-0000-0000-000000000006', 'c0000000-0000-0000-0000-000000000007'),

                                                          -- The Merovingian
                                                          ('b0000000-0000-0000-0000-000000000007', 'c0000000-0000-0000-0000-000000000003');