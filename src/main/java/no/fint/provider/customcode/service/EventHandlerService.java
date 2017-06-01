package no.fint.provider.customcode.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.model.relation.FintResource;
import no.fint.provider.adapter.event.EventResponseService;
import no.fint.provider.adapter.event.EventStatusService;
import no.fint.provider.customcode.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The EventHandlerService receives the <code>event</code> from SSE endpoint (provider) in the {@link #handleEvent(String)} method.
 */
@Slf4j
@Service
public class EventHandlerService {

    @Autowired
    private EventResponseService eventResponseService;

    @Autowired
    private EventStatusService eventStatusService;

    /**
     * <p>
     * HandleEvent is responsible of handling the <code>event</code>. This is what should be done:
     * </p>
     * <ol>
     * <li>Convert the JSON event to an <a href="https://docs.felleskomponent.no/fint-event-model/">Event object</a></li>
     * <li>Verify that the adapter can handle the <code>event</code>. This is done in the {@link EventStatusService#verifyEvent(Event)} method</li>
     * <li>Call the code to handle the action</li>
     * <li>Posting back the handled <code>event</code>. This done in the {@link EventResponseService#postResponse(Event)} method</li>
     * </ol>
     * <p>
     * This is where you implement your code for handling the <code>event</code>. It is typically done by making a onEvent method:
     * </p>
     * <pre>
     *     {@code
     *     public Event onGetAllDogs(String event) {
     *         Event<String> dogAllEvent = EventUtil.toEvent(event);
     *
     *         // Call a service to get all dogs from the application and add the result to the event data
     *         // dogAllEvent.setData(dogs);
     *
     *         return dogAllEvent;
     *         }
     *     }
     * </pre>
     *
     * @param json The <code>event</code> received from the provider
     */
    public void handleEvent(String json) {
        Event event = EventUtil.toEvent(json);

        if (event.isHealthCheck()) {
            postHealthCheckResponse(event);
        } else {
            if (event != null && eventStatusService.verifyEvent(event).getStatus() == Status.PROVIDER_ACCEPTED) {
                Action action = Action.valueOf(event.getAction());
                Event<FintResource> responseEvent = new Event<>(event);

                /**
                 *
                 * Add if statements for all the actions
                 *
                 *   if (action == Action.GET_ALL_DOGS) {
                 *       onGetAllDogs(responseEvent);
                 *   }
                 *
                 */

                responseEvent.setStatus(Status.PROVIDER_RESPONSE);
                eventResponseService.postResponse(responseEvent);
            }
        }
    }

    /**
     * Checks if the application is healthy and updates the event object.
     *
     * @param event The event object
     */
    public void postHealthCheckResponse(Event event) {
        Event<Health> healthCheckEvent = new Event<>(event);
        healthCheckEvent.setStatus(Status.TEMP_UPSTREAM_QUEUE);

        if (healthCheck()) {
            healthCheckEvent.addData(new Health("adapter", HealthStatus.APPLICATION_HEALTHY.name()));
        } else {
            healthCheckEvent.addData(new Health("adapter", HealthStatus.APPLICATION_UNHEALTHY));
            healthCheckEvent.setMessage("The adapter is unable to communicate with the application.");
        }

        eventResponseService.postResponse(healthCheckEvent);
    }

    /**
     * This is where we implement the health check code
     *
     * @return {@code true} if health is ok, else {@code false}
     */
    private boolean healthCheck() {
        /*
         * Check application connectivity etc.
         */
        return true;
    }
}
