package dev.onebrc;


import picocli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;


@CommandLine.Command(name = "1brc", version = "1.0",
    description = "One Billion Rows Challenge",
    mixinStandardHelpOptions = true)
class Main implements Runnable {

    @CommandLine.Option(names = "-p", arity = "1", paramLabel = "PARSER",
        defaultValue = "Basic", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
        description = "The file parser. One of: ${COMPLETION-CANDIDATES}.")
    private OneBRC.Parser parser;

    @CommandLine.Option(names = "-o", arity = "1", paramLabel = "OUT_FILE",
        description = "The name of the output file to generate. Defaults to printing to the console.")
    private String outFileName;

    @CommandLine.Parameters(index = "0", arity = "1", paramLabel = "IN_FILE",
        description = "The name of the input file to process.")
    private String inFileName;


    @Override
    public void run() {
        OneBRC.parse(parser, inFileName, getOutStream(outFileName));
    }


    private PrintStream getOutStream(String outFileName) {
        if (outFileName == null)
            return System.out;

        try {
            return new PrintStream(outFileName, UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create or open file: %s.".formatted(e.getMessage()));
        }
    }


    void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

}
