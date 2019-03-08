package no.fint.customcode.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.adapter.event.EventResponseService;
import no.fint.adapter.event.EventStatusService;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.model.relation.FintResource;
import no.fint.model.relation.Relation;
import no.fint.pwfa.model.Dog;
import no.fint.pwfa.model.Owner;
import no.fint.pwfa.model.PwfaActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The EventHandlerService receives the <code>event</code> from SSE endpoint (provider) in the {@link #handleEvent(Event)} method.
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

    public void handleEvent(Event event) {
        if (event.isHealthCheck()) {
            postHealthCheckResponse(event);
        } else {
            if (eventStatusService.verifyEvent(event).getStatus() == Status.ADAPTER_ACCEPTED) {
                Event<FintResource> responseEvent = new Event<>(event);
                try {

                    createEventResponse(event, responseEvent);

                } catch (Exception e) {
                    log.error("Error handling event {}", event, e);
                    responseEvent.setResponseStatus(ResponseStatus.ERROR);
                    responseEvent.setMessage(e.getMessage());
                } finally {
                    responseEvent.setStatus(Status.ADAPTER_RESPONSE);
                    eventResponseService.postResponse(responseEvent);
                }
            }
        }
    }

    /**
     * TODO
     * <p>
     * createEventResponse is responsible for responding to the <code>event</code>. This is what should be done:
     * </p>
     * <ol>
     * <li>Call the code to handle the action</li>
     * <li>Posting back the handled <code>event</code>. This done in the {@link EventResponseService#postResponse(Event)} method</li>
     * </ol>
     * <p>
     * This is where you implement your code for handling the <code>event</code>. It is typically done by making a onEvent method:
     * </p>
     * <pre>
     *     {@code
     *     public void onGetAllDogs(Event<FintResource> dogAllEvent) {
     *
     *         // Call a service to get all dogs from the application and add the result to the event data
     *         // dogAllEvent.addData(dogResource);
     *
     *     }
     *     }
     * </pre>
     *
     * @param event The <code>event</code> received from the provider
     */
    private void createEventResponse(Event event, Event<FintResource> responseEvent) {
        switch (PwfaActions.valueOf(event.getAction())) {
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
    }

    /**
     * TODO
     * Example of handling action
     *
     * @param responseEvent Event containing the response
     */
    private void onGetOwner(Event<FintResource> responseEvent) {
        Optional<Owner> owner = owners.stream().filter(o -> o.getId().equals(responseEvent.getQuery())).findFirst();

        if (owner.isPresent()) {
            responseEvent.addData(FintResource.with(owner.get()).addRelations(
                    new Relation.Builder().with(Owner.Relasjonsnavn.DOG).forType(Dog.class).value(owner.get().getId().substring(0, 1)).build())
            );
        }
    }

    /**
     * TODO
     * Example of handling action
     *
     * @param responseEvent Event containing the response
     */
    private void onGetDog(Event<FintResource> responseEvent) {
        Optional<Dog> dog = dogs.stream().filter(d -> d.getId().equals(responseEvent.getQuery())).findFirst();

        if (dog.isPresent()) {
            responseEvent.addData(FintResource.with(dog.get()).addRelations(
                    new Relation.Builder().with(Dog.Relasjonsnavn.OWNER).forType(Owner.class).value(dog.get().getId() + "0").build())
            );

        }
    }

    /**
     * TODO
     * Example of handling action
     *
     * @param responseEvent Event containing the response
     */
    private void onGetAllOwners(Event<FintResource> responseEvent) {


        Relation relationDog1 = new Relation.Builder().with(Owner.Relasjonsnavn.DOG).forType(Dog.class).value("1").build();
        Relation relationDog2 = new Relation.Builder().with(Owner.Relasjonsnavn.DOG).forType(Dog.class).value("2").build();

        responseEvent.addData(FintResource.with(owners.get(0)).addRelations(relationDog1));
        responseEvent.addData(FintResource.with(owners.get(1)).addRelations(relationDog2));

    }

    /**
     * TODO
     * Example of handling action
     *
     * @param responseEvent Event containing the response
     */
    private void onGetAllDogs(Event<FintResource> responseEvent) {

        Relation relationOwner1 = new Relation.Builder().with(Dog.Relasjonsnavn.OWNER).forType(Owner.class).value("10").build();
        Relation relationOwner2 = new Relation.Builder().with(Dog.Relasjonsnavn.OWNER).forType(Owner.class).value("20").build();

        responseEvent.addData(FintResource.with(dogs.get(0)).addRelations(relationOwner1));
        responseEvent.addData(FintResource.with(dogs.get(1)).addRelations(relationOwner2));

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
     * TODO
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
     * TODO
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
