# FINT SSE Adapter Skeleton

[![Build Status](https://jenkins.rogfk.no/buildStatus/icon?job=FINTprosjektet/fint-sse-adapter-skeleton/master)](https://jenkins.rogfk.no/job/FINTprosjektet/job/fint-sse-adapter-skeleton/job/master/)

* [Introduction](#introduction)
* [Packages and files](#packages-and-files)
 * [Action.java](#actionjava)
 * [EvnetHandlerService.java](#eventhandlerservicejava)
* [Adapter configuration](#adapter-configuration)


## Introduction

## Packages and files
The adapter is divided into to main packages. The `adapter package` is the core adapter code. In general this don't need
any customization. The `customcode package` (which should be named for example after the application the adapter talks to)
is where the logic of the adapter is placed.

### Action.java
This is a ENUM of all the actions this adapter supports. Every adapter must support the HEALTH action. For example for
the student component the action enum should be something like:

```java
public enum Action {
    HEALTH,
    GET_ALL_STUDENTS,
    GET_STUDENT,
    UPDATE_STUDENT;

    ...
}
```

### EventHandlerService.java
The actions is handled in the `handleEvent()` method:

````java
    public void handleEvent(String event) {
        Event eventObj = EventUtil.toEvent(event);
        if (eventObj != null && eventStatusService.verifyEvent(eventObj).getStatus() == Status.PROVIDER_ACCEPTED) {
            Action action = Action.valueOf(eventObj.getAction());
            Event<?> responseEvent = null;

            if (action == Action.HEALTH) {
                responseEvent = onHealthCheck(event);
            }

            /**
             * Add if statements for all the actions
             */

            if (responseEvent != null) {
                responseEvent.setStatus(Status.PROVIDER_RESPONSE);
                eventResponseService.postResponse(responseEvent);
            }
        }
    }

````

## Adapter configuration
| Key | Description | Example |
|-----|-------------|---------|
| fint.provider.adapter.organizations | List of orgIds the adapter handles. | rogfk.no, vaf.no, ofk.no |
| fint.provider.adapter.sse-endpoint | Url to the sse endpoint. | https://api.felleskomponent.no/arbeidstakere/provider/sse |
| fint.provider.adapter.status-endpoint | Url to the status endpoint. | https://api.felleskomponent.no/arbeidstakere/provider/status |
| fint.provider.adapter.response-endpoint | Url to the response endpoint. | https://api.felleskomponent.no/arbeidstakere/provider/response |

