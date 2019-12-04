package dev.samstevens.automaton.spring;

import dev.samstevens.automaton.Automaton;
import dev.samstevens.automaton.payload.PayloadRequestTransformingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AutomatonController {

    @Autowired
    private Automaton automaton;

    @RequestMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String doAll(HttpServletRequest request) throws PayloadRequestTransformingException {
        automaton.handleRequest(request);

        return "OK";
    }
}
