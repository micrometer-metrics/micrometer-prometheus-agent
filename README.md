# micrometer-prometheus-agent is no longer actively maintained by VMware, Inc.

## Prometheus Agent

Build the agent with `./gradlew build`.

Run an application with `java -javaagent:micrometer-prometheus-agent.jar -jar application.jar`.

This exposes a Prometheus scrape endpoint on `http://YOURHOST:7001/prometheus`.

## What is this?

This project is a "what-if" thought experiment on adding Micrometer instrumentation as an agent that can be statically or dynamically loaded into an application without adding a dependency.
