package dev.samstevens.automaton.spring;

import dev.samstevens.automaton.Automaton;
import dev.samstevens.automaton.AutomatonBuilder;
import dev.samstevens.automaton.action.provider.ActionProvider;
import dev.samstevens.automaton.action.provider.annotate.MethodActionProvider;
import dev.samstevens.automaton.driver.Driver;
import dev.samstevens.automaton.memory.Memory;
import dev.samstevens.automaton.memory.ShortTermMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan("dev.samstevens.automaton.spring")
public class AutomatonConfig {

    @Autowired(required = false)
    Memory memory;

    @Autowired(required = false)
    List<Driver> drivers;

    @Autowired(required = false)
    @Actions
    List<Object> actions;

    @Bean
    public FilterRegistrationBean<ChallengeRequestFilter> loggingFilter(){
        FilterRegistrationBean<ChallengeRequestFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new ChallengeRequestFilter());
        registrationBean.addUrlPatterns("/");

        return registrationBean;
    }

    @Bean
    public List<ActionProvider> getAutomatonActionProviders() {
        List<ActionProvider> list = new ArrayList<>();

        for (Object bean : actions) {
            list.add(new MethodActionProvider(bean));
        }

        return list;
    }

    @Bean
    public Automaton getAutomaton(List<ActionProvider> actionProviders) {
        AutomatonBuilder builder = new AutomatonBuilder();

        // Give the bot short term memory if no Memory bean specified by
        // the consuming spring app
        if (memory == null) {
            builder.memory(new ShortTermMemory());
        } else {
            builder.memory(memory);
        }

        for (ActionProvider actionprovider : actionProviders) {
            builder.actionProvider(actionprovider);
        }

        for (Driver driver : drivers) {
            builder.driver(driver);
        }

        return builder.build();
    }
}
