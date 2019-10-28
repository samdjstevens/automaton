package dev.samstevens.automaton.message;

import com.github.seratch.jslack.api.methods.MethodsClient;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SlackMessageSenderTest {

    @Test
    public void testCanRetrieveDefaultChannel() {
        MethodsClient methodsClient = mock(MethodsClient.class);
        MessageSender sender = new SlackMessageSender(methodsClient, "my-channel");

        assertEquals("my-channel", sender.getDefaultChannel());
    }

    @Test
    public void testSendingMessageWithoutChannelUsesDefault() throws IOException, SlackApiException {
        MethodsClient methodsClient = mock(MethodsClient.class);
        MessageSender sender = new SlackMessageSender(methodsClient, "my-channel");

        sender.send("hello world");

        verifyMessage(methodsClient, "hello world", "my-channel");
    }

    @Test
    public void testSendingMessageWithChannel() throws IOException, SlackApiException {
        MethodsClient methodsClient = mock(MethodsClient.class);
        MessageSender sender = new SlackMessageSender(methodsClient, "my-channel");

        sender.send("hello world", "another-channel");

        verifyMessage(methodsClient, "hello world", "another-channel");
    }

    @Test(expected = RuntimeException.class)
    public void testSlackExceptionIsConvertedToRuntimeException() throws IOException, SlackApiException {
        MethodsClient methodsClient = mock(MethodsClient.class);
        when(methodsClient.chatPostMessage((ChatPostMessageRequest) any())).thenThrow(SlackApiException.class);

        MessageSender sender = new SlackMessageSender(methodsClient, "my-channel");
        sender.send("hello world");
    }

    private void verifyMessage(MethodsClient methodsClient, String text, String channel) throws IOException, SlackApiException {
        // Bad verification as this will fail if the implementation changes at all
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .text(text).channel(channel).build();

        verify(methodsClient, times(1)).chatPostMessage(request);
    }
}
