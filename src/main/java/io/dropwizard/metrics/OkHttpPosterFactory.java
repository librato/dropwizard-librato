package io.dropwizard.metrics;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.librato.metrics.client.IPoster;
import com.librato.metrics.client.OkHttpPoster;

@JsonTypeName("okhttp")
public class OkHttpPosterFactory implements PosterFactory {

    @Override
    public IPoster createPoster() {
        return new OkHttpPoster();
    }
}
