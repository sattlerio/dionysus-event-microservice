package io.sattler.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuardianClient {

    private final static Logger log = LoggerFactory.getLogger(GuardianClient.class);

    private String guardianHost;
    private String transactionId;

    public GuardianClient(String host, String transactionId) {
        this.guardianHost = host;
        this.transactionId = transactionId;
    }

    public Boolean getUserPermission(Integer requiredPermission) throws UnirestException {
        log.info("prepared guardian client and going to request to host");
        HttpResponse<JsonNode> getUserPermission = Unirest.get(this.guardianHost)
                .header("Content-Type", "application/json")
                .header("accept", "application/json")
                .asJson();
        log.info("successfully fetched data from host");

        Integer httpStatus = getUserPermission.getStatus();

        if (httpStatus != 200) {
            log.info("not possible to get user permission because of http error " + httpStatus);
            throw new UnirestException("not possible to fetch user permission because of http error " + httpStatus);
        }
        JSONObject statusBody = getUserPermission.getBody().getObject();

        String status = statusBody.getString("status");

        if (status.equals("OK")) {
            JSONObject data = statusBody.getJSONObject("data");

            Integer permission = data.getInt("user_permission");

            if (permission >= 0 && permission <= requiredPermission) {
                return true;
            }
        }

        return false;
    }
}
