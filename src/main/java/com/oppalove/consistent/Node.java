package com.oppalove.consistent;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Node {
    String ip;
    String port;

    public Node(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }

}
