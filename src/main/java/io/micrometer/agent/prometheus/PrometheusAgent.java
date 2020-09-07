package io.micrometer.agent.prometheus;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.net.InetSocketAddress;

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
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(7001), 0);
            server.createContext("/prometheus", httpExchange -> {
                String response = meterRegistry.scrape();
                httpExchange.getResponseHeaders().set("Content-Type", TextFormat.CONTENT_TYPE_004);
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });

            new Thread(server::start).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
