package dev.samstevens.automaton.spring;

import dev.samstevens.automaton.Automaton;
import dev.samstevens.automaton.AutomatonBuilder;
import dev.samstevens.automaton.action.provider.ActionProvider;
import dev.samstevens.automaton.action.provider.annotate.MethodActionProvider;
import dev.samstevens.automaton.driver.Driver;
import dev.samstevens.automaton.memory.Memory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
@ComponentScan("dev.samstevens.automaton.spring")
public class AutomatonConfig {

    @Autowired
    Memory memory;

    @Autowired
    List<Driver> drivers;

    @Autowired
    @Actions
    List<Object> actions;


    @Bean
    public List<ActionProvider> getActionProviders() {
        List<ActionProvider> list = new ArrayList<>();


        for (Object bean : actions) {
            list.add(new MethodActionProvider(bean));
        }

        return list;
    }
    @Bean
    public Automaton getAutomaton() {

        AutomatonBuilder b = new AutomatonBuilder()
                .memory(memory);

        for (ActionProvider ap : getActionProviders()) {
            b.actionProvider(ap);
        }

        for (Driver d : drivers) {
            b.driver(d);
        }

        return b.build();
    }

}
