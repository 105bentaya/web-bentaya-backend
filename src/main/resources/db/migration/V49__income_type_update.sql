ALTER TABLE invoice_income_type ADD COLUMN donation BIT(1) DEFAULT FALSE;

UPDATE invoice_income_type
SET donation = TRUE
WHERE id BETWEEN 1 AND 3;