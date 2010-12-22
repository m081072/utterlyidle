package com.googlecode.utterlyidle.handlers;

import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseHandler;
import com.googlecode.utterlyidle.Status;

import java.io.IOException;

public class NullHandler implements ResponseHandler {
    public void handle(Response response) throws IOException {
        response.status(Status.NO_CONTENT);
    }
}
