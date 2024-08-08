package net.burndmg.eschallenges.data.dto.run;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.Map;

public record RunSearchHit(@JsonAlias("_source") Map<String, Object> source) {}
