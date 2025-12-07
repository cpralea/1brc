package dev.onebrc.parsers;


import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.Callable;


public abstract class Parser {


    public abstract void parse(String file, PrintStream out);


    protected String print(Map<String, Statistics> statistics) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');

        statistics.entrySet().stream()
            .sorted(Map.Entry.comparingByKey()).forEach(entry -> {
                var city = entry.getKey();
                var stats = entry.getValue();
                builder
                    .append(city)
                    .append('=');
                builder
                    .append(stats.min)
                    .insert(builder.length() - 1, '.')
                    .append('/')
                    .append("%.1f".formatted(Math.round(1.0f *
                        stats.sum / stats.count
                    ) / 10.0f))
                    .append('/')
                    .append(stats.max)
                    .insert(builder.length() - 1, '.')
                    .append(", ");
            });
        if (builder.length() > 1) {
            builder.setLength(builder.length() - 2);
        }

        builder.append('}');
        return builder.toString();
    }


    protected static <T> T propagateExceptions(Callable<T> block, String message) {
        try {
            return block.call();
        } catch (Exception e) {
            throw new RuntimeException(message  + ": %s.".formatted(e.getMessage()));
        }
    }


    protected static <T> T propagateExceptions(Callable<T> block) {
        return propagateExceptions(block, "An exception occurred");
    }

}
