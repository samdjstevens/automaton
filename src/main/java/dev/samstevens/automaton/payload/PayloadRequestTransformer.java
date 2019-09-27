package dev.samstevens.automaton.payload;

import javax.servlet.http.HttpServletRequest;

public interface PayloadRequestTransformer {
    boolean shouldTransformRequest(HttpServletRequest request);
    Payload transformRequest(HttpServletRequest request) throws PayloadRequestTransformingException;
}
