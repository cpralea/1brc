package dev.onebrc;


public class Config {

    public static final int DEFAULT_TRIE_CHUNK_SIZE = 4096;

    public static final int DEFAULT_NUM_CORES = Runtime.getRuntime().availableProcessors();
    public static final int DEFAULT_NUM_SEGMENTS = 12 * DEFAULT_NUM_CORES;

}
