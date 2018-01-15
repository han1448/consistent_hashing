package com.oppalove.consistent;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

@Slf4j
public class ConsistentHash {
    private static final int MAX_NODE = 10000;
    private static final int DEFAULT_REPLICA = 1;
    private int virtualNodeCount;
    private int currentNodeCount;
    private NavigableMap<Integer, Node> hashRing = new TreeMap<>();
    private Random random = new Random();

    public ConsistentHash() {
        this.virtualNodeCount = DEFAULT_REPLICA;
    }

    public ConsistentHash(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
    }

    public void add(Node node) {
        currentNodeCount++;

        if ((currentNodeCount * virtualNodeCount) >= MAX_NODE * 0.9) {
            throw new IllegalStateException("Too much nodes.");
        }

        for (int r = 0; r < virtualNodeCount; r++) {
            int nodeIdx = random.nextInt(MAX_NODE);
            while (hashRing.containsKey(nodeIdx)) {
                nodeIdx = random.nextInt(MAX_NODE);
            }
            log.debug("adding a node({}) -> {}", node.ip, nodeIdx);
            hashRing.put(nodeIdx, node);
        }
    }

    public void remove(Node node) {

        final Integer[] keyNode = new Integer[virtualNodeCount];
        int idx=0;
        for (Map.Entry<Integer, Node> entry : hashRing.entrySet()) {
            if (entry.getValue().equals(node)) {
                log.debug("removing a node({}) -> {}", node.ip, entry.getKey());
                keyNode[idx++] = entry.getKey();
            }
        }

        for (int i=0; i < keyNode.length; i++) {
            hashRing.remove(keyNode[i]);
        }
    }

    public Node getNode(String key) {
        int hashKey = key.hashCode() % MAX_NODE;
        log.debug("getNode() -> {}", hashKey);
        if (hashRing.ceilingEntry(hashKey) == null)
            return hashRing.firstEntry().getValue();
        return hashRing.ceilingEntry(hashKey).getValue();
    }
}
