package dev.samstevens.automaton.message;

import com.github.seratch.jslack.api.methods.MethodsClient;

public class SlackMessageSender implements MessageSender {

    private MethodsClient methodsClient;
    private String channel;

    public SlackMessageSender(MethodsClient methodsClient, String channel) {
        this.methodsClient = methodsClient;
        this.channel = channel;
    }

    public String getDefaultChannel() {
        return channel;
    }

    @Override
    public void send(String message) {
        send(message, this.channel);
    }

    @Override
    public void send(String message, String channel) {
        try {
            methodsClient.chatPostMessage(req -> req.channel(channel).text(message));
        } catch (Exception e) {
            throw new RuntimeException("failed to send message", e);
        }
    }
}
