package com.oppalove.consistent;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Node {
    String name;
    String ip;
    String port;

    public Node(String ip, String port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

}
