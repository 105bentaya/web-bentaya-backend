UPDATE booking
SET exclusive_reservation = 0
WHERE status = 'OCCUPIED';

UPDATE booking
SET exclusive_reservation = 1,
    status                = 'OCCUPIED'
WHERE status = 'FULLY_OCCUPIED';

UPDATE booking
SET exclusive_reservation = 1,
    status                = 'OCCUPIED'
WHERE status = 'LEFT'
   or status = 'FINISHED';
