package edu.stevens.cs522.chatserver.entities;

import java.time.Instant;

public class TimestampConverter {
    public static Instant deserialize(String value) {
        return value == null ? null : Instant.parse(value);
    }

    public static String serialize(Instant timestamp) {
        return timestamp == null ? null : timestamp.toString();
    }
}
