CREATE TABLE scout_center_file
(
    id        INT AUTO_INCREMENT NOT NULL,
    uuid      VARCHAR(255)       NOT NULL,
    name      VARCHAR(255)       NOT NULL,
    mime_type VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_scoutcenterfile PRIMARY KEY (id)
);

CREATE TABLE scout_center
(
    id                     INT AUTO_INCREMENT NOT NULL,
    name                   VARCHAR(127)       NOT NULL,
    place                  VARCHAR(127)       NOT NULL,
    max_capacity           INT                NOT NULL,
    min_exclusive_capacity INT                NOT NULL,
    rule_pdf_id            INT                NULL,
    incidences_doc_id      INT                NULL,
    attendance_doc_id      INT                NULL,
    information            VARCHAR(1023)      NOT NULL,
    price                  INT                NOT NULL,
    CONSTRAINT pk_scoutcenter PRIMARY KEY (id),
    CONSTRAINT FK_SCOUTCENTER_ON_ATTENDANCEDOC FOREIGN KEY (attendance_doc_id) REFERENCES scout_center_file (id),
    CONSTRAINT FK_SCOUTCENTER_ON_INCIDENCESDOC FOREIGN KEY (incidences_doc_id) REFERENCES scout_center_file (id),
    CONSTRAINT FK_SCOUTCENTER_ON_RULEPDF FOREIGN KEY (rule_pdf_id) REFERENCES scout_center_file (id)
);

CREATE TABLE scout_center_features
(
    scout_center_id INT          NOT NULL,
    features        VARCHAR(255) NOT NULL,
    CONSTRAINT fk_scoutcenter_features_on_scout_center FOREIGN KEY (scout_center_id) REFERENCES scout_center (id)
);

CREATE TABLE scout_center_photos
(
    scout_center_id INT NOT NULL,
    photos_id       INT NOT NULL,
    CONSTRAINT fk_scocenpho_on_scout_center FOREIGN KEY (scout_center_id) REFERENCES scout_center (id),
    CONSTRAINT fk_scocenpho_on_scout_center_file FOREIGN KEY (photos_id) REFERENCES scout_center_file (id)
);

INSERT INTO scout_center (id, name, max_capacity, min_exclusive_capacity, information, price, place)
VALUES (1, 'Aula de la Naturaleza El Palmital', 36, 25,
        'El Aula de la Naturaleza El Palmital ubicada en Santa María de Guía y bastante cerca de Moya es un sitio ideal para hacer actividades ambientales. Aunque el sitio no cuenta con zonas específicas de naturaleza, cuenta con unos alrededores magníficos donde hacer rutas y otro tipo de juegos/talleres educativos al aire libre. Este es un espacio cedido por el Ayuntamiento de Santa María de Guía a nuestra Asociación que es quien lo gestiona.',
        600, 'El Palmital, Guía'),
       (2, 'Campamento Picacho-Doñana', 390, 30,
        'El Campamento Picacho-Doñana, ubicado en la montaña del picacho es un espacio abierto enorme para poder realizar actividades de campamento. Existen varias parcelas definidas donde poder montar las casetas, llegando a haber como máximo unas 390 personas acampadas a la vez. El campamento está a la altura de los espacios de acampada que podemos encontrar en la isla y está además a unos pocos minutos de Arucas, donde pueden comprar abastecimiento para la actividad que vayan a realizar o acudir a un centro sanitario si fuera necesario.',
        200, 'Santidad, Arucas'),
       (3, 'Refugio Luis Martín', 45, 0,
        'El refugio en lo alto del Campamento Picacho-Doñana es uno de los mágicos sitios de los que disponemos, rodeado de vegetación autóctona canaria como es el drago.',
        200, 'El Picacho, Arucas'),
       (4, 'Refugio Bentayga', 45, 0,
        'El Refugio Tejeda se encuentra bajo la sombra del Roque Bentayga. Es una pequeña casita con un cuarto multiespacio que hace de zona de estar y habitación. Se encuentra en mitad del barranco, dando la posibilidad de hacer rutas en subida, sentido Tejeda o en bajada. Además, tiene cerca las Cuevas del Rey, un espacio arqueológico único en la isla.',
        250, 'La Higuerilla, Tejeda');


INSERT INTO scout_center_features
VALUES (1, 'Antigua escuela unitaria reformada'),
       (1, 'Cuenta con un total 36 camas distribuidas en 5 habitaciones'),
       (1, 'Comedor con mesas y sillas para unas 40 personas'),
       (1, 'Cocina equipada con vitro de dos fuegos, extractor, microondas, fregadero, nevera y congelador'),
       (1, 'En el exterior lavaderos con 10 chorros'),
       (1, '1 baño unisex con 3 inodoros y 3 duchas (con agua caliente)'),
       (1, '1 baño unisex con 3 inodoros'),
       (1, 'Dos aula multiusos con mesas y sillas'),
       (1, '2 baños individuales (sin agua caliente)'),
       (1, '1 baño para personas de movilidad reducida  (sin agua caliente)'),
       (1, 'Un aula de reuniones con mesas y sillas'),
       (1, 'Dos aulas multiuso con mesas y sillas'),
       (1, 'Una sala con productos de limpieza'),
       (1, 'Una pequeña cancha de fútbol con porterías pequeñas y una canasta en un lateral'),
       (1, 'Aforo máximo: 36 personas'),
       (2, '4 baños individuales'),
       (2, 'Duchas compartidas al aire libre para 20 personas simultáneamente'),
       (2, 'Horno de leña'),
       (2, 'Barbacoa'),
       (2, 'Espacios donde realizar actividades y juegos'),
       (2, 'Aparcamiento a la entrada'),
       (2, 'Aforo máximo: 390 personas. (Varias zonas de acampada con suficiente espacio para distintos grupos)'),
       (2, 'Temporalmente solo admitimos reservas de Asociaciones Scout'),
       (3, 'Una habitación con tres plantas de litera corrida, con capacidad para unas 20 a 35 personas según tamaño'),
       (3, 'Una habitación con literas individuales, que hacen un total de 10 camas'),
       (3, 'Una pequeña cocina con fregadero y fuego de gas'),
       (3, 'Chorros de agua exteriores'),
       (3, 'Aforo máximo: entre 35 y 45 personas, según el tamaño de las mismas'),
       (3, 'Temporalmente solo admitimos reservas de Asociaciones Scout'),
       (4, 'Casa rural con alpendre y pequeño terreno'),
       (4, 'Baño completo'),
       (4, 'Cocina completa'),
       (4, 'Aforo máximo: entre 35 y 45 personas, según el tamaño de las mismas'),
       (4, 'Temporalmente solo admitimos reservas de Asociaciones Scout');

#UPDATE EXISTING BOOKINGS

ALTER TABLE booking
    add scout_center_id int;

UPDATE booking
SET scout_center_id = 1
WHERE scout_center = 'PALMITAL';

UPDATE booking
SET scout_center_id = 2
WHERE scout_center = 'TERRENO';

UPDATE booking
SET scout_center_id = 3
WHERE scout_center = 'REFUGIO_TERRENO';

UPDATE booking
SET scout_center_id = 4
WHERE scout_center = 'TEJEDA';

ALTER TABLE booking
    DROP scout_center;

ALTER TABLE booking
    ADD CONSTRAINT FK_BOOKING_ON_SCOUTCENTER FOREIGN KEY (scout_center_id) REFERENCES scout_center (id);