package com.pet.pethubapi;

import com.hazelcast.core.Hazelcast;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Callback which shuts down in-memory Hazelcast instance started in integration tests. Without this, port exhaustion would occur.
 * Used automatically for with all integration tests.
 *
 * @see PetIntegrationTest
 */
public class ShutdownHazelcastCallback implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        Hazelcast.shutdownAll();
    }

}
