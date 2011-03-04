package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.*;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.utterlyidle.HeaderParameters.headerParameters;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

public class ExceptionHandler implements HttpHandler {
    private final HttpHandler httpHandler;
    private final ResponseHandlersFinder handlers;

    public ExceptionHandler(HttpHandler httpHandler, ResponseHandlersFinder handlers) {
        this.httpHandler = httpHandler;
        this.handlers = handlers;
    }

    public Response handle(Request request) throws Exception {
        try {
            return httpHandler.handle(request);
        } catch (InvocationTargetException e) {
            return findAndHandle(request, e);
        } catch (Exception e) {
            return findAndHandle(request, e);
        }
    }

    private Response findAndHandle(Request request, Exception exception) throws Exception {
        Response response = response(
                INTERNAL_SERVER_ERROR,
                headerParameters(pair(CONTENT_TYPE, TEXT_PLAIN)),
                exception);
        return handlers.findAndHandle(request, response);
    }
}
