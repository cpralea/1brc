package dev.onebrc;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


public class TestUtils {

    public static String getResourceURI(String name) throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(
                TestUtils.class.getResource(name)
        ).toURI()).toString();
    }


    public static String getStringData(String uri) throws IOException {
        return Files.readString(Path.of(uri));
    }

}
