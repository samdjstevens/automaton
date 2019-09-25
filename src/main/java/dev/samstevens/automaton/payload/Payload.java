package dev.samstevens.automaton.payload;

import com.sun.istack.internal.NotNull;
import lombok.*;
import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class Payload {
    private final String type;
    @NotNull
    private final String message;
    private final String channel;
    private final String sender;
    private final Instant timestamp;
}
