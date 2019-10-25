package dev.samstevens.automaton.payload;

import lombok.*;
import java.time.Instant;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class Payload {
    private final String type;
    @NonNull
    private final String message;
    private final String channel;
    private final String sender;
    private final Instant timestamp;
    private final boolean isMention;
}
