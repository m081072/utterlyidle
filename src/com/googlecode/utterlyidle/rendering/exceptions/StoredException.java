package com.googlecode.utterlyidle.rendering.exceptions;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Exceptions;
import com.googlecode.utterlyidle.Request;

import java.util.Date;

public class StoredException {
    private final Date date;
    private final Request request;
    private final Exception exception;

    public StoredException(Date date, Request request, Exception exception) {
        this.date = date;
        this.request = forceInMemory(request);
        this.exception = exception;
    }

    private static Request forceInMemory(final Request request) {
        request.toString();
        return request;
    }

    public Date getDate() {
        return date;
    }

    public Request getRequest() {
        return request;
    }

    public Exception getException() {
        return exception;
    }

    public String getExceptionAsString() {
        return  Exceptions.asString(exception);
    }

    public static Callable1<? super StoredException, Exception> exception() {
        return new Callable1<StoredException, Exception>() {
            @Override
            public Exception call(StoredException e) throws Exception {
                return e.getException();
            }
        };
    }
}
