## Overview

This project provides an easy way to send metrics from your Dropwizard project to Librato.

## Usage

There are two steps. First, you must add the `dropwizard-metrics-librato` Maven dependency to your POM file. Second,
the application config YAML needs to be updated to configure the Librato Reporter, which will send your Metrics
data to Librato.

First, add the `metrics-librato-dropwizard` dependency in your POM:

### Dropwizard 9.x

    <dependency>
        <groupId>com.librato.metrics</groupId>
        <artifactId>dropwizard-metrics-librato</artifactId>
        <version>0.9.1.4</version>
    </dependency>

### Dropwizard 8.x

    <dependency>
        <groupId>com.librato.metrics</groupId>
        <artifactId>dropwizard-metrics-librato</artifactId>
        <version>0.8.5.3</version>
    </dependency>

### Dropwizard 7.x

    <dependency>
        <groupId>com.librato.metrics</groupId>
        <artifactId>dropwizard-metrics-librato</artifactId>
        <version>0.7.0.11</version>
    </dependency>

Next, add a `metrics` configuration element to your YAML config file:

    metrics:
      reporters:
        - type: librato
          username: "<Librato Email>"
          token: "<Librato API Token>"
          source: "<Source Identifier (usually hostname)>"
          timeout: [optional (int), number of seconds, defaults to 5]
          prefix: [optional (string), prepended to metric names]
          name: [optional (string), name of the reporter]


That's it.  Once your application starts, your metrics should soon appear in Librato.

## Whitelist / Blacklist

By default, all expanded metrics (percentiles, rates) are submitted for each Timer, Histogram,
and Meter.  If you wish to whitelist only certain metrics, you can do so like this:

    metrics:
      reporters:
        - type: librato
          username: "<Librato Email>"
          token: "<Librato API Token>"
          source: "<Source Identifier (usually hostname)>"
          timeout: [optional (int), number of seconds, defaults to 5]
          prefix: [optional (string), prepended to metric names]
          name: [optional (string), name of the reporter]
          metricWhitelist:
          	- PCT_75
          	- PCT_98
          	- PCT_99
          	- RATE_MEAN
          	- RATE_1_MINUTE
          	- RATE_5_MINUTE

 Similarly, if you wish to blacklist certain expanded metrics, you would do something
 similar to the above example, but replace `metricWhitelist` with `metricBlacklist`.

 The full set of expanded metric names that you can specify are:

 * MEDIAN
 * PCT_75
 * PCT_95
 * PCT_98
 * PCT_99
 * PCT_999
 * COUNT
 * RATE_MEAN
 * RATE_1_MINUTE
 * RATE_5_MINUTE
 * RATE_15_MINUTE

 Note that you cannot supply both `metricWhitelist` and `metricBlacklist`.


## Notes

For each logical application, it is highly recommend you use a `prefix` to distinguish common sets of metrics.

## Contributors

* Initial code: [Chris Huang](https://github.com/tianx2)
