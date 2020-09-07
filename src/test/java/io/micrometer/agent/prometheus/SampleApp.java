package io.micrometer.agent.prometheus;

import io.javalin.Javalin;

public class SampleApp {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(8080);

        app.get("/hello", ctx -> ctx.result("Hello World!"));
    }
}
