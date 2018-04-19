package io.sattler.resources;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.codahale.metrics.annotation.Timed;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.sattler.client.GuardianClient;
import io.sattler.db.Event;
import io.sattler.db.EventDAO;
import io.sattler.db.EventLanguages;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.UUID;

@Path("/permission")
@Produces(MediaType.APPLICATION_JSON)
public class EventPermissionResource {


    private final EventDAO eventDAO;

    private final static Logger log = LoggerFactory.getLogger(EventPermissionResource.class);

    public EventPermissionResource(EventDAO eventDAO) { this.eventDAO = eventDAO; }

    @GET
    @ExceptionMetered
    @Timed
    @Path("/{event_id}/{company_id}")
    public Response checkIfUserBelongsToEvent(@PathParam("event_id") String eventId,
                                              @PathParam("company_id") String companyId,
                                              @Context HttpHeaders httpHeaders) {
        String requestId = httpHeaders.getHeaderString("x-transactionid");
        String userId = httpHeaders.getHeaderString("x-user-uuid");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }

        logInfoWithTransactionId(requestId, "got request to check user permission");

        if (userId == null || userId.isEmpty()) {
            logInfoWithTransactionId(requestId, "no user id submitted");
            throw new WebApplicationException("no user submitted", 401);
        }

        String host = System.getenv("GUARDIAN_URL") + userId + '/' + companyId;
        GuardianClient client = new GuardianClient(host, requestId);

        try {
            Boolean permission = client.getUserPermission(1);

            JSONObject response = new JSONObject();
            if(permission) {
                response
                        .put("STATUS", "OK")
                        .put("message", "user is allowed to access event")
                        .put("access", true)
                        .put("request_id", requestId)
                        .put("status_code", 200);
                return Response.status(200).entity(response.toString()).build();
            } else {
                Long eventAccess = eventDAO.userPermission(eventId, userId);
                if (eventAccess == 1) {
                    response
                            .put("STATUS", "OK")
                            .put("message", "user is allowed to access event")
                            .put("access", true)
                            .put("request_id", requestId)
                            .put("status_code", 200);
                } else {
                    response
                            .put("STATUS", "OK")
                            .put("message", "user is not to access event")
                            .put("access", false)
                            .put("request_id", requestId)
                            .put("status_code", 200);
                }
                return Response.status(500).entity(response.toString()).build();
            }


        } catch (UnirestException e) {
            logInfoWithTransactionId(requestId, "not possible to communicate with Guardian abort with 500");
            log.error(e.toString());
            log.error(e.getMessage());
            throw new WebApplicationException("permission error", 500);
        }

    }

    private void logInfoWithTransactionId(String requestId, String message) {
        log.info(String.format("%s: %s", requestId, message));
    }
}
