package dev.samstevens.automaton.payload.adapter;

import com.google.gson.Gson;
import dev.samstevens.automaton.payload.Payload;
import dev.samstevens.automaton.payload.PayloadRequestTransformer;
import dev.samstevens.automaton.payload.PayloadRequestTransformingException;
import lombok.Getter;
import lombok.Setter;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.time.Instant;

public class SlackPayloadRequestTransformer implements PayloadRequestTransformer {

    @Getter
    @Setter
    private class SlackJsonPayload {
        private String type;
        private String channel;
        private String text;
        private String user;
        private String ts;
    }

    private final Gson gson;

    public SlackPayloadRequestTransformer(Gson gson) {
        this.gson = gson;
    }

    /**
     * Identify a request is a Slack payload by looking for a header they send with requests.
     */
    @Override
    public boolean shouldTransformRequest(HttpServletRequest request) {
        return request.getHeader("X-Slack-Request-Timestamp") != null;
    }

    @Override
    public Payload transformRequest(HttpServletRequest request) throws PayloadRequestTransformingException {

        SlackJsonPayload slackPayload;
        try (BufferedReader reader = request.getReader()) {
            slackPayload = gson.fromJson(reader, SlackJsonPayload.class);
        } catch (Exception e) {
            throw new PayloadRequestTransformingException();
        }

        return Payload.builder()
            .type(slackPayload.getType())
            .channel(slackPayload.getChannel())
            .sender(slackPayload.getUser())
            .timestamp(timestampFromSlackPayload(slackPayload))
            .message(slackPayload.getText())
            .build();
    }

    private Instant timestampFromSlackPayload(SlackJsonPayload payload) {
        String epoch = payload.getTs().split("\\.")[0];

        return Instant.ofEpochSecond(Long.valueOf(epoch));
    }
}
