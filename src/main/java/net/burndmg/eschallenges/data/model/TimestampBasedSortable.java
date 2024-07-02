package net.burndmg.eschallenges.data.model;

@SuppressWarnings("unused") // we just want to ensure that such field exists
public interface TimestampBasedSortable {

    String TIMESTAMP_FIELD = "timestamp";

    String timestamp();
}
