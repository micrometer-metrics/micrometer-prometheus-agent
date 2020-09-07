package io.micrometer.agent.prometheus;

import io.javalin.Javalin;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import java.lang.instrument.Instrumentation;

public class PrometheusAgent {
    private static final PrometheusMeterRegistry meterRegistry =
            new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    static {
        new ProcessorMetrics().bindTo(meterRegistry);
        new JvmMemoryMetrics().bindTo(meterRegistry);
        new JvmGcMetrics().bindTo(meterRegistry);
        new JvmHeapPressureMetrics().bindTo(meterRegistry);
        new FileDescriptorMetrics().bindTo(meterRegistry);

        // TODO how to get the Tomcat manager in the agent?
        // new TomcatMetrics(manager, Tags.empty()).bindTo(meterRegistry);
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        runPrometheusScrapeEndpoint();
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        runPrometheusScrapeEndpoint();
    }

    private static void runPrometheusScrapeEndpoint() {
        Javalin app = Javalin.create().start(7001);

        app.get("/prometheus", ctx -> ctx
                .header("Content-Type", TextFormat.CONTENT_TYPE_004)
                .result(meterRegistry.scrape()));
    }
}
