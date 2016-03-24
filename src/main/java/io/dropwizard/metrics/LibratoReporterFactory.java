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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@JsonTypeName("librato")
public class LibratoReporterFactory extends BaseReporterFactory {
    private static final Logger log = LoggerFactory.getLogger(LibratoReporterFactory.class);

    @JsonProperty
    private String username;

    @JsonProperty
    private String token;

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

    @JsonProperty
    @NotNull
    private List<String> metricWhitelist = Collections.emptyList();

    @JsonProperty
    @NotNull
    private List<String> metricBlacklist = Collections.emptyList();

    @Override
    public Optional<Duration> getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(Optional<Duration> frequency) {
        this.frequency = frequency;
    }

    public ScheduledReporter build(MetricRegistry registry) {
        if (source == null) {
            source = System.getenv("LIBRATO_SOURCE");
        }
        if (username == null) {
            username = System.getenv("LIBRATO_USERNAME");
        }
        if (token == null) {
            token = System.getenv("LIBRATO_TOKEN");
        }

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
        if (!metricWhitelist.isEmpty() && !metricBlacklist.isEmpty()) {
            log.error("Both whitelist and blacklist cannot be supplied");
        } else {
            try {
                if (!metricWhitelist.isEmpty()) {
                    Set<LibratoReporter.ExpandedMetric> expandedWhitelist = toExpandedMetric(metricWhitelist);
                    builder.setExpansionConfig(new LibratoReporter.MetricExpansionConfig(expandedWhitelist));
                    log.info("Set metric whitelist to {}", expandedWhitelist);
                } else if (!metricBlacklist.isEmpty()) {
                    EnumSet<LibratoReporter.ExpandedMetric> all = EnumSet.allOf(LibratoReporter.ExpandedMetric.class);
                    Set<LibratoReporter.ExpandedMetric> expandedBlacklist = toExpandedMetric(metricBlacklist);
                    Set<LibratoReporter.ExpandedMetric> expandedWhitelist = new HashSet<LibratoReporter.ExpandedMetric>();
                    for (LibratoReporter.ExpandedMetric metric : all) {
                        if (!expandedBlacklist.contains(metric)) {
                            expandedWhitelist.add(metric);
                        }
                    }
                    builder.setExpansionConfig(new LibratoReporter.MetricExpansionConfig(expandedWhitelist));
                    log.info("Set metric whitelist to {}", expandedWhitelist);
                }
            } catch (Exception e) {
                log.error("Could not process whitelist / blacklist", e);
            }
        }
        return builder.build();
    }

    private Set<LibratoReporter.ExpandedMetric> toExpandedMetric(List<String> names) {
        Set<LibratoReporter.ExpandedMetric> result = new HashSet<LibratoReporter.ExpandedMetric>();
        for (String name : names) {
            name = name.toUpperCase();
            result.add(LibratoReporter.ExpandedMetric.valueOf(name));
        }
        return result;
    }
}

