package dev.onebrc.parsers;


import dev.onebrc.TestUtils;
import dev.onebrc.parsers.advanced.AdvancedParser;
import dev.onebrc.parsers.basic.BasicParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ParserTest {

    static String in;
    static String ref;
    static String refData;


    @BeforeAll
    static void setUp() throws Exception {
        in = TestUtils.getResourceURI("/sample.txt");
        ref = TestUtils.getResourceURI("/sample.ref");
        refData = TestUtils.getStringData(ref);
    }


    @Test
    void basic() {
        testParser(new BasicParser());
    }


    @Test
    void advanced() {
        testParser(new AdvancedParser());
    }


    void testParser(Parser parser) {
        var data = new ByteArrayOutputStream();
        var out = new PrintStream(data);

        parser.parse(in, out);

        assertEquals(refData, data.toString(UTF_8));
    }

}
