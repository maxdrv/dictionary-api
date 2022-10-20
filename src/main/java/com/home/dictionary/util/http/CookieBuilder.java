package com.home.dictionary.util.http;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CookieBuilder {

    private final List<Node> nodes = new ArrayList<>();

    public CookieBuilder addNode(String name) {
        this.nodes.add(new NoValueNode(name));
        return this;
    }

    public CookieBuilder addNode(String name, String value) {
        this.nodes.add(new ValueNode(name, value));
        return this;
    }

    public HttpHeader toHttpHeader() {
        return new HttpHeader(
                "Set-Cookie",
                nodes.stream().map(Node::stringify).collect(Collectors.joining("; "))
        );
    }

    private interface Node {
        String stringify();
    }

    @RequiredArgsConstructor
    private static class NoValueNode implements Node {

        private final String name;

        @Override
        public String stringify() {
            return name;
        }
    }

    @RequiredArgsConstructor
    private static class ValueNode implements Node {

        private final String name;
        private final String value;

        @Override
        public String stringify() {
            return name + "=" + value;
        }
    }

}
