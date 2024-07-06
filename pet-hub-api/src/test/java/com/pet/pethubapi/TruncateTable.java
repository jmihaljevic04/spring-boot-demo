package com.pet.pethubapi;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

/**
 * Deletes all records from defined tables. All tables that want to be cleaned up have to be listed and maintained manually.
 * Used in integration tests, where JdbcTemplate and its getter must be provided. Substitute for custom-implemented AfterAll callback.
 *
 * @see RestoreDatabaseCallback
 */
public abstract class TruncateTable {

    public abstract JdbcTemplate getJdbcTemplate();

    public void clean() {
        JdbcTestUtils.deleteFromTables(getJdbcTemplate(), """
            table1, table2
            """);
    }

}
