package io.sattler.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.sattler.client.GuardianClient;
import io.sattler.db.Event;
import io.sattler.db.EventDAO;
import io.sattler.db.EventDates;
import org.joda.time.DateTime;
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
    public Response createNewEvent(@NotNull @Valid Event event,
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

        validateEvent(event, requestId);

        logInfoWithTransactionId(requestId, "successfully validated event data");

        Integer eventId = eventDAO.createEvent("tutu", "VC00001", UUID.randomUUID().toString(), "djsladja", "jdskaljdaslk",
        "jl", "1012GK", "1012", 6.00D, 7.00D, "dddd", false, DateTime.now(), DateTime.now());

        logInfoWithTransactionId(requestId, "successfully inserted event in db");

        return Response.status(200).entity(event).build();
    }

    private boolean validateEvent(Event event, String requestId) throws WebApplicationException {

        if (!event.getMultiDayEvent() && (event.getStartDate() == null || event.getEndDate() == null)) {
            logInfoWithTransactionId(requestId, "no end or start submitted");
            throw new WebApplicationException("You have to submit a end date", 400);
        } else {
            if (event.getStartDate().isAfter(event.getEndDate())) {
                logInfoWithTransactionId(requestId, "start date can not be after end date");
                throw new WebApplicationException("the start date can not be after the end date", 400);
            }
        }

        if (event.getMultiDayEvent()) {
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
        }
        return true;

    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("%s: %s", transactionId, message));
    }
}
