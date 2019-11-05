package dev.samstevens.automaton.payload.driver;

import com.github.seratch.jslack.app_backend.SlackSignature;
import java.time.Instant;

public class SlackRequestSignatureValidator {

    private final SlackSignature.Verifier verifier;

    public SlackRequestSignatureValidator(String signingSecret) {
        verifier = new SlackSignature.Verifier(new SlackSignature.Generator(signingSecret));
    }

    public boolean isValid(String requestTime, String requestBody, String requestSignature) {
        return verifier.isValid(requestTime, requestBody, requestSignature, Instant.now().toEpochMilli());
    }
}
