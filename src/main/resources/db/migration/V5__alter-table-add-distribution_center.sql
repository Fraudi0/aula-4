ALTER TABLE product
ADD COLUMN distribution_center TEXT;

UPDATE product
SET distribution_center = 'Mogi das Cruzes'
WHERE id IN ('p1', 'p2', 'p3', 'p4', 'p5', 'p6', 'p7', 'p8', 'p9', 'p10');

UPDATE product
SET distribution_center = 'Recife'
WHERE id IN ('p11', 'p12', 'p13', 'p14', 'p15', 'p16', 'p17', 'p18');

UPDATE product
SET distribution_center = 'Porto Alegre'
WHERE id IN ('p19', 'p20');