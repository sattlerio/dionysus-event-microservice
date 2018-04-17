package io.sattler.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.sattler.api.EventCreation;
import io.sattler.client.GuardianClient;
import io.sattler.db.Event;
import io.sattler.db.EventDAO;
import io.sattler.db.EventDates;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class EventResource {

    private final EventDAO eventDAO;

    private final static Logger log = LoggerFactory.getLogger(EventResource.class);

    public EventResource(EventDAO eventDAO) { this.eventDAO = eventDAO; }

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

        log.info(event.getCompanyId());
        log.info(host);
        log.info(event.getStartDate().toString());
        log.info(event.getEndDate().toString());

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
