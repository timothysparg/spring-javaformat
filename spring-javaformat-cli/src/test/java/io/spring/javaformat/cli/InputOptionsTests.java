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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link InputOptions} — plain picocli unit tests with no Spring context.
 *
 * @author Tim Sparg
 */
class InputOptionsTests {

	private TestCommand command;

	private CommandLine commandLine;

	@BeforeEach
	void setUp() {
		this.command = new TestCommand();
		this.commandLine = new CommandLine(this.command);
	}

	@Nested
	class PathOption {

		@Test
		void acceptsExistingDirectory(@TempDir Path tempDir) {
			InputOptionsTests.this.commandLine.parseArgs(tempDir.toString());
			assertThat(InputOptionsTests.this.command.options.path).isEqualTo(tempDir.toFile().getAbsoluteFile());
		}

		@Test
		void rejectsNonExistentDirectory() {
			assertThatExceptionOfType(CommandLine.ParameterException.class)
				.isThrownBy(() -> InputOptionsTests.this.commandLine.parseArgs(
						"/nonexistent/path/that/does/not/exist"));
		}

		@Test
		void rejectsFile(@TempDir Path tempDir) throws IOException {
			File file = tempDir.resolve("test.txt").toFile();
			file.createNewFile();
			assertThatExceptionOfType(CommandLine.ParameterException.class)
				.isThrownBy(() -> InputOptionsTests.this.commandLine.parseArgs(file.getAbsolutePath()));
		}

	}

	@Nested
	class EncodingOption {

		@Test
		void acceptsValidCharset() {
			InputOptionsTests.this.commandLine.parseArgs("--encoding", "ISO-8859-1");
			assertThat(InputOptionsTests.this.command.options.encoding).isEqualTo(Charset.forName("ISO-8859-1"));
		}

		@Test
		void rejectsInvalidCharset() {
			assertThatExceptionOfType(CommandLine.ParameterException.class)
				.isThrownBy(() -> InputOptionsTests.this.commandLine.parseArgs("--encoding", "NOT-A-VALID-CHARSET"));
		}

	}

	@Nested
	class IncludesOption {

		@Test
		void acceptsValidGlobPattern() {
			InputOptionsTests.this.commandLine.parseArgs("--includes", "**/*.groovy");
			assertThat(InputOptionsTests.this.command.options.includes).containsExactly("**/*.groovy");
		}

		@Test
		void acceptsMultiplePatternsSplitByComma() {
			InputOptionsTests.this.commandLine.parseArgs("--includes", "**/*.java,**/*.kt");
			assertThat(InputOptionsTests.this.command.options.includes).containsExactly("**/*.java", "**/*.kt");
		}

		@Test
		void rejectsInvalidGlobPattern() {
			assertThatExceptionOfType(CommandLine.ParameterException.class)
				.isThrownBy(() -> InputOptionsTests.this.commandLine.parseArgs("--includes", "[invalid"));
		}

	}

	@Nested
	class ExcludesOption {

		@Test
		void acceptsValidGlobPattern() {
			InputOptionsTests.this.commandLine.parseArgs("--excludes", "**/build/**");
			assertThat(InputOptionsTests.this.command.options.excludes).containsExactly("**/build/**");
		}

		@Test
		void rejectsInvalidGlobPattern() {
			assertThatExceptionOfType(CommandLine.ParameterException.class)
				.isThrownBy(() -> InputOptionsTests.this.commandLine.parseArgs("--excludes", "[invalid"));
		}

	}

	@Nested
	class LineSeparatorOption {

		@Test
		void acceptsCr() {
			InputOptionsTests.this.commandLine.parseArgs("--line-separator", "CR");
			assertThat(InputOptionsTests.this.command.options.resolveLineSeparator()).isEqualTo("\r");
		}

		@Test
		void rejectsInvalidValue() {
			assertThatExceptionOfType(CommandLine.ParameterException.class)
				.isThrownBy(() -> InputOptionsTests.this.commandLine.parseArgs("--line-separator", "INVALID"));
		}

	}

	@PicocliManaged
	@Command(name = "test")
	private static class TestCommand implements Callable<Integer> {

		@Mixin
		InputOptions options;

		@Override
		public Integer call() {
			return 0;
		}

	}

}
