package io.sattler.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.sattler.api.EventBasicEdit;
import io.sattler.api.EventCreation;
import io.sattler.api.EventDateEdit;
import io.sattler.api.EventDetailsEdit;
import io.sattler.client.GuardianClient;
import io.sattler.db.*;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    private final EventDAO eventDAO;

    private final static Logger log = LoggerFactory.getLogger(EventResource.class);

    public EventResource(EventDAO eventDAO) { this.eventDAO = eventDAO; }

    @GET
    @ExceptionMetered
    @Timed
    @Path("/event/get/{event_id}/{company_id}")
    public Response getEventById(@PathParam("event_id") String eventId,
                                 @PathParam("company_id") String companyId,
                                 @Context HttpHeaders httpHeaders) {
        String requestId = httpHeaders.getHeaderString("x-transactionid");
        String userId = httpHeaders.getHeaderString("x-user-uuid");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        logInfoWithTransactionId(requestId, "got new request to fetch basic event informations");

        if (userId == null || userId.isEmpty()) {
            logInfoWithTransactionId(requestId, "no user id submitted");
            throw new WebApplicationException("no user submitted", 401);
        }
        String host = System.getenv("GUARDIAN_URL") + userId + '/' + companyId;
        GuardianClient client = new GuardianClient(host, requestId);

        try {
            Event event = null;
            Boolean permission = client.getUserPermission(1);
            if (permission) {
                event = eventDAO.fetchEventById(companyId, eventId);
            } else {
                event = eventDAO.fetchEventByIdWithPermission(userId,
                        companyId, eventId);
            }
            log.info("----------------");
            log.info(event.getStartDate().toString());
            log.info(event.getEndDate().toString());
            log.info("----------------");

            if (event == null) {
                logInfoWithTransactionId(requestId, "event does not exist abort with status 404");
                throw new WebApplicationException("the event does not exist", 404);
            }
            Set<EventLanguages> eventLanguages = eventDAO.fetchLanguagesFromEvent(event.getId());
            if (eventLanguages != null) {
                event.setEventLanguages(eventLanguages);
            }

            Set<EventCurrencies> eventCurrencies = eventDAO.fetchCurrenciesFromEvent(event.getId());
            if (eventCurrencies != null) {
                event.setEventCurrencies(eventCurrencies);
            }

            return Response.status(200).entity(event).build();

        } catch (UnirestException e) {
            logInfoWithTransactionId(requestId, "not possible to communicate with Guardian abort with 500");
            log.error(e.toString());
            log.error(e.getMessage());
            throw new WebApplicationException("permission error", 500);
        }
    }

    @GET
    @ExceptionMetered
    @Timed
    @Path("/event/get/{company_id}")
    public Response getBasicEventData(@PathParam("company_id") String companyId,
                                      @Context HttpHeaders httpHeaders) {
        String requestId = httpHeaders.getHeaderString("x-transactionid");
        String userId = httpHeaders.getHeaderString("x-user-uuid");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        logInfoWithTransactionId(requestId, "got new request to fetch basic event informations");

        if (userId == null || userId.isEmpty()) {
            logInfoWithTransactionId(requestId, "no user id submitted");
            throw new WebApplicationException("no user submitted", 401);
        }
        String host = System.getenv("GUARDIAN_URL") + userId + '/' + companyId;
        GuardianClient client = new GuardianClient(host, requestId);

        try {
            if (client.getUserPermission(1)) {
                logInfoWithTransactionId(requestId, "user is a manager give them full access");
                List<Event> events = eventDAO.getBasicEventInformation(companyId);
                return Response.status(200).entity(events).build();
            } else {
                logInfoWithTransactionId(requestId, "user is not a manager dont give them full access");
                List<Event> events = eventDAO.getBasicEventInformationWithPermission(userId, companyId);
                return Response.status(200).entity(events).build();
            }
        } catch (UnirestException e) {
            log.error(e.toString());
            log.error(e.getMessage());
            logInfoWithTransactionId(requestId, "not possible to get permission from guardian");
            throw new WebApplicationException("there was an internal server error", 500);
        }
    }

    @PUT
    @ExceptionMetered
    @Timed
    @Path("/event/edit/date/{event_id}/{company_id}")
    public Response editEventDates(@NotNull @Valid EventDateEdit eventDateEdit,
                                   @PathParam("event_id") String eventId,
                                   @PathParam("company_id") String companyId,
                                   @Context HttpHeaders httpHeaders) {

        String requestId = httpHeaders.getHeaderString("x-transactionid");
        String userId = httpHeaders.getHeaderString("x-user-uuid");

        logInfoWithTransactionId(requestId, "got new request to update event dates");

        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        if (userId == null || userId.isEmpty()) {
            logInfoWithTransactionId(requestId, "no user id submitted");
            throw new WebApplicationException("not authorized", 401);
        }

        logInfoWithTransactionId(requestId, "trying to validate authorization from guardian");

        String host = System.getenv("GUARDIAN_URL") + userId + '/' + companyId;

        GuardianClient client = new GuardianClient(host, requestId);

        Event event;

        try {

            if (!client.getUserPermission(3)) {
                logInfoWithTransactionId(requestId,"required permission is 3 for product manager");
                throw new WebApplicationException("not authorized", 401);
            }
            event = eventDAO.fetchEventById(companyId, eventId);
        } catch (UnirestException e) {
            logInfoWithTransactionId(requestId, e.toString());
            throw new WebApplicationException("not possible to verify user permission", 401);
        }
        if (event == null) {
            throw new WebApplicationException("event does not exist", 404);
        }
        if (eventDateEdit.getMultiDayEvent()) {
            logInfoWithTransactionId(requestId, "method for multiple days are not implemented :(");
            throw new WebApplicationException("multi day events are  not implemented yet", Response.Status.NOT_IMPLEMENTED.getStatusCode());
        }

        try {
            log.info(eventDateEdit.getEndDate().toString());
            log.info(eventDateEdit.getStartDate().toString());
           logInfoWithTransactionId(requestId, "everything is good trying now to insert update into database");
           eventDAO.updateEventOneTimeDates(eventDateEdit.getMultiDayEvent(),
                   new Timestamp(eventDateEdit.getStartDate().toDateTime(DateTimeZone.UTC).getMillis()),
                   new Timestamp(eventDateEdit.getEndDate().toDateTime(DateTimeZone.UTC).getMillis()),
                   companyId, eventId);

           JSONObject response = new JSONObject().put("status", "OK").put("message", "sussessfully updated event")
                   .put("status_code", 200).put("transaction_id", requestId);

           return Response.status(200).entity(response.toString()).build();
        } catch (Exception e) {
            logInfoWithTransactionId(requestId, "not possible to update event abort transaction");
            log.warn(e.toString());
            log.warn(e.getMessage());
            throw new WebApplicationException("error while trying to update event", 400);
        }
    }

    @PUT
    @ExceptionMetered
    @Metered
    @Path("/event/edit/basic/{event_id}/{company_id}")
    public Response editEventLocation(@NotNull EventBasicEdit evenEdit,
                                      @PathParam("event_id") String eventId,
                                      @PathParam("company_id") String companyId,
                                      @Context HttpHeaders httpHeaders) {
        String requestId = httpHeaders.getHeaderString("x-transactionid");
        String userId = httpHeaders.getHeaderString("x-user-uuid");

        logInfoWithTransactionId(requestId, "got new request to update event basic information and location");

        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        if (userId == null || userId.isEmpty()) {
            logInfoWithTransactionId(requestId, "no user id submitted");
            throw new WebApplicationException("not authorized", 401);
        }

        logInfoWithTransactionId(requestId, "trying to validate authorization from guardian");

        String host = System.getenv("GUARDIAN_URL") + userId + '/' + companyId;

        GuardianClient client = new GuardianClient(host, requestId);

        Event event;

        try {

            if (!client.getUserPermission(3)) {
                logInfoWithTransactionId(requestId,"user has not the required permission");
                throw new WebApplicationException("not authorized", 401);
            }
            event = eventDAO.fetchEventById(companyId, eventId);
        } catch (UnirestException e) {
            logInfoWithTransactionId(requestId, e.toString());
            throw new WebApplicationException("not possible to verify user permission", 401);
        }
        if (event == null) {
            throw new WebApplicationException("event does not exist", 404);
        }

        try {
            if (evenEdit.getEventLocation() == null) {
                logInfoWithTransactionId(requestId, "updating event without event location");
                eventDAO.updateEventBasicDetails(evenEdit.getEventName(), evenEdit.getVenue(),
                        companyId, eventId);
            } else {
                logInfoWithTransactionId(requestId, "updating event with event location");
                eventDAO.updateEventBasicDetailsWithLocation(evenEdit.getEventName(),
                        evenEdit.getVenue(), evenEdit.getEventLocation().getStreetNumber().get(), evenEdit.getEventLocation().getStreet(),
                        evenEdit.getEventLocation().getCity(), evenEdit.getEventLocation().getCountryId(),
                        evenEdit.getEventLocation().getZip(), evenEdit.getEventLocation().getLatitude(),
                        evenEdit.getEventLocation().getLongitude(), companyId, eventId);
            }
            JSONObject positiveResponse = new JSONObject().put("status", "OK")
                    .put("status_code", 200)
                    .put("message", "successfully updated event")
                    .put("transaction_id", requestId);
            return Response.status(200).entity(positiveResponse.toString()).build();
        } catch (Exception e) {
            logInfoWithTransactionId(requestId, "not possible to update event in database");
            log.warn(e.getMessage());
            throw new WebApplicationException("not possible to update event", 500);
        }
    }

    @PUT
    @ExceptionMetered
    @Metered
    @Path("/event/edit/detail/{event_id}/{company_id}")
    public Response editEventDetails(@NotNull EventDetailsEdit eventDetails,
                                     @PathParam("event_id") String eventId,
                                     @PathParam("company_id") String companyId,
                                     @Context HttpHeaders httpHeaders) {

        String requestId = httpHeaders.getHeaderString("x-transactionid");
        String userId = httpHeaders.getHeaderString("x-user-uuid");

        logInfoWithTransactionId(requestId, "got new request to update event details");

        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        if (userId == null || userId.isEmpty()) {
            logInfoWithTransactionId(requestId, "no user id submitted");
            throw new WebApplicationException("not authorized", 401);
        }

        logInfoWithTransactionId(requestId, "trying to validate authorization from guardian");

        String host = System.getenv("GUARDIAN_URL") + userId + '/' + companyId;

        GuardianClient client = new GuardianClient(host, requestId);

        Event event;
        try {
            if (!client.getUserPermission(3)) {
                logInfoWithTransactionId(requestId,"user has not the required permission");
                throw new WebApplicationException("not authorized", 401);
            }
            event = eventDAO.fetchEventById(companyId, eventId);
        } catch (UnirestException e) {
            logInfoWithTransactionId(requestId, e.toString());
            logInfoWithTransactionId(requestId, "not possible to verify user permission");
            throw new WebApplicationException("not possible to verify user permission", 401);
        }
        if (event == null) {
            throw new WebApplicationException("event does not exist", 404);
        }

        if(eventDetails.getMultiLanguage()) {
            if(!event.getMultiLanguage()) {
                eventDAO.updateEventMultiLanguage(true, companyId, eventId);
            }
            if (eventDetails.getDefaultLanguageId() != event.getDefaultLanguageId()) {
                eventDAO.updateEventDefaultLanguage(eventDetails.getDefaultLanguageId(),
                        companyId, eventId);
            }
            eventDAO.removeLanguagesFromEvent(eventId, userId);

            for (EventLanguages language : eventDetails.getLanguages()) {
                if (!eventDetails.getDefaultLanguageId().equals(language.getLanguageId())) {
                    eventDAO.createEventLanguage(event.getId(), language.getLanguageId());
                }
            }

        } else {
            eventDAO.removeLanguagesFromEvent(eventId, userId);
            if(event.getMultiLanguage()) {
                eventDAO.updateEventMultiLanguage(false, companyId, eventId);
            }
        }

        try {
            eventDAO.removeCurrenciesFromEvent(eventId, userId);
            for (EventCurrencies currency : eventDetails.getCurrencies()) {
                Long eventCurrencyId = eventDAO.createEventCurrency(event.getId(), currency.getCurrencyId());
                if(eventCurrencyId == null) {
                    throw new WebApplicationException("not possible to insert event currencies", 500);
                }
            }
        } catch (Exception e) {
            logInfoWithTransactionId(requestId, "not possible to update database");
            log.warn(e.toString());
            log.warn(e.getMessage());
            throw new WebApplicationException("not possible to communicate with database", 500);
        }

        JSONObject response = new JSONObject().put("STATUS", "OK")
                .put("status_code", 200)
                .put("message", "successfully updated event details")
                .put("transaction_id", requestId);
        return Response.status(200).entity(response.toString()).build();
    }

    @POST
    @ExceptionMetered
    @Timed
    @Path("/event/create")
    public Response newEvent(@NotNull @Valid EventCreation event,
                             @Context HttpHeaders httpHeaders) {
        String requestId = httpHeaders.getHeaderString("x-transactionid");
        String userId = httpHeaders.getHeaderString("x-user-uuid");

        logInfoWithTransactionId(requestId, "got new request to create event");

        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        if (userId == null || userId.isEmpty()) {
            logInfoWithTransactionId(requestId, "no user id submitted");
            throw new WebApplicationException("not authorized", 401);
        }

        logInfoWithTransactionId(requestId, "trying to validate authorization from guardian");

        String host = System.getenv("GUARDIAN_URL") + userId + '/' + event.getCompanyId();

        GuardianClient client = new GuardianClient(host, requestId);

        try {
            if (!client.getUserPermission(1)) {
                logInfoWithTransactionId(requestId,"user has not the required permission");
                throw new WebApplicationException("not authorized", 401);
            }
        } catch (UnirestException e) {
            logInfoWithTransactionId(requestId, e.toString());
            logInfoWithTransactionId(requestId, "not possible to verify user permission");
            throw new WebApplicationException("not possible to verify user permission", 401);
        }

        logInfoWithTransactionId(requestId, "successfully validated event data");

        validateEvent(event, requestId);

        logInfoWithTransactionId(requestId, "successfully validated event data");

        String eventUuid = UUID.randomUUID().toString();

        try {
            logInfoWithTransactionId(requestId, "trying to insert new Event into Event Database");
            log.info(event.getEndDate().toString());
            log.info(String.valueOf(event.getEndDate().getMillis()));
            Long eventId = eventDAO.createEvent(event.getEventName(), event.getCompanyId(), eventUuid, event.getEventLocation().getStreetNumber().get(),
                    event.getEventLocation().getStreet(), event.getEventLocation().getCity(), event.getEventLocation().getCountryId(), event.getEventLocation().getZip(),
                    event.getEventLocation().getLatitude(), event.getEventLocation().getLongitude(),
                    event.getVenue(), event.getMultiDayEvent(), event.getStartDate(),
                    event.getEndDate(),
                    event.getMultiLanguage(),
                    event.getDefaultLanguageId().get(),
                    event.getDefaultCurrencyId());

            logInfoWithTransactionId(requestId, "tutut");

            if (event.getMultiLanguage()) {
                if(event.getLanguageIds().isPresent() && !event.getLanguageIds().get().isEmpty()) {
                    for (String languageId : event.getLanguageIds().get()) {
                        eventDAO.createEventLanguage(eventId, languageId);
                    }
                } else {
                    logInfoWithTransactionId(requestId, "event is a multi language event but no langauges submitted");
                    throw new WebApplicationException(400);
                }
            }

            logInfoWithTransactionId(requestId, "successfully inserted event into datbase");

            JSONObject response = new JSONObject()
                    .put("STATUS", "OK")
                    .put("TRANSACTION_ID", requestId)
                    .put("MESSAGE", "successfully created new event")
                    .put("STATUS_CODE", 200);
            return Response.status(200).entity(response.toString()).build();
        } catch (Exception e) {
            logInfoWithTransactionId(requestId, "not possible to write into database");
            log.error(e.getMessage());
            log.error("There was an error duriing database writing: {}", e);
            return Response.status(500).build();
        }
    }

    private boolean validateEvent(EventCreation event, String requestId) throws WebApplicationException {

        if (!event.getMultiDayEvent() && (event.getStartDate() == null || event.getEndDate() == null)) {
            logInfoWithTransactionId(requestId, "no end or start submitted");
            throw new WebApplicationException("You have to submit a end date", 400);
        } else {
            if (event.getStartDate().isAfter(event.getEndDate())) {
                logInfoWithTransactionId(requestId, "start date can not be after end date");
                throw new WebApplicationException("the start date can not be after the end date", 400);
            }
        }

        if(event.getMultiLanguage() && event.getMultiLanguage() && (!event.getDefaultLanguageId().isPresent() || (!event.getLanguageIds().isPresent() && event.getLanguageIds().get().isEmpty()))) {
            logInfoWithTransactionId(requestId, "multi day event is selected but no data submitted");
            throw new WebApplicationException("you have to submit a default Language ID and at least one Language if you create multi language events", 400);
        }

        /*if (event.getMultiDayEvent()) {
            if (event.getEventDates().size() > 0) {
                for (EventDates dates : event.getEventDates()) {
                    logInfoWithTransactionId(requestId, "iterating over event dates and check time");
                    if (dates.getStartDate().isAfter(dates.getEndDate())) {
                        logInfoWithTransactionId(requestId, "at least one event date has the start date after the end date");
                        throw new WebApplicationException("the start date cant be after the end date", 400);
                    }
                }
            } else {
                logInfoWithTransactionId(requestId, "no dates for the multi day event specified");
                throw new WebApplicationException("no dates for the multi day event specified", 400);
            }
        }*/
        return true;

    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("%s: %s", transactionId, message));
    }
}
