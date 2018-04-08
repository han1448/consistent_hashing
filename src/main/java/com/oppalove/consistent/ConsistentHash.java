package com.oppalove.consistent;

import lombok.extern.slf4j.Slf4j;

import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

@Slf4j
public class ConsistentHash {
    private static final int MAX_NODE = Integer.MAX_VALUE;
    private static final int DEFAULT_REPLICA = 1;
    private final long tRange = (long) Integer.MAX_VALUE + Math.abs((long) Integer.MIN_VALUE);
    private int virtualNodeCount;
    private NavigableMap<Long, Node> hashRing = new TreeMap<>();

    /**
     * Default constructor.
     */
    public ConsistentHash() {
        this.virtualNodeCount = DEFAULT_REPLICA;
    }

    /**
     * Constructor that can be set virtualNodeCount.
     * If you add new node, it will be created randomly any index as the number of virtualNodeCount.
     *
     * @param virtualNodeCount the number of virtual count per each node.
     */
    public ConsistentHash(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
    }

    /**
     * Add new node.
     *
     * @param node T node object.
     */
    public synchronized void add(Node node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            addNode(node, i);
        }
    }

    private void addNode(Node node, int i) {
        long idx = getIdx(node, i);
        log.debug("Node added : {}({})", node.getIp(), idx);
        hashRing.put(idx, node);
    }

    private long getIdx(Node node, int i) {
        long sha1Hash = Math.abs(
                SHA1.generate(
                        SHA1.generate(String.valueOf((node.getName() + i).hashCode())) +
                                SHA1.generate(String.valueOf(node.getIp().hashCode())) +
                                SHA1.generate(String.valueOf(node.getPort().hashCode())))
                        .hashCode());
        return sha1Hash * MAX_NODE / tRange;
    }

    /**
     * Remove node from hash.
     *
     * @param node T node object.
     */
    public synchronized void remove(Node node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            long idx = getIdx(node, i);
            Node remNode = hashRing.remove(idx);
            log.debug("{}({}) node removed.", remNode.getName(), idx);
        }
    }

    /**
     * Get node from the given key.
     *
     * @param key key data.
     * @return Node node.
     */
    public Node getNode(String key) throws NodeNotFoundException {
        long hashKey = Math.abs((long) (key.hashCode() % MAX_NODE));
        log.debug("getNode() -> {}", hashKey);
        if (hashRing.ceilingEntry(hashKey) == null)
            return Optional.ofNullable(hashRing.firstEntry())
                    .orElseThrow(NodeNotFoundException::nodeNotFoundException)
                    .getValue();
        return Optional.ofNullable(hashRing.ceilingEntry(hashKey))
                .orElseThrow(NodeNotFoundException::nodeNotFoundException)
                .getValue();
    }
}
