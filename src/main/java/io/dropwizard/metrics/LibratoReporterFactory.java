package io.dropwizard.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.librato.metrics.reporter.ExpandedMetric;
import com.librato.metrics.reporter.LibratoReporter;
import com.librato.metrics.reporter.MetricExpansionConfig;
import com.librato.metrics.reporter.ReporterBuilder;
import io.dropwizard.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        ReporterBuilder builder = LibratoReporter.builder(registry, username, token)
                .setRateUnit(getRateUnit())
                .setDurationUnit(getDurationUnit())
                .setFilter(getFilter());
        if (source != null) {
            builder.setSource(source);
        }
        if (sourceRegex != null) {
            builder.setSourceRegex(sourceRegex);
        }
        if (libratoUrl != null) {
            builder.setUrl(libratoUrl);
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
                    Set<ExpandedMetric> expandedWhitelist = toExpandedMetric(metricWhitelist);
                    builder.setExpansionConfig(new MetricExpansionConfig(expandedWhitelist));
                    log.info("Set metric whitelist to {}", expandedWhitelist);
                } else if (!metricBlacklist.isEmpty()) {
                    EnumSet<ExpandedMetric> all = EnumSet.allOf(ExpandedMetric.class);
                    Set<ExpandedMetric> expandedBlacklist = toExpandedMetric(metricBlacklist);
                    Set<ExpandedMetric> expandedWhitelist = new HashSet<ExpandedMetric>();
                    for (ExpandedMetric metric : all) {
                        if (!expandedBlacklist.contains(metric)) {
                            expandedWhitelist.add(metric);
                        }
                    }
                    builder.setExpansionConfig(new MetricExpansionConfig(expandedWhitelist));
                    log.info("Set metric whitelist to {}", expandedWhitelist);
                }
            } catch (Exception e) {
                log.error("Could not process whitelist / blacklist", e);
            }
        }
        return builder.build();
    }

    private Set<ExpandedMetric> toExpandedMetric(List<String> names) {
        Set<ExpandedMetric> result = new HashSet<ExpandedMetric>();
        for (String name : names) {
            name = name.toUpperCase();
            result.add(ExpandedMetric.valueOf(name));
        }
        return result;
    }
}

