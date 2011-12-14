package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Pair;
import com.googlecode.utterlyidle.handlers.Auditor;
import com.googlecode.utterlyidle.handlers.Auditors;
import com.googlecode.utterlyidle.modules.AuditModule;
import org.junit.Test;

import java.util.Date;

import static com.googlecode.utterlyidle.RequestBuilder.get;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AuditTest {
    @Test
    public void recordsRequestAndResponse() throws Exception {
        RestApplication testApplication = new RestApplication();
        testApplication.add(auditModule());

        Request request = get("").build();
        Response response = testApplication.handle(request);

        assertThat(TestAuditor.receivedRequest, is(request));
        assertThat(TestAuditor.receivedResponse, is(response));
    }

    private AuditModule auditModule() {
        return new AuditModule() {
            public Auditors addAuditors(Auditors auditors) {
                return auditors.add(TestAuditor.class);
            }
        };
    }

    public static class TestAuditor implements Auditor {
        public static Request receivedRequest;
        public static Response receivedResponse;

        public void audit(Pair<Request, Date> request, Pair<Response, Date> response) {
            receivedRequest = request.first();
            receivedResponse = response.first();
        }

    }
}
