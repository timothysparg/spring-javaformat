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

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;

import io.spring.javaformat.cli.format.ApplyCommand;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ApplyCommand}.
 *
 * @author Tim Sparg
 */
class ApplyCommandIntegrationTests extends AbstractCommandIntegrationTests {

	private static final Path FIXTURES_DIR = Path.of("src/test/resources/fixtures/format");

	@Autowired
	ApplyCommand applyCommand;

	@Override
	ApplyCommand command() {
		return this.applyCommand;
	}

	@Test
	void defaultEncodingFormatsFile(@TempDir Path tempDir) throws Exception {
		copyFixture(FIXTURES_DIR, "default", tempDir);
		String before = Files.readString(tempDir.resolve("Main.java"));

		execute(new StringWriter(), new StringWriter(), tempDir.toString());

		assertThat(Files.readString(tempDir.resolve("Main.java"))).isNotEqualTo(before);
	}

	@Test
	void nonDefaultEncodingFormatsFile(@TempDir Path tempDir) throws Exception {
		Charset latin1 = Charset.forName("ISO-8859-1");
		copyFixture(FIXTURES_DIR, "latin1", tempDir);
		String before = Files.readString(tempDir.resolve("Main.java"), latin1);

		execute(new StringWriter(), new StringWriter(), "--encoding", "ISO-8859-1", tempDir.toString());

		String after = Files.readString(tempDir.resolve("Main.java"), latin1);
		assertThat(after).isNotEqualTo(before);
		assertThat(after).contains("é");
	}

	@Test
	void includesOnlyFormatsMatchingFiles(@TempDir Path tempDir) throws Exception {
		copyFixture(FIXTURES_DIR, "default", tempDir);
		String mainBefore = readFile(tempDir, "Main.java");
		String noteBefore = Files.readString(tempDir.resolve("notes.txt"));
		String excludedBefore = readFile(tempDir, "excluded/Excluded.java");

		execute(new StringWriter(), new StringWriter(), "--includes", "**/Main.java", tempDir.toString());

		assertThat(readFile(tempDir, "Main.java")).isNotEqualTo(mainBefore);
		assertThat(Files.readString(tempDir.resolve("notes.txt"))).isEqualTo(noteBefore);
		assertThat(readFile(tempDir, "excluded/Excluded.java")).isEqualTo(excludedBefore);
	}

	@Test
	void excludesSkipsExcludedFiles(@TempDir Path tempDir) throws Exception {
		copyFixture(FIXTURES_DIR, "default", tempDir);
		String mainBefore = readFile(tempDir, "Main.java");
		String excludedBefore = readFile(tempDir, "excluded/Excluded.java");

		execute(new StringWriter(), new StringWriter(), "--excludes", "excluded/**", tempDir.toString());

		assertThat(readFile(tempDir, "Main.java")).isNotEqualTo(mainBefore);
		assertThat(readFile(tempDir, "excluded/Excluded.java")).isEqualTo(excludedBefore);
	}

	@Test
	void springJavaFormatConfigChangesIndentationStyle(@TempDir Path tempDir) throws Exception {
		copyFixture(FIXTURES_DIR, "spaces-config", tempDir);

		execute(new StringWriter(), new StringWriter(), tempDir.toString());

		String formatted = readFile(tempDir, "Main.java");
		assertThat(formatted).contains("    void method() {");
		assertThat(formatted).doesNotContain("\tvoid method() {");
	}

	private String readFile(Path dir, String path) throws Exception {
		return Files.readString(dir.resolve(path));
	}

}
