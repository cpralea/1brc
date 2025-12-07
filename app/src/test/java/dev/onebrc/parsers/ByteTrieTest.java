package dev.onebrc.parsers;


import dev.onebrc.TestUtils;
import dev.onebrc.parsers.advanced.ByteTrie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ByteTrieTest {

    static final short CHUNK_SIZE = 128;

    ByteTrie trie;


    @BeforeEach
    void setUp() {
        trie = new ByteTrie(CHUNK_SIZE);
    }


    @Test
    void insertByteOnce() {
        assertEquals(1, trie.process((byte) 'a'));
    }


    @Test
    void restartAfterOneInsert() {
        insertByteOnce();
        trie.restart();
        insertByteOnce();
    }


    @Test
    void insertByteTwice() {
        assertEquals(1, trie.process((byte) 'a'));
        assertEquals(2, trie.process((byte) 'b'));
    }


    @Test
    void restartAfterTwoInserts() {
        insertByteTwice();
        trie.restart();
        insertByteTwice();
    }


    @Test
    void insertOneWord() {
        String word = "apple";
        word.chars().forEach(c -> trie.process((byte) c));
        assertEquals(word.length(), trie.getCurrentNode());
    }


    @Test
    void insertTwoNonOverlappingWords() {
        String word1 = "cat";
        word1.chars().forEach(c -> trie.process((byte) c));
        assertEquals(word1.length(), trie.getCurrentNode());

        trie.restart();

        String word2 = "mouse";
        word2.chars().forEach(c -> trie.process((byte) c));
        assertEquals(word1.length() + word2.length(), trie.getCurrentNode());
    }


    @Test
    void insertTwoWordsOneBeingAPrefixOfTheOther() {
        String word2 = "app";

        String word1 = word2 + "le";
        word1.chars().forEach(c -> trie.process((byte) c));
        assertEquals(word1.length(), trie.getCurrentNode());

        trie.restart();

        word2.chars().forEach(c -> trie.process((byte) c));
        assertEquals(word2.length(), trie.getCurrentNode());
    }


    @Test
    void insertTwoWordsHavingOverlappingPrefixes() {
        String prefix = "ap";

        String word1 = prefix + "ple";
        word1.chars().forEach(c -> trie.process((byte) c));
        assertEquals(word1.length(), trie.getCurrentNode());

        trie.restart();

        String word2 = prefix + "e";
        word2.chars().forEach(c -> trie.process((byte) c));
        assertEquals(word1.length() + word2.length() - prefix.length(), trie.getCurrentNode());
    }


    @Test
    void retrieveOneWord() {
        String word = "apple";
        word.chars().forEach(c -> trie.process((byte) c));
        assertEquals(word, new String(trie.getSequence(trie.getCurrentNode()), UTF_8));
    }


    @Test
    void retrieveTwoNonOverlappingWords() {
        String word1 = "cat";
        word1.chars().forEach(c -> trie.process((byte) c));
        short node1 = trie.getCurrentNode();

        trie.restart();

        String word2 = "mouse";
        word2.chars().forEach(c -> trie.process((byte) c));
        short node2 = trie.getCurrentNode();

        assertEquals(word1, new String(trie.getSequence(node1), UTF_8));
        assertEquals(word2, new String(trie.getSequence(node2), UTF_8));
    }


    @Test
    void retrieveTwoWordsOneBeingAPrefixOfTheOther() {
        String word2 = "app";

        String word1 = word2 + "le";
        word1.chars().forEach(c -> trie.process((byte) c));
        short node1 = trie.getCurrentNode();

        trie.restart();

        word2.chars().forEach(c -> trie.process((byte) c));
        short node2 = trie.getCurrentNode();

        assertEquals(word1, new String(trie.getSequence(node1), UTF_8));
        assertEquals(word2, new String(trie.getSequence(node2), UTF_8));
    }


    @Test
    void retrieveTwoWordsHavingOverlappingPrefixes() {
        String prefix = "ap";

        String word1 = prefix + "ple";
        word1.chars().forEach(c -> trie.process((byte) c));
        short node1 = trie.getCurrentNode();

        trie.restart();

        String word2 = prefix + "e";
        word2.chars().forEach(c -> trie.process((byte) c));
        short node2 = trie.getCurrentNode();

        assertEquals(word1, new String(trie.getSequence(node1), UTF_8));
        assertEquals(word2, new String(trie.getSequence(node2), UTF_8));
    }


    @Test
    void insertAndRetrieveWords() throws Exception {
        String refData = TestUtils.getStringData(TestUtils.getResourceURI("/sample.ref"));

        List<String> cities = Arrays.stream(refData.substring(1, refData.length() - 2).split("=.*?, ")).toList();
        List<Short> nodes = new ArrayList<>();

        cities.forEach(c -> {
            for (byte b : c.getBytes(UTF_8)) {
                trie.process(b);
            }
            nodes.addLast(trie.getCurrentNode());
            trie.restart();
        });
        assertEquals(cities.size(), nodes.size());

        List<String> retrievedCities = new ArrayList<>();
        nodes.forEach(n -> retrievedCities.addLast(new String(trie.getSequence(n), UTF_8)));

        assertEquals(cities, retrievedCities);
    }

}
