package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.matchers.NumberMatcher;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static com.googlecode.utterlyidle.html.Html.html;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class HtmlTest {
    @Test
    public void printsContentIfItCantParseIt() throws Exception {
        try {
            new Html("foo");
            fail("should have thrown");
        } catch (IllegalArgumentException e){
            assertThat(e.getMessage(), containsString("foo"));
        }
    }

    @Test
    public void toStringReturnsOriginalHtml() throws Exception{
        Html html = html("   <html/>   ");
        assertThat(html.toString(), is("   <html/>   "));
    }

    @Test
    public void returnsForms() throws Exception{
        Html html = html("<html><form id=\"form1\"><input name=\"one\"/></form><form id=\"form2\"><input name=\"two\"/></form></html>");
        Sequence<Form> forms = html.forms();
        assertThat(forms.size(), is(equalTo(2)));
        assertThat(forms.first().toString(), is(equalTo("<form id=\"form1\"><input name=\"one\"/></form>")));
        assertThat(forms.second().toString(), is(equalTo("<form id=\"form2\"><input name=\"two\"/></form>")));
    }

    @Test
    public void supportsCount() throws Exception{
        Html html = html("<html/>");
        assertThat(html.count("//html"), NumberMatcher.is(1));
        assertThat(html.count("//bob"), NumberMatcher.is(0));
    }

    @Test
    public void supportsContains() throws Exception{
        Html html = html("<html/>");
        assertThat(html.contains("//html"), is(true));
        assertThat(html.contains("//bob"), is(false));
    }

    @Test
    public void supportsInnerHtml() throws Exception {
        Html html = html("<html><head><title>Oh Hello</title></head><body></body></html>");
        assertThat(html.innerHtml("//head"), is(html("<title>Oh Hello</title>")));
    }

    @Test
    public void supportsSelectValues() throws Exception {
        Html html = html("<html><head><title>Title1</title><title>Title2</title></head><body></body></html>");
        assertThat(html.selectValues("//title"), hasExactly("Title1", "Title2"));

    }


}
