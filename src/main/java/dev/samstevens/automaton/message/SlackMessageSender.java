package dev.samstevens.automaton.message;

import com.github.seratch.jslack.api.methods.MethodsClient;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import java.io.IOException;

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
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .text(message)
                .channel(channel)
                .build();

            methodsClient.chatPostMessage(request);
        } catch (IOException | SlackApiException e) {
            throw new RuntimeException("failed to send message", e);
        }
    }
}
