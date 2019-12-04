package dev.samstevens.automaton.spring;

import com.google.gson.Gson;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Controller
public class SlackChallengeController {

    private final Gson gson = new Gson();

    @Data
    private class ChallengePayload {
        private String token;
        private String challenge;
        private String type;
    }

    @RequestMapping("/challenge")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String doAll(HttpServletRequest request) throws IOException {
        ChallengePayload challenge;
        try (BufferedReader reader = request.getReader()) {
            challenge = gson.fromJson(reader, ChallengePayload.class);
        }

        if (challenge != null && challenge.getChallenge() != null) {
            return challenge.getChallenge();
        }

        return "";
    }
}
