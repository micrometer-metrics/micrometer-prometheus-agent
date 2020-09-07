## Prometheus Agent

Build the agent with `./gradlew build`.

Run an application with `java -javaagent:micrometer-prometheus-agent.jar -jar application.jar`.

This exposes a Prometheus scrape endpoint on `http://YOURHOST:7001/prometheus`.
