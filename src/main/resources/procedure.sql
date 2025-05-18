CREATE DEFINER=`root`@`localhost` PROCEDURE `list_device`(
    IN queryType VARCHAR(20),
    IN page INT,
    IN pageSize INT,
    IN accountingCode VARCHAR(255),
    IN location VARCHAR(255),
    IN manufacture VARCHAR(255),
    IN notes VARCHAR(255),
    IN purchaseDate DATETIME,
    IN purpose VARCHAR(255),
    IN serialNumber VARCHAR(255),
    IN categoryId BIGINT,
    IN expirationDate DATETIME
)
BEGIN
    DECLARE selectQuery VARCHAR(20);

    IF queryType = 'LIST' THEN
        SET selectQuery = 'SELECT * ';
    ELSE
        SET selectQuery = 'SELECT COUNT(*) ';
    END IF;

    IF page IS NULL THEN
        SET page = 1;
    END IF;

    IF pageSize IS NULL THEN
        SET pageSize = 10;
    END IF;

    SET @query = CONCAT(selectQuery, 'FROM Device WHERE 1=1 ');

    IF accountingCode IS NOT NULL AND accountingCode != '' THEN
        SET @query = CONCAT(@query, ' AND accounting_code LIKE "%', accountingCode, '%" ');
    END IF;

    IF location IS NOT NULL AND location != '' THEN
        SET @query = CONCAT(@query, ' AND location LIKE "%', location, '%" ');
    END IF;

    IF manufacture IS NOT NULL AND manufacture != '' THEN
        SET @query = CONCAT(@query, ' AND manufacture LIKE "%', manufacture, '%" ');
    END IF;

    IF notes IS NOT NULL AND notes != '' THEN
        SET @query = CONCAT(@query, ' AND notes LIKE "%', notes, '%" ');
    END IF;

    IF purchaseDate IS NOT NULL THEN
        SET @query = CONCAT(@query, ' AND purchase_date = "', purchaseDate, '" ');
    END IF;

    IF purpose IS NOT NULL AND purpose != '' THEN
        SET @query = CONCAT(@query, ' AND purpose LIKE "%', purpose, '%" ');
    END IF;

    IF serialNumber IS NOT NULL AND serialNumber != '' THEN
        SET @query = CONCAT(@query, ' AND serial_number LIKE "%', serialNumber, '%" ');
    END IF;

    IF categoryId IS NOT NULL THEN
        SET @query = CONCAT(@query, ' AND category_id = ', categoryId, ' ');
    END IF;

    IF expirationDate IS NOT NULL THEN
        SET @query = CONCAT(@query, ' AND expiration_date = "', expirationDate, '" ');
    END IF;

    IF queryType = 'LIST' THEN
        SET @query = CONCAT(@query, ' LIMIT ', (page - 1) * pageSize, ',', pageSize);
    END IF;

    PREPARE statement FROM @query;
    EXECUTE statement;
    DEALLOCATE PREPARE statement;
END;