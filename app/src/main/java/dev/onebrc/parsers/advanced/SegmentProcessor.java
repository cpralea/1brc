package dev.onebrc.parsers.advanced;


import dev.onebrc.parsers.Statistics;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.nio.charset.StandardCharsets.UTF_8;


public class SegmentProcessor implements Callable<Map<String, Statistics>> {

    public record SegmentLimits(long begin, long end) {}


    private static final short TRIE_CHUNK_SIZE = 4 * 1024;

    private final FileChannel channel;
    private final SegmentLimits limits;


    public SegmentProcessor(FileChannel channel, SegmentLimits limits) {
        this.channel = channel;
        this.limits = limits;
    }


    @Override
    public Map<String, Statistics> call() {
        ByteTrie trie = new ByteTrie(TRIE_CHUNK_SIZE);
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment segment = channel.map(FileChannel.MapMode.READ_ONLY, limits.begin, limits.end, arena);
            var statistics = doScan(segment, trie);
            return statistics.entrySet().stream().collect(Collectors.toMap(
                e -> new String(trie.getSequence(e.getKey()), UTF_8),
                Map.Entry::getValue
            ));
        } catch (IOException e) {
            throw new RuntimeException("An I/O exception occurred: %s.".formatted(e.getMessage()));
        }
    }


    private Map<Short, Statistics> doScan(MemorySegment segment, ByteTrie trie) {
        Map<Short, Statistics> statistics = new HashMap<>();

        long idx = 0, end = segment.byteSize();

        while (idx < end) {
            byte b;
            short c = 0;
            int t = 0;

            while (true) {
                b = segment.get(JAVA_BYTE, idx++);
                if (b != ';') {
                    c = trie.process(b);
                } else {
                    break;
                }
            }
            trie.restart();

            boolean isNegative = segment.get(JAVA_BYTE, idx) == '-';
            if (isNegative) {
                idx++;
            }
            while (true) {
                b = segment.get(JAVA_BYTE, idx++);
                if (b != '\n') {
                    if (b != '.') {
                        t *= 10;
                        t += b - '0';
                    }
                } else {
                    break;
                }
            }
            if (isNegative) {
                t = -t;
            }

            Statistics s = statistics.get(c);
            if (s == null) {
                s = new Statistics();
                statistics.put(c, s);
            }
            s.record(t);
        }

        return statistics;
    }

}
