package io.dropwizard.metrics;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.librato.metrics.client.DefaultPoster;
import com.librato.metrics.client.IPoster;

@JsonTypeName("default")
public class DefaultPosterFactory implements PosterFactory {

    @Override
    public IPoster createPoster() {
        return new DefaultPoster();
    }
}
