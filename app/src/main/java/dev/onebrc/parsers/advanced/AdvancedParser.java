package dev.onebrc.parsers.advanced;


import dev.onebrc.parsers.Parser;
import dev.onebrc.parsers.Statistics;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dev.onebrc.Config.DEFAULT_NUM_CORES;
import static dev.onebrc.Config.DEFAULT_NUM_SEGMENTS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;


public class AdvancedParser extends Parser {

    @Override
    public void parse(String file, PrintStream out) {
        try (
            FileChannel channel = open(file);
            Arena arena = Arena.ofConfined();
            ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_NUM_CORES)
        ) {
            MemorySegment segment = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size(), arena);
            var statistics = propagateExceptions(() -> executor.invokeAll(
                subSegmentProcessorsOf(channel, segment)
            )).stream().map(r -> propagateExceptions(r::get)).reduce(new HashMap<>(),
                this::statisticsAccumulator
            );
            out.println(print(statistics));
        } catch (IOException e) {
            throw new RuntimeException("An I/O exception occurred: %s.".formatted(e.getMessage()));
        }
    }


    private List<SegmentProcessor> subSegmentProcessorsOf(FileChannel channel, MemorySegment memorySegment) {
        List<SegmentProcessor> processors = new ArrayList<>(DEFAULT_NUM_SEGMENTS);

        long offset = 0, limit = memorySegment.byteSize();
        do {
            SegmentProcessor processor;
            long size = limit / DEFAULT_NUM_SEGMENTS;

            if (offset + size < limit) {
                byte b;
                do {
                    b = memorySegment.get(JAVA_BYTE, offset + size);
                    size++;
                } while (b != '\n');
            } else {
                size = limit - offset;
            }

            processor = new SegmentProcessor(channel, new SegmentProcessor.SegmentLimits(offset, size));
            processors.addLast(processor);

            offset += size;
        } while (offset < limit);

        return processors;
    }


    private Map<String, Statistics> statisticsAccumulator(Map<String, Statistics> s1, Map<String, Statistics> s2) {
        s2.forEach((c, s2s) -> {
            var s1s = s1.putIfAbsent(c, s2s);
            if (s1s != null) {
                s1s.mergeIn(s2s);
            }
        });
        return s1;
    }


    private FileChannel open(String file) {
        return propagateExceptions(
            () -> FileChannel.open(Path.of(file), StandardOpenOption.READ),
            "Could not open file"
        );
    }

}
