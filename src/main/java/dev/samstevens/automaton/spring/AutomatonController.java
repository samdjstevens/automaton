package dev.samstevens.automaton.spring;

import com.google.gson.Gson;
import dev.samstevens.automaton.Automaton;
import dev.samstevens.automaton.payload.PayloadRequestTransformingException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Controller
public class AutomatonController {

    @Autowired
    private Automaton automaton;

    @Data
    public class ChallengePayload {
        private String token;
        private String challenge;
        private String type;
    }

    @RequestMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String doAll(HttpServletRequest request) throws PayloadRequestTransformingException, IOException {

        // challenge marshalling....
//        {
//            "token": "Jhj5dZrVaK7ZwHHjRyZWjbDl",
//                "challenge": "3eZbrw1aBm2rZgRNFdxV2595E9CY3gmdALWMmHkvFXO7tYXAYM8P",
//                "type": "url_verification"
//        }

//        HttpServletRequest r2 = new HttpServletRequestWrapper(request);
//        ChallengePayload challenge;
//        Gson gson = new Gson();
//        try (BufferedReader reader = r2.getReader()) {
//            challenge = gson.fromJson(reader, ChallengePayload.class);
//        } catch (Exception e) {
//            throw e;
//        }
//
//        if (challenge != null && challenge.getChallenge() != null) {
//            return challenge.getChallenge();
//        }

        automaton.handleRequest(request);
        return "OK";
    }
}
