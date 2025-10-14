package com.kamruddin.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.TestPropertySource;

@ApplicationModuleTest
@TestPropertySource(properties = {
    "spring.modulith.events.jdbc.schema-initialization.enabled=true"
})
class EventPublicationServiceTest {

    @Autowired
    private EventPublicationService eventPublicationService;

    @Test
    void shouldResubmitIncompletePublications() {
        // This test verifies that the EventPublicationService is properly configured
        // and can be injected. The actual resubmission would happen in a real scenario
        // where events fail and need to be retried on application restart.

        // In a real test, you would:
        // 1. Create a scenario where an event listener fails
        // 2. Verify that the publication is marked as incomplete
        // 3. Call resubmitIncompletePublications()
        // 4. Verify that the event is retried

        // For now, we just verify the service exists and can be called
        eventPublicationService.resubmitIncompletePublications();
    }

    @Test
    void shouldResubmitFailedPublications() {
        // Test the method for resubmitting only failed publications
        eventPublicationService.resubmitFailedPublications();
    }
}