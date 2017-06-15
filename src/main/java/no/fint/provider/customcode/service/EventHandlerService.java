package no.fint.provider.customcode.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.model.relation.FintResource;
import no.fint.model.relation.Relation;
import no.fint.provider.adapter.event.EventResponseService;
import no.fint.provider.adapter.event.EventStatusService;
import no.fint.provider.customcode.Action;
import no.fint.pwfa.model.Dog;
import no.fint.pwfa.model.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private List<Dog> dogs;
    private List<Owner> owners;

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
     * @param event The <code>event</code> received from the provider
     */
    public void handleEvent(Event event) {
        if (event.isHealthCheck()) {
            postHealthCheckResponse(event);
        } else {
            if (event != null && eventStatusService.verifyEvent(event).getStatus() == Status.PROVIDER_ACCEPTED) {
                Action action = Action.valueOf(event.getAction());
                Event<FintResource> responseEvent = new Event<>(event);

                switch (action) {
                    case GET_DOG:
                        onGetDog(responseEvent);
                        break;
                    case GET_OWNER:
                        onGetOwner(responseEvent);
                        break;
                    case GET_ALL_DOGS:
                        onGetAllDogs(responseEvent);
                        break;
                    case GET_ALL_OWNERS:
                        onGetAllOwners(responseEvent);
                        break;
                }

                responseEvent.setStatus(Status.PROVIDER_RESPONSE);
                eventResponseService.postResponse(responseEvent);
            }
        }
    }

    /**
     * Example of handling action
     *
     * @param responseEvent
     */
    private void onGetOwner(Event<FintResource> responseEvent) {
        Optional<Owner> owner = owners.stream().filter(o -> o.getId().equals(responseEvent.getQuery())).findFirst();

        if (owner.isPresent()) {
            responseEvent.addData(FintResource.with(owner.get()).addRelasjoner(
                    new Relation.Builder().with(Owner.Relasjonsnavn.DOG).forType(Dog.class).value(owner.get().getId().substring(0, 1)).build())
            );
        }
    }

    /**
     * Example of handling action
     *
     * @param responseEvent
     */
    private void onGetDog(Event<FintResource> responseEvent) {
        Optional<Dog> dog = dogs.stream().filter(d -> d.getId().equals(responseEvent.getQuery())).findFirst();

        if (dog.isPresent()) {
            responseEvent.addData(FintResource.with(dog.get()).addRelasjoner(
                    new Relation.Builder().with(Dog.Relasjonsnavn.OWNER).forType(Owner.class).value(dog.get().getId() + "0").build())
            );

        }
    }

    /**
     * Example of handling action
     *
     * @param responseEvent
     */
    private void onGetAllOwners(Event<FintResource> responseEvent) {


        Relation relationDog1 = new Relation.Builder().with(Owner.Relasjonsnavn.DOG).forType(Dog.class).value("1").build();
        Relation relationDog2 = new Relation.Builder().with(Owner.Relasjonsnavn.DOG).forType(Dog.class).value("2").build();

        responseEvent.addData(FintResource.with(owners.get(0)).addRelasjoner(relationDog1));
        responseEvent.addData(FintResource.with(owners.get(1)).addRelasjoner(relationDog2));

    }

    /**
     * Example of handling action
     *
     * @param responseEvent
     */
    private void onGetAllDogs(Event<FintResource> responseEvent) {

        Relation relationOwner1 = new Relation.Builder().with(Dog.Relasjonsnavn.OWNER).forType(Owner.class).value("10").build();
        Relation relationOwner2 = new Relation.Builder().with(Dog.Relasjonsnavn.OWNER).forType(Owner.class).value("20").build();

        responseEvent.addData(FintResource.with(dogs.get(0)).addRelasjoner(relationOwner1));
        responseEvent.addData(FintResource.with(dogs.get(1)).addRelasjoner(relationOwner2));

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

    /**
     * Data used in examples
     */
    @PostConstruct
    void init() {
        owners = new ArrayList<>();
        dogs = new ArrayList<>();

        owners.add(new Owner("10", "Mikke Mus"));
        owners.add(new Owner("20", "Minni Mus"));

        dogs.add(new Dog("1", "Pluto", "Working Springer Spaniel"));
        dogs.add(new Dog("2", "Lady", "Working Springer Spaniel"));
    }
}
