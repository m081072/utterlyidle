package com.googlecode.utterlyidle;

import com.googlecode.yadic.Container;

public interface Application extends RequestHandler {
    Container applicationScope();

    Application add(Module module);

    Engine engine();
}
