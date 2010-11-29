package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Left;
import com.googlecode.totallylazy.None;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Right;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Some;

import javax.swing.text.html.Option;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.utterlyidle.Param.isParam;
import static com.googlecode.utterlyidle.Param.toParam;

public class ParametersExtractor {
    private Sequence<Pair<Object,Annotation[]>> pairs;

    public ParametersExtractor(Method method, Sequence<Object> arguments) {
        pairs = arguments.zip(sequence(method.getParameterAnnotations()));
    }

    public <T extends Parameters> T extract(T parameters, Class<? extends Annotation> aClass){
        for (Pair<Object, Annotation[]> pair : pairs) {
            final Object value = pair.first();
            final Sequence<Annotation> annotations = sequence(pair.second()).filter(isParam());
            annotations.safeCast(aClass).map(toParam()).foldLeft(parameters, add(value));
        }
        return parameters;
    }

    public static Callable2<Parameters, Param, Parameters> add(final Object value) {
        return new Callable2<Parameters, Param, Parameters>() {
            public Parameters call(Parameters parameters, Param param) throws Exception {
                return parameters.add(param.value(), convertToString(value));
            }
        };
    }

    private static String convertToString(Object value) {
        if(value instanceof Left){
            return convertToString(((Left) value).left());
        }
        if(value instanceof Right){
            return convertToString(((Right) value).right());
        }
        if(value instanceof Some){
            return convertToString(((Some) value).get());
        }
        if(value instanceof None){
            return "";
        }
        return value.toString();
    }

}
