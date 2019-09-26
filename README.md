# entityauditor


CREATE TABLE `entity_audit` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `entity` VARCHAR(45) NULL,
  `row_id` VARCHAR(255) NULL,
  `operation` VARCHAR(45) NULL,
  `old_values` LONGTEXT NULL,
  `new_values` LONGTEXT NULL,
  `created_by` VARCHAR(255) NULL,
  `created_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`));

