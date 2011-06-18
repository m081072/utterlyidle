package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.annotations.HttpMethod;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.googlecode.utterlyidle.HttpMessageParser.*;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.Responses.response;
import static com.googlecode.utterlyidle.Status.*;
import static com.googlecode.utterlyidle.annotations.HttpMethod.GET;
import static com.googlecode.utterlyidle.io.Url.url;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class HttpMessageParserTest {

    @Test
    public void parseRequest() {
        Request originalRequest = new RequestBuilder(HttpMethod.POST, "/my/path").
                withHeader("header 1", "header 1 value").
                withHeader("header 2", "header 2 value").
                withForm("form 1", "form 1 value").
                withForm("form 2", "form 2 value").
                build();

        Request parsedRequest = HttpMessageParser.parseRequest(originalRequest.toString());

        assertEquals(originalRequest, parsedRequest);
    }

    @Test
    public void parseRequestWithoutBodyWithoutCRLF() {
        assertThat(new RequestBuilder(GET, "/path").build(), is(HttpMessageParser.parseRequest("GET /path HTTP/1.1")));
    }

    @Test
    public void parseRequestWithoutBody() {
        Request requestMessage = new RequestBuilder(HttpMethod.GET, "/test").build();
        assertThat(requestMessage, is(HttpMessageParser.parseRequest(requestMessage.toString())));
    }

    @Test
    public void parseResponseWithoutBody() {
        Response response = Responses.response(Status.OK);
        assertThat(response, is(HttpMessageParser.parseResponse(response.toString())));
    }

    @Test
    public void parseResponseWithoutBodyWithoutCRLF() {
        assertThat(Responses.response(status(426, "Upgrade Required")),
                is(HttpMessageParser.parseResponse("HTTP/1.1 426 Upgrade Required")));
    }

    @Test
    public void parseResponse() {
        Response originalResponse = response(OK).header("header name", "header value").bytes("entity".getBytes());

        Response response = HttpMessageParser.parseResponse(originalResponse.toString());

        assertThat(originalResponse, is(response));
        assertThat(response.bytes(), is("entity".getBytes()));
    }

    @Test
    public void handlesHeadersParamsWithNoValue() {
        String input = get("/").withHeader("header", "").build().toString();

        Request parsed = HttpMessageParser.parseRequest(input);

        assertThat(parsed.headers().getValue("header"), is(equalTo("")));
    }

    @Test
    public void handlesFormParamsWithNoValue() {
        String input = get("/").withForm("form", "").build().toString();

        Request parsed = HttpMessageParser.parseRequest(input);

        assertThat(parsed.form().getValue("form"), is(equalTo("")));
    }

    @Test
    public void canParseRequestWithOnlyRequestLine() {
        RequestBuilder builder = new RequestBuilder(GET, "/my/path");
        Request originalRequest = builder.build();

        Request parsedRequest = HttpMessageParser.parseRequest(originalRequest.toString());

        assertThat(parsedRequest.method(), is(equalTo("GET")));
        assertThat(parsedRequest.url(), is(equalTo(url("/my/path"))));
    }

    @Test
    public void parseResponseStatusLine() {
        assertThat(toStatus("HTTP/1.1 Status: 404 Not Found"), is(NOT_FOUND));
        assertThat(toStatus("HTTP/1.0 Status: 400 Bad Request"), is(BAD_REQUEST));
    }

    @Test
    public void parseRequestLine() {
        assertThat(toMethodAndPath("GET http://localhost:8080/path/ HTTP/1.1"), is(Pair.<String, String>pair("GET", "http://localhost:8080/path/")));
    }

    @Test
    public void parseHeader() {
        assertThat(toFieldNameAndValue("Accept: text/xml"), is(Pair.<String, String>pair("Accept", "text/xml")));
    }

}
