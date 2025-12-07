package dev.onebrc.parsers.advanced;


import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static dev.onebrc.Config.DEFAULT_TRIE_CHUNK_SIZE;


public class ByteTrie {

    private final short chunkSize;

    private short[] nodeInboundTransition = new short[0];
    private short[] nodeOutboundTransitionsList = new short[0];
    private short nodeCapacity = 0;
    private short nodeCount = 1;
    private short currentNode = 0;

    private short[] transitionSourceNode = new short[0];
    private short[] transitionDestinationNode = new short[0];
    private byte[] transitionLabel = new byte[0];
    private short[] transitionNextInList = new short[0];
    private short transitionCapacity = 0;
    private short transitionCount = 0;


    public ByteTrie(short chunkSize) {
        if (chunkSize <= 0) {
            chunkSize = DEFAULT_TRIE_CHUNK_SIZE;
        }
        this.chunkSize = chunkSize;
        growNodesBy(chunkSize);
        growTransitionsBy(chunkSize);
    }


    public void restart() {
        currentNode = 0;
    }


    public short process(byte b) {
        short currentTransition = nodeOutboundTransitionsList[currentNode];
        while (currentTransition != -1) {
            if (transitionLabel[currentTransition] == b) {
                currentNode = transitionDestinationNode[currentTransition];
                return currentNode;
            }
            currentTransition = transitionNextInList[currentTransition];
        }

        if (nodeCount >= nodeCapacity) {
            growNodesBy(chunkSize);
        }
        short newNode = nodeCount++;
        if (transitionCount >= transitionCapacity) {
            growTransitionsBy(chunkSize);
        }
        short newTransition = transitionCount++;

        nodeInboundTransition[newNode] = newTransition;
        nodeOutboundTransitionsList[newNode] = -1;
        transitionSourceNode[newTransition] = currentNode;
        transitionDestinationNode[newTransition] = newNode;
        transitionLabel[newTransition] = b;
        transitionNextInList[newTransition] = nodeOutboundTransitionsList[currentNode];
        nodeOutboundTransitionsList[currentNode] = newTransition;

        currentNode = newNode;
        return currentNode;
    }


    public short getCurrentNode() {
        return currentNode;
    }


    public byte[] getSequence(short node) {
        Stack<Byte> stack = new Stack<>();
        while (node != 0) {
            short inboundTransition = nodeInboundTransition[node];
            stack.push(transitionLabel[inboundTransition]);
            node = transitionSourceNode[inboundTransition];
        }
        return toPrimitive(stack.reversed());
    }


    private void growNodesBy(short size) {
        nodeCapacity += size;

        nodeInboundTransition = Arrays.copyOf(nodeInboundTransition, nodeCapacity);
        nodeOutboundTransitionsList = Arrays.copyOf(nodeOutboundTransitionsList, nodeCapacity);

        Arrays.fill(nodeInboundTransition, nodeCapacity - size, nodeCapacity, (short) -1);
        Arrays.fill(nodeOutboundTransitionsList, nodeCapacity - size, nodeCapacity, (short) -1);
    }


    private void growTransitionsBy(short size) {
        transitionCapacity += size;

        transitionSourceNode = Arrays.copyOf(transitionSourceNode, transitionCapacity);
        transitionDestinationNode = Arrays.copyOf(transitionDestinationNode, transitionCapacity);
        transitionLabel = Arrays.copyOf(transitionLabel, transitionCapacity);
        transitionNextInList = Arrays.copyOf(transitionNextInList, transitionCapacity);

        Arrays.fill(transitionSourceNode, transitionCapacity - size, transitionCapacity, (short) -1);
        Arrays.fill(transitionDestinationNode, transitionCapacity - size, transitionCapacity, (short) -1);
        Arrays.fill(transitionLabel, transitionCapacity - size, transitionCapacity, (byte) -1);
        Arrays.fill(transitionNextInList, transitionCapacity - size, transitionCapacity, (short) -1);
    }


    static private byte[] toPrimitive(List<Byte> l) {
        byte[] bytes = new byte[l.size()];
        int i = 0;
        for (Byte b : l) {
            bytes[i++] = b;
        }
        return bytes;
    }

}
