package dev.samstevens.automaton.payload

import spock.lang.Specification
import java.time.Instant

class PayloadSpec extends Specification {

    def "can build a payload object" () {
        given:
            def now = Instant.now()
            def payload = Payload.builder()

        when:
            payload
                .channel("the-channel")
                .message("Hello, world!")
                .sender("MrUser")
                .type("direct_message")
                .timestamp(now)
                .build()

        then:
            payload != null
            payload.channel == "the-channel"
            payload.message == "Hello, world!"
            payload.sender == "MrUser"
            payload.type == "direct_message"
            payload.timestamp == now
    }

    def "if a message is not set an exception is thrown" () {
        given:
            def builder =  Payload.builder()
                .channel("the-channel")
                .sender("MrUser")
                .type("direct_message")

        when:
            builder.build()

        then:
            thrown(NullPointerException)
    }
}
