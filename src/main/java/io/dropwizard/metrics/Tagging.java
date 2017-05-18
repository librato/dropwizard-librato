package io.dropwizard.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.Map;

public class Tagging {
    @JsonProperty("enabled")
    public Boolean enabled = false;

    @JsonProperty("static")
    public Map<String, String> staticTags = Collections.emptyMap();

    @JsonProperty("environment")
    public Map<String, String> environmentTags = Collections.emptyMap();
}
