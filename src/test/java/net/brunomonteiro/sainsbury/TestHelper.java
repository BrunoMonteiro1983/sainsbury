package net.brunomonteiro.sainsbury;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestHelper {
	public static String getFileContent(String filename) throws IOException, URISyntaxException {
		URI resourceUri = Objects.requireNonNull(TestHelper.class.getClassLoader().getResource(filename)).toURI();
		Stream<String> lines = Files.lines(Paths.get(resourceUri));
		String response = lines.collect(Collectors.joining("\n"));

		lines.close();
		return response;
	}
}
