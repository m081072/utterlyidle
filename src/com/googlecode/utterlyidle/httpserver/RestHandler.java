package com.googlecode.utterlyidle.httpserver;

import com.googlecode.utterlyidle.Application;
import com.googlecode.utterlyidle.BasePath;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.Status;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static com.googlecode.totallylazy.callables.TimeCallable.calculateMilliseconds;
import static java.lang.System.nanoTime;

public class RestHandler implements HttpHandler {
    private final Application application;
    private final BasePath basePath;

    public RestHandler(Application application, BasePath basePath) {
        this.application = application;
        this.basePath = basePath;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        HttpExchangeRequest request = new HttpExchangeRequest(httpExchange, basePath);
        HttpExchangeResponse response = new HttpExchangeResponse(httpExchange);
        try {
            long start = nanoTime();
            application.handle(request, response);
            System.out.println(String.format("%s %s -> %s in %s msecs", request.method(), request.url(), response.code(), calculateMilliseconds(start, nanoTime())));
        } catch (Exception e) {
            outputException(request, response, e);
        }
    }

    private void outputException(Request request, Response response, Exception e) throws IOException {
        System.err.println(String.format("%s %s -> %s", request.method(), request.url(), e));
        e.printStackTrace(System.err);
        response.code(Status.INTERNAL_SERVER_ERROR);
        try {
            e.printStackTrace(response.writer());
        } finally {
            response.close();
        }
    }
}
