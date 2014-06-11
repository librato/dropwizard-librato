## Overview

This project provides an easy way to send metrics from your Dropwizard 0.7 project to Librato. 

## Usage

There are two steps. First, you must add the `dropwizard-metrics-librato` Maven dependency to your POM file. Second,
the application config YAML needs to be updated to configure the Librato Reporter, which will send your Metrics
data to Librato.

First, add the `metrics-librato-dropwizard` dependency in your POM:

    <dependency>
        <groupId>com.librato.metrics</groupId>
        <artifactId>dropwizard-metrics-librato</artifactId>
        <version>0.7.0.1</version>
    </dependency>
    
Next, add a `metrics` configuration element to your YAML config file:

    metrics:
      reporters:
        - type: librato
          username: "<Librato Email>"
          token: "<Librato API Token>""
          source: "<Source Identifier (usually hostname)>"
          timeout: [optional (int), number of seconds, defaults to 5]
          prefix: [optional (string), prepended to metric names]
          name: [optional (string), name of the reporter]
          
          
That's it.  Once your application starts, your metrics should soon appear in Librato.

## Notes

For each logical application, it is highly recommend you use a `prefix` to distinguish common sets of metrics.

## Contributors

* Initial code: [Chris Huang](https://github.com/tianx2)
