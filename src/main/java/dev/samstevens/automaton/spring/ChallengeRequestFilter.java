package dev.samstevens.automaton.spring;

import com.google.gson.Gson;
import dev.samstevens.automaton.CustomHttpServletRequestWrapper;
import lombok.Data;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
@Order(1)
public class ChallengeRequestFilter implements Filter {

    private final Gson gson = new Gson();

    @Data
    private class ChallengePayload {
        private String token;
        private String challenge;
        private String type;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest wrappedRequest = new CustomHttpServletRequestWrapper((HttpServletRequest) servletRequest);

        ChallengePayload challenge;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(wrappedRequest.getInputStream()))) {
            challenge = gson.fromJson(reader, ChallengePayload.class);
        }

        if (challenge != null && challenge.getChallenge() != null) {
            System.out.println("passed challenge");
            servletResponse.getWriter().write(challenge.getChallenge());
            return;
        }

        try (InputStream stream = wrappedRequest.getInputStream()) {
            String body = new BufferedReader(new InputStreamReader(stream))
                    .lines().collect(Collectors.joining(System.lineSeparator()));
            System.out.println(body);
        } catch (Exception e) {
            throw new RuntimeException("Could not read request body.", e);
        }

        filterChain.doFilter(wrappedRequest, servletResponse);
    }

    @Override
    public void destroy() {
        //
    }
}
