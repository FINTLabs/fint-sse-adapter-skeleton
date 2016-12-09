package no.fint.provider.hrapp.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.EventUtil;
import no.fint.event.model.Status;
import no.fint.provider.adapter.service.EventResponseService;
import no.fint.provider.adapter.service.EventStatusService;
import no.fint.provider.hrapp.Action;
import no.fk.fint.arbeidstaker.Arbeidstaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventHandlerService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EventResponseService eventResponseService;

    @Autowired
    private EventStatusService eventStatusService;

    public void handleEvent(String event) {
        Event eventObj = EventUtil.toEvent(event);
        if (eventObj != null && eventStatusService.verifyEvent(eventObj).getStatus() == Status.PROVIDER_ACCEPTED) {
            Action action = Action.valueOf(eventObj.getAction());
            Event<?> responseEvent = null;

            if (action == Action.HEALTH) {
                responseEvent = onHealthCheck(event);
            }

            if (responseEvent != null) {
                responseEvent.setStatus(Status.PROVIDER_RESPONSE);
                eventResponseService.postResponse(responseEvent);
            }
        }
    }

    public Event onHealthCheck(String event) {
        Event<String> healthEvent = EventUtil.toEvent(event);

        if (employeeService.healthCheck()) {
            healthEvent.setData(Lists.newArrayList("I'm fine thanks! How are you?"));
        } else {
            healthEvent.setData(Lists.newArrayList("Oh, I'm feeling bad! How are you?"));
        }

        return healthEvent;
    }
}
