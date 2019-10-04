package dev.samstevens.automaton.driver;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.MethodsClient;
import com.google.gson.Gson;
import dev.samstevens.automaton.message.MessageSender;
import dev.samstevens.automaton.message.SlackMessageSender;
import dev.samstevens.automaton.payload.PayloadRequestTransformer;
import dev.samstevens.automaton.payload.driver.SlackPayloadRequestTransformer;

public class SlackDriver implements Driver {
    private final PayloadRequestTransformer transformer = new SlackPayloadRequestTransformer(new Gson());
    private final MethodsClient methodsClient;

    public SlackDriver(String token) {
        methodsClient = Slack.getInstance().methods(token);
    }

    @Override
    public PayloadRequestTransformer getPayloadRequestTransformer() {
        return transformer;
    }

    @Override
    public MessageSender createMessageSender(String channel) {
        return new SlackMessageSender(methodsClient, channel);
    }
}
