package dev.samstevens.automaton.payload.driver;

import com.google.gson.Gson;
import dev.samstevens.automaton.payload.Payload;
import dev.samstevens.automaton.payload.PayloadRequestTransformer;
import dev.samstevens.automaton.payload.PayloadRequestTransformingException;
import lombok.Getter;
import lombok.Setter;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.time.Instant;
import java.util.regex.Pattern;

public class SlackPayloadRequestTransformer implements PayloadRequestTransformer {

    @Getter
    @Setter
    private class SlackJsonPayload {
        @Getter
        @Setter
        class SlackEventJsonPayload {
            private String type;
            private String channel;
            private String text;
            private String user;
            private String ts;
        }
        private String type;
        private SlackEventJsonPayload event;
    }

    private final Gson gson;
    private final Pattern botNameMentionPattern;

    public SlackPayloadRequestTransformer(Gson gson, String botName) {
        this.gson = gson;
        this.botNameMentionPattern = Pattern.compile("@" + botName);
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
            throw new PayloadRequestTransformingException(e);
        }

        // no event found in the request body
        if (slackPayload == null || slackPayload.getEvent() == null) {
            return null;
        }

        return Payload.builder()
            .type(slackPayload.getEvent().getType())
            .channel(slackPayload.getEvent().getChannel())
            .sender(slackPayload.getEvent().getUser())
            .timestamp(timestampFromSlackPayload(slackPayload))
            .message(slackPayload.getEvent().getText())
            .isMention(payloadIsBotMention(slackPayload))
            .build();
    }

    private Instant timestampFromSlackPayload(SlackJsonPayload payload) {
        if (payload.getEvent().getTs() == null) {
            return null;
        }
        
        String epoch = payload.getEvent().getTs().split("\\.")[0];

        return Instant.ofEpochSecond(Long.valueOf(epoch));
    }

    private boolean payloadIsBotMention(SlackJsonPayload payload) {
        return payload.getEvent().getType().equals("app_mention") && botNameMentionPattern.matcher(payload.getEvent().getText()).find();
    }
}
