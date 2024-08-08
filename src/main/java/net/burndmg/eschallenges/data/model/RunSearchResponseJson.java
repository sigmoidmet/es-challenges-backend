package net.burndmg.eschallenges.data.model;

public record RunSearchResponseJson (
   String hitsJsonArray,
   String aggregationsMap
) {

    public RunSearchResponseJson {
        if (aggregationsMap == null) {
            aggregationsMap = "{}";
        }
    }
}
