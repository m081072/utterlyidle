package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Uri;

import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Uri.uri;

public class UriActivator implements Callable<Uri> {
    private final String value;

    public UriActivator(final String value) {
        this.value = value;
    }

    @Override
    public Uri call() throws Exception {
        return uri(value);
    }
}
