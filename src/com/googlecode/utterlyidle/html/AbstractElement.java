package com.googlecode.utterlyidle.html;

import com.googlecode.totallylazy.Exceptions;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Function2;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Xml;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static com.googlecode.totallylazy.Xml.selectContents;
import static com.googlecode.utterlyidle.html.Html.html;
import static java.lang.String.format;

public class AbstractElement {
    protected final Node node;

    public AbstractElement(Node node) {
        this.node = node;
    }

    public boolean hasAttribute(String attributeName) {
        return Xml.matches(node, format("@%s", attributeName));
    }

    public String attribute(String attributeName) {
        return Xml.selectContents(node, format("@%s", attributeName));
    }

    public void setAttribute(String name, String value) {
        if (node instanceof Element)
            ((Element) node).setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        if (node instanceof Element)
            ((Element) node).removeAttribute(name);
    }

    public String contents(String expression) {
        return selectContents(node, expression);
    }

    public Number count(String xpath) {
        return Xml.selectNumber(node, String.format("count(%s)", xpath));
    }

    public String selectContent(String xpath) {
        return selectContents(node, xpath);
    }

    protected Element expectElement(String xpath) {
        return Xml.expectElement(node, xpath);
    }

    protected Sequence<Element> selectElements(String xpath) {
        return Xml.selectElements(node, xpath);
    }

    public boolean contains(String xpath) {
        return Xml.matches(node, xpath);
    }

    public Html innerHtml(String xpath) throws Exception {
        return html(selectContents(node, xpath));
    }

    @Override
    public String toString() {
        try {
            return Xml.format(node);
        } catch (Exception e) {
            return "Exception in toString():\n" + Exceptions.asString(e);
        }
    }

    public String toString(Pair<String, Object>... attributes) {
        try {
            return Xml.format(node, attributes);
        } catch (Exception e) {
            return "Exception in toString():\n" + Exceptions.asString(e);
        }
    }

    public static class functions {
        public static Function1<AbstractElement, String> attribute(final String attributeName) {
            return attribute().flip().apply(attributeName);
        }

        public static Function2<AbstractElement, String, String> attribute() {
            return new Function2<AbstractElement, String, String>() {
                @Override
                public String call(final AbstractElement element, final String attributeName) throws Exception {
                    return element.attribute(attributeName);
                }
            };
        }

        public static Function1<AbstractElement, String> contents(final String xpath) {
            return contents().flip().apply(xpath);
        }

        public static Function2<AbstractElement, String, String> contents() {
            return new Function2<AbstractElement, String, String>() {
                @Override
                public String call(final AbstractElement element, final String xpath) throws Exception {
                    return element.contents(xpath);
                }
            };
        }

        public static Function1<AbstractElement, String> selectContent(final String xpath) {
            return selectContent().flip().apply(xpath);
        }

        public static Function2<AbstractElement, String, String> selectContent() {
            return new Function2<AbstractElement, String, String>() {
                @Override
                public String call(final AbstractElement element, final String xpath) throws Exception {
                    return element.selectContent(xpath);
                }
            };
        }


        public static Function1<AbstractElement, Html> innerHtml(final String xpath) {
            return innerHtml().flip().apply(xpath);
        }

        public static Function2<AbstractElement, String, Html> innerHtml() {
            return new Function2<AbstractElement, String, Html>() {
                @Override
                public Html call(final AbstractElement element, final String xpath) throws Exception {
                    return element.innerHtml(xpath);
                }
            };
        }


        public static Function1<AbstractElement, Number> count(final String xpath) {
            return count().flip().apply(xpath);
        }

        public static Function2<AbstractElement, String, Number> count() {
            return new Function2<AbstractElement, String, Number>() {
                @Override
                public Number call(final AbstractElement element, final String xpath) throws Exception {
                    return element.count(xpath);
                }
            };
        }
    }

    public static class predicates {

        public static Predicate<AbstractElement> hasAttribute(final String attributeName) {
            return new Predicate<AbstractElement>() {
                @Override
                public boolean matches(final AbstractElement element) {
                    return element.hasAttribute(attributeName);
                }
            };
        }

        public static Predicate<AbstractElement> contains(final String xpath) {
            return new Predicate<AbstractElement>() {
                @Override
                public boolean matches(final AbstractElement element) {
                    return element.contains(xpath);
                }
            };
        }
    }
}
