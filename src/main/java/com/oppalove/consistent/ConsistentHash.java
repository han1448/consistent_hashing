package com.oppalove.consistent;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

@Slf4j
public class ConsistentHash<T> {
    private static final int MAX_NODE = 10000;
    private static final int DEFAULT_REPLICA = 1;
    private int virtualNodeCount;
    private int maxNodeCount;
    private int currentNodeCount;
    private NavigableMap<Integer, T> hashRing = new TreeMap<>();
    private Random random = new Random();

    /**
     * Default constructor.
     */
    public ConsistentHash() {
        this.virtualNodeCount = DEFAULT_REPLICA;
        this.maxNodeCount = MAX_NODE;
    }

    /**
     * Constructor that can be set virtualNodeCount.
     * If you add new node, it will be created randomly any index as the number of virtualNodeCount.
     *
     * @param virtualNodeCount the number of virtual count per each node.
     */
    public ConsistentHash(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
        this.maxNodeCount = MAX_NODE;
    }

    /**
     * Constructor that can be set virtualNodeCount and maxNode.
     *
     * @param virtualNodeCount the number of virtual node per each node.
     * @param maxNode          the number of maximum node including virtual node.
     */
    public ConsistentHash(int virtualNodeCount, int maxNode) {
        this.virtualNodeCount = virtualNodeCount;
        this.maxNodeCount = maxNode;
    }

    /**
     * Add new node.
     *
     * @param node T node object.
     */
    public void add(T node) {
        currentNodeCount++;

        if ((currentNodeCount * virtualNodeCount) >= maxNodeCount * 0.9) {
            throw new IllegalStateException("Too much nodes.");
        }

        for (int r = 0; r < virtualNodeCount; r++) {
            int nodeIdx = random.nextInt(maxNodeCount);
            while (hashRing.containsKey(nodeIdx)) {
                nodeIdx = random.nextInt(maxNodeCount);
            }
            log.debug("adding a node({}) -> {}", node.toString(), nodeIdx);
            hashRing.put(nodeIdx, node);
        }
    }

    /**
     * Remove node from hash.
     *
     * @param node T node object.
     */
    public void remove(T node) {

        final Integer[] keyNode = new Integer[virtualNodeCount];
        int idx = 0;
        for (Map.Entry<Integer, T> entry : hashRing.entrySet()) {
            if (entry.getValue().equals(node)) {
                log.debug("removing a node({}) -> {}", node.toString(), entry.getKey());
                keyNode[idx++] = entry.getKey();
            }
        }

        for (int i = 0; i < keyNode.length; i++) {
            hashRing.remove(keyNode[i]);
        }
    }

    /**
     * Get node from the given key.
     *
     * @param key key data.
     * @return T node.
     */
    public T getNode(String key) {
        int hashKey = key.hashCode() % maxNodeCount;
        log.debug("getNode() -> {}", hashKey);
        if (hashRing.ceilingEntry(hashKey) == null)
            return hashRing.firstEntry().getValue();
        return hashRing.ceilingEntry(hashKey).getValue();
    }
}
