package com.googlecode.utterlyidle.handlers;

import com.googlecode.totallylazy.*;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.Request;
import com.googlecode.utterlyidle.Response;
import com.googlecode.utterlyidle.ResponseBuilder;
import com.googlecode.utterlyidle.Status;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.List;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Closeables.using;
import static com.googlecode.totallylazy.Maps.pairs;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;
import static com.googlecode.utterlyidle.HttpHeaders.CONTENT_LENGTH;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.NOT_FOUND;
import static com.googlecode.utterlyidle.Status.OK;
import static com.googlecode.utterlyidle.Status.status;

public class ClientHttpHandler implements HttpClient {
    private final int milliseconds;

    public ClientHttpHandler() {
        this(0);
    }

    public ClientHttpHandler(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Response handle(final Request request) throws Exception {
        URL url = new URL(request.uri().toString());
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(milliseconds);
        connection.setUseCaches(true);
        connection.setReadTimeout(milliseconds);
        if (connection instanceof HttpURLConnection) {
            return handle(request, (HttpURLConnection) connection);
        }
        return handle(request, connection);
    }

    private Response handle(Request request, URLConnection connection) throws IOException {
        try {
            sendRequest(request, connection);
            return createResponse(connection, OK, using(connection.getInputStream(), bytes()));
        } catch (FileNotFoundException e) {
            return createResponse(connection, NOT_FOUND, new byte[0]);
        }
    }

    private Response handle(Request request, HttpURLConnection connection) throws IOException {
        try {
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(request.method());
            sendRequest(request, connection);
            Status status = status(connection); // request is actually sent now
            byte[] bytes = using(inputStream(connection), bytes());
            return createResponse(connection, status, bytes);
        } catch (ConnectException ex) {
            return response(Status.CONNECTION_REFUSED);
        } catch (SocketTimeoutException ex) {
            return response(Status.CLIENT_TIMEOUT);
        }
    }

    public static InputStream inputStream(HttpURLConnection urlConnection) throws IOException {
        if (urlConnection.getResponseCode() >= 400) {
            return urlConnection.getErrorStream();
        } else {
            return urlConnection.getInputStream();
        }
    }

    private Response createResponse(URLConnection connection, Status status, byte[] bytes) {
        final ResponseBuilder builder = pairs(connection.getHeaderFields()).
                filter(where(first(String.class), is(not(equalIgnoringCase(HttpHeaders.TRANSFER_ENCODING))))).
                fold(ResponseBuilder.response(status).entity(bytes),
                        responseHeaders());
        if(!builder.build().headers().contains(CONTENT_LENGTH)){
            return builder.header(CONTENT_LENGTH, bytes.length).build();
        }
        return builder.build();
    }

    private void sendRequest(Request request, URLConnection connection) throws IOException {
        sequence(request.headers()).fold(connection, requestHeaders());
        if (Integer.valueOf(request.headers().getValue(CONTENT_LENGTH)) > 0) {
            connection.setDoOutput(true);
            using(connection.getOutputStream(), request.entity().transferFrom());
        }
    }

    private static Callable2<? super URLConnection, ? super Pair<String, String>, URLConnection> requestHeaders() {
        return new Callable2<URLConnection, Pair<String, String>, URLConnection>() {
            public URLConnection call(URLConnection connection, Pair<String, String> header) throws Exception {
                connection.setRequestProperty(header.first(), header.second());
                return connection;
            }
        };
    }

    private static Callable2<ResponseBuilder, Pair<String, List<String>>, ResponseBuilder> responseHeaders() {
        return new Callable2<ResponseBuilder, Pair<String, List<String>>, ResponseBuilder>() {
            public ResponseBuilder call(ResponseBuilder response, final Pair<String, List<String>> entry) throws Exception {
                return sequence(entry.second()).fold(response, responseHeader(entry.first()));
            }
        };
    }

    private static Callable2<ResponseBuilder, String, ResponseBuilder> responseHeader(final String key) {
        return new Callable2<ResponseBuilder, String, ResponseBuilder>() {
            public ResponseBuilder call(ResponseBuilder response, String value) throws Exception {
                if (key != null) {
                    return response.header(key, value);
                }
                return response;
            }
        };
    }

    public static Callable1<InputStream, byte[]> bytes() {
        return new Callable1<InputStream, byte[]>() {
            public byte[] call(InputStream stream) throws Exception {
                return Bytes.bytes(stream);
            }
        };
    }
}
