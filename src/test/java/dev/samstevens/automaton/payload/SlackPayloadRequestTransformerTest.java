package dev.samstevens.automaton.payload;

import com.google.gson.Gson;
import dev.samstevens.automaton.payload.driver.SlackPayloadRequestTransformer;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.Instant;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class SlackPayloadRequestTransformerTest {

    @Test
    public void testIfShouldTransformRequest() throws IOException {
        PayloadRequestTransformer payloadRequestTransformer = new SlackPayloadRequestTransformer(new Gson());

        assertTrue(payloadRequestTransformer.shouldTransformRequest(getSlackRequest()));
        assertFalse(payloadRequestTransformer.shouldTransformRequest(getOtherRequest()));
    }

    @Test
    public void testParsesRequestCorrectly() throws PayloadRequestTransformingException, IOException {
        PayloadRequestTransformer payloadRequestTransformer = new SlackPayloadRequestTransformer(new Gson());

        Payload payload = payloadRequestTransformer.transformRequest(getSlackRequest());

        assertEquals("message", payload.getType());
        assertEquals("C2147483705", payload.getChannel());
        assertEquals("U2147483697", payload.getSender());
        assertEquals("Hello world", payload.getMessage());
        assertEquals(Instant.ofEpochSecond(1355517523), payload.getTimestamp());
    }

    @Test(expected = PayloadRequestTransformingException.class)
    public void testMalformedRequestThrowsException() throws PayloadRequestTransformingException, IOException {
        PayloadRequestTransformer payloadRequestTransformer = new SlackPayloadRequestTransformer(new Gson());

        payloadRequestTransformer.transformRequest(getBadRequest());
    }

    private HttpServletRequest getSlackRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(readRequestStub("/payload/slack_request_body.json"))));
        when(request.getHeader("X-Slack-Request-Timestamp")).thenReturn(Instant.now().toString());

        return request;
    }

    private HttpServletRequest getBadRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(readRequestStub("/payload/malformed_request_body.json"))));
        when(request.getHeader("X-Slack-Request-Timestamp")).thenReturn(Instant.now().toString());

        return request;
    }

    private HttpServletRequest getOtherRequest() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getReader()).thenReturn(
                new BufferedReader(new StringReader(readRequestStub("/payload/other_request_body.json"))));

        return request;
    }

    /**
     * Helper method to read the stub file request JSON.
     */
    private String readRequestStub(String filename) throws IOException {
        InputStream stream = this.getClass().getResourceAsStream(filename);
        StringBuilder resultStringBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }

        return resultStringBuilder.toString().trim();
    }
}
