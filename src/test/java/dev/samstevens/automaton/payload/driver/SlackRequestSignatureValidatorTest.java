package dev.samstevens.automaton.payload.driver;

import org.junit.Test;
import static org.junit.Assert.*;

public class SlackRequestSignatureValidatorTest {

    @Test
    public void testSignatureValidation() {
        String signingSecret = "21e958ece7004774095d652331223c55";
        SlackRequestSignatureValidator validator = new SlackRequestSignatureValidator(signingSecret);

        String requestTimestamp = "1572975949870";
        String requestBody=  "{\n" +
                "  \"token\": \"xxxxxxxxx\",\n" +
                "  \"team_id\": \"xxxxxxxxx\",\n" +
                "  \"api_app_id\": \"xxxxxxxxx\",\n" +
                "  \"event\": {\n" +
                "    \"type\": \"message\",\n" +
                "    \"channel\": \"C2147483705\",\n" +
                "    \"user\": \"U2147483697\",\n" +
                "    \"text\": \"Hello world\",\n" +
                "    \"ts\": \"1355517523.000005\"\n" +
                "  },\n" +
                "  \"type\": \"event_callback\",\n" +
                "  \"event_id\": \"Ev0LAN670R\",\n" +
                "  \"event_time\": 1515449522000016\n" +
                "}";
        String signature = "v0=a10858df44ef8b0c40b2272272d1e90ab76a70c4de1b1daf2df613ddfc847a35";

        assertTrue(validator.isValid(requestTimestamp, requestBody, signature));
    }
}
