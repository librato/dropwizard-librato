package io.dropwizard.metrics;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.librato.metrics.client.IPoster;
import io.dropwizard.jackson.Discoverable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface PosterFactory extends Discoverable {
    IPoster createPoster();

}

