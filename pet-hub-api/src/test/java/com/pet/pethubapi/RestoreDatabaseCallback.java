package com.pet.pethubapi;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Callback which clears whole database and reruns Flyway database migrations. Used in integrations tests, to clear state and prepare clean state for next test (due to Testcontainers being reused between tests).
 * Property "spring.flyway.clean-disabled" must be set to false, otherwise Flyway.clean() will throw exception.
 * Should be used as <code>@ExtendWith(RestoreDatabaseCallback.class)</code> when there is data that actually needs to be cleaned up.
 * <br></br>
 * This solution could significantly slow down test execution with number/complexity of database migrations. In that case, utilize <code>JdbcTestUtils.deleteFromTable()</code>.
 * Or experiment with: <a href="https://mikemybytes.com/2023/02/01/squashing-db-migrations-using-testcontainers/">Squash DB migrations</a>.
 * @see TruncateTable
 */
@Slf4j
public class RestoreDatabaseCallback implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        final var flyway = SpringExtension.getApplicationContext(extensionContext).getBean(Flyway.class);

        log.info("Cleaning up database and execution migrations...");
        flyway.clean();
        flyway.migrate();
    }

}
