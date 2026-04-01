/*
 * Copyright 2017-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.javaformat.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

/**
 * Base class for command integration tests.
 *
 * @author Tim Sparg
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
abstract class AbstractCommandIntegrationTests {

	@Autowired
	IFactory factory;

	abstract Callable<Integer> command();

	int execute(StringWriter out, StringWriter err, String... args) {
		return new CommandLine(command(), this.factory).setOut(new PrintWriter(out))
			.setErr(new PrintWriter(err))
			.execute(args);
	}

	void copyFixture(Path fixturesDir, String name, Path target) throws IOException {
		Path fixtureDir = fixturesDir.resolve(name);
		try (Stream<Path> files = Files.walk(fixtureDir)) {
			files.filter(Files::isRegularFile).forEach((source) -> {
				try {
					Path dest = target.resolve(fixtureDir.relativize(source));
					Files.createDirectories(dest.getParent());
					Files.copy(source, dest);
				}
				catch (IOException ex) {
					throw new UncheckedIOException(ex);
				}
			});
		}
	}

}
