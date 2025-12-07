package dev.onebrc.parsers.basic;


import dev.onebrc.parsers.Statistics;
import dev.onebrc.parsers.Parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;


public class BasicParser extends Parser {

    public void parse(String file, PrintStream out) {
        try (Reader reader = open(file)) {
            var output = print(scan(reader));
            out.println(output);
        } catch (IOException e) {
            throw new RuntimeException("An I/O exception occurred: %s.".formatted(e.getMessage()));
        }
    }


    private Map<String, Statistics> scan(Reader reader) {
        Map<String, Statistics> statistics = new HashMap<>();

        Scanner scanner = new Scanner(reader);
        scanner.useDelimiter("[;\n]");

        while (scanner.hasNext()) {
            String city = scanner.next();
            String temperature = scanner.next();
            statistics.putIfAbsent(city, new Statistics());
            statistics.get(city).record(
                Integer.parseInt(
                    temperature.replace(".", "")
                )
            );
        }

        return statistics;
    }


    private Reader open(String file) {
        return propagateExceptions(
            () -> new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF_8)),
            "Could not open file"
        );
    }

}
