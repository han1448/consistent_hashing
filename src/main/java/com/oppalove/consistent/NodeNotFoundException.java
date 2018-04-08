package com.oppalove.consistent;

public class NodeNotFoundException extends Exception {

    public NodeNotFoundException() {
        super();
    }

    public NodeNotFoundException(String message) {
        super(message);
    }

    public NodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NodeNotFoundException nodeNotFoundException() {
        return new NodeNotFoundException("Can not find node.");
    }
}
