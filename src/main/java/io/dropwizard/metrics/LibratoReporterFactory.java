package io.dropwizard.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.Optional;
import com.librato.metrics.DefaultHttpPoster;
import com.librato.metrics.HttpPoster;
import com.librato.metrics.LibratoReporter;
import io.dropwizard.util.Duration;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@JsonTypeName("librato")
public class LibratoReporterFactory extends BaseReporterFactory {
    @NotNull
    @JsonProperty
    private String username;

    @NotNull
    @JsonProperty
    private String token;

    @NotNull
    @JsonProperty
    private String source;

    @JsonProperty
    private Long timeout;

    @JsonProperty
    private String prefix;

    @JsonProperty
    private String name;

    @JsonProperty
    private String libratoUrl;

    @JsonProperty
    private String sourceRegex;

    @JsonProperty
    private String prefixDelimiter;

    @JsonProperty
    private Boolean deleteIdleStats;

    @JsonProperty
    @NotNull
    private Optional<Duration> frequency = Optional.of(Duration.seconds(60));

    @Override
    public Optional<Duration> getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(Optional<Duration> frequency) {
        this.frequency = frequency;
    }

    public ScheduledReporter build(MetricRegistry registry) {
        LibratoReporter.Builder builder = LibratoReporter.builder(registry, username, token, source)
                .setRateUnit(getRateUnit())
                .setDurationUnit(getDurationUnit())
                .setFilter(getFilter());
        if (sourceRegex != null) {
            Pattern sourceRegexPattern = Pattern.compile(sourceRegex);
            builder.setSourceRegex(sourceRegexPattern);
        }
        if (libratoUrl != null) {
            HttpPoster httpPoster = new DefaultHttpPoster(libratoUrl, username, token);
            builder.setHttpPoster(httpPoster);
        }
        if (prefix != null) {
            builder.setPrefix(prefix);
        }
        if (name != null) {
            builder.setName(name);
        }
        if (timeout != null) {
            builder.setTimeout(timeout, TimeUnit.SECONDS);
        }
        if (prefixDelimiter != null) {
            builder.setPrefix(prefixDelimiter);
        }
        if (deleteIdleStats != null) {
            builder.setDeleteIdleStats(deleteIdleStats);
        }
        return builder.build();
    }
}

