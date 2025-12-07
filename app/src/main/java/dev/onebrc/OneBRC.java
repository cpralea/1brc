package dev.onebrc;


import dev.onebrc.parsers.advanced.AdvancedParser;
import dev.onebrc.parsers.basic.BasicParser;

import java.io.PrintStream;


public class OneBRC {

    public enum Parser { Basic, Advanced }


    public static void parse(Parser parser, String file, PrintStream out) {
        switch (parser) {
            case Basic -> new BasicParser().parse(file, out);
            case Advanced -> new AdvancedParser().parse(file, out);
        }
    }

}
