package no.fint.provider.customcode.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.Status;
import no.fint.event.model.health.Health;
import no.fint.event.model.health.HealthStatus;
import no.fint.model.relation.FintResource;
import no.fint.model.relation.Relation;
import no.fint.provider.adapter.event.EventResponseService;
import no.fint.provider.adapter.event.EventStatusService;
import no.fint.provider.adapter.sse.EventHandler;
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
public class EventHandlerService implements EventHandler {

    @Autowired
    private EventResponseService eventResponseService;

    private List<Dog> dogs;
    private List<Owner> owners;

    /**
     * <p>
     * HandleEvent is responsible of handling the <code>event</code>. This is what should be done:
     * </p>
     * <ol>
     * <li>Verify that the adapter can handle the <code>event</code>. This is done in the {@link EventStatusService#verifyEvent(Event)} method</li>
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
    public void handleEvent(Event event) {
        PwfaActions action = PwfaActions.valueOf(event.getAction());
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

        responseEvent.setStatus(Status.ADAPTER_RESPONSE);
        eventResponseService.postResponse(responseEvent);
    }

    /**
     * Example of handling action
     *
     * @param responseEvent Event containing the response
     */
    private void onGetOwner(Event<FintResource> responseEvent) {
        Optional<Owner> owner = owners.stream().filter(o -> o.getId().equals(responseEvent.getQuery())).findFirst();

        owner.ifPresent(owner1 -> responseEvent.addData(FintResource.with(owner1).addRelations(
                new Relation.Builder().with(Owner.Relasjonsnavn.DOG).forType(Dog.class).value(owner1.getId().substring(0, 1)).build())
        ));
    }

    /**
     * Example of handling action
     *
     * @param responseEvent Event containing the response
     */
    private void onGetDog(Event<FintResource> responseEvent) {
        Optional<Dog> dog = dogs.stream().filter(d -> d.getId().equals(responseEvent.getQuery())).findFirst();

        dog.ifPresent(dog1 -> responseEvent.addData(FintResource.with(dog1).addRelations(
                new Relation.Builder().with(Dog.Relasjonsnavn.OWNER).forType(Owner.class).value(dog1.getId() + "0").build())
        ));
    }

    /**
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
    @Override
    public void handleHealthCheck(Event event) {
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
