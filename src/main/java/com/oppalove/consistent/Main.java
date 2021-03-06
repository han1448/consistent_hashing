package com.oppalove.consistent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) throws NodeNotFoundException {
        ConsistentHash consistentHash = new ConsistentHash(10);
        consistentHash.add(new Node("127.0.0.1", "8080", "node1"));
        consistentHash.add(new Node("127.0.0.2", "8080", "node2"));
        consistentHash.add(new Node("127.0.0.3", "8080", "node3"));


        Node node = consistentHash.getNode("Hi");
        log.debug("Node is {}", node.ip);

        Node node1 = consistentHash.getNode("Hello");
        log.debug("Node is {}", node1.ip);

        Node node2 = consistentHash.getNode("GoodBye");
        log.debug("Node is {}", node2.ip);

        Node node4 = new Node("127.0.0.4", "8080", "node4");
        consistentHash.add(node4);

        node = consistentHash.getNode("Hi");
        log.debug("Node is {}", node.ip);

        node1 = consistentHash.getNode("Hello");
        log.debug("Node is {}", node1.ip);

        node2 = consistentHash.getNode("GoodBye");
        log.debug("Node is {}", node2.ip);

        consistentHash.remove(node4);

        node = consistentHash.getNode("Hi");
        log.debug("Node is {}", node.ip);

        node1 = consistentHash.getNode("Hello");
        log.debug("Node is {}", node1.ip);

        node2 = consistentHash.getNode("GoodBye");
        log.debug("Node is {}", node2.ip);
    }
}
