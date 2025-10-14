package com.kamruddin.modulith;

import java.time.Duration;
import java.util.function.Predicate;

import org.springframework.modulith.events.EventPublication;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublicationService {

    private final IncompleteEventPublications incompleteEventPublications;

    /**
     * Resubmit all incomplete publications
     */
    public void resubmitIncompletePublications() {
        log.info("Resubmitting all incomplete event publications");
        incompleteEventPublications.resubmitIncompletePublications(p -> true);
    }

    /**
     * Resubmit failed publications only
     */
    public void resubmitFailedPublications() {
        log.info("Resubmitting failed event publications");
        Predicate<EventPublication> failedPublications = p -> {
            try {
                return p.getCompletionDate().isEmpty();
            } catch (Exception e) {
                return true; // Consider it failed if we can't determine status
            }
        };
        incompleteEventPublications.resubmitIncompletePublications(failedPublications);
    }

    /**
     * Resubmit publications older than the given duration
     */
    public void resubmitIncompletePublicationsOlderThan(Duration duration) {
        log.info("Resubmitting incomplete event publications older than {}", duration);
        incompleteEventPublications.resubmitIncompletePublicationsOlderThan(duration);
    }
}