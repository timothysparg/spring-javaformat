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
import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;

import io.spring.javaformat.cli.check.CheckCommand;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link CheckCommand}.
 *
 * @author Tim Sparg
 */
class CheckCommandIntegrationTests extends AbstractCommandIntegrationTests {

	private static final Path CHECK_FIXTURES_DIR = Path.of("src/test/resources/fixtures/check");

	private static final Path FORMAT_FIXTURES_DIR = Path.of("src/test/resources/fixtures/format");

	@Autowired
	CheckCommand checkCommand;

	@Override
	CheckCommand command() {
		return this.checkCommand;
	}

	@Nested
	class SkipFormat {

		@Test
		void violationsAreReported(@TempDir Path tempDir) throws Exception {
			copyFixture(CHECK_FIXTURES_DIR, "star-import", tempDir);

			StringWriter err = new StringWriter();
			int exitCode = execute(new StringWriter(), err, "--skip-format", "--project-root-package",
					"com.example", tempDir.toString());

			assertThat(exitCode).isEqualTo(1);
			assertThat(err.toString()).contains("WithStarImport.java");
		}

		@Test
		void includesOnlyChecksMatchingFiles(@TempDir Path tempDir) throws Exception {
			copyFixture(CHECK_FIXTURES_DIR, "includes-filter", tempDir);

			StringWriter err = new StringWriter();
			int exitCode = execute(new StringWriter(), err, "--skip-format", "--project-root-package",
					"com.example", "--includes", "**/WithStarImport.java", tempDir.toString());

			assertThat(exitCode).isEqualTo(1);
			assertThat(err.toString()).contains("WithStarImport.java");
			assertThat(err.toString()).doesNotContain("ExcludedFromIncludes.java");
		}

		@Test
		void excludesSkipsExcludedFiles(@TempDir Path tempDir) throws Exception {
			copyFixture(CHECK_FIXTURES_DIR, "excludes-filter", tempDir);

			StringWriter err = new StringWriter();
			int exitCode = execute(new StringWriter(), err, "--skip-format", "--project-root-package",
					"com.example", "--excludes", "excluded/**", tempDir.toString());

			assertThat(exitCode).isEqualTo(1);
			assertThat(err.toString()).contains("WithStarImport.java");
			assertThat(err.toString()).doesNotContain("ExcludedWithStarImport.java");
		}

		@Test
		void customHeaderFileIsUsed(@TempDir Path tempDir) throws Exception {
			copyFixture(CHECK_FIXTURES_DIR, "custom-header", tempDir);

			int exitCode = execute(new StringWriter(), new StringWriter(), "--skip-format", "--header-type", "FILE",
					"--header-file",
					tempDir.resolve("header.txt").toString(), tempDir.toString());

			assertThat(exitCode).isEqualTo(0);
		}

	}

	@Nested
	class SkipCheckstyle {

		@Test
		void violationsAreReported(@TempDir Path tempDir) throws Exception {
			copyFixture(FORMAT_FIXTURES_DIR, "default", tempDir);

			StringWriter err = new StringWriter();
			int exitCode = execute(new StringWriter(), err, "--skip-checkstyle", tempDir.toString());

			assertThat(exitCode).isEqualTo(1);
			assertThat(err.toString()).contains("Main.java");
		}

		@Test
		void nonDefaultEncodingDetectsViolations(@TempDir Path tempDir) throws Exception {
			copyFixture(FORMAT_FIXTURES_DIR, "latin1", tempDir);

			StringWriter err = new StringWriter();
			int exitCode = execute(new StringWriter(), err, "--skip-checkstyle", "--encoding", "ISO-8859-1",
					tempDir.toString());

			assertThat(exitCode).isEqualTo(1);
			assertThat(err.toString()).contains("Main.java");
		}

	}

	@Nested
	class Combined {

		@Test
		void groupsViolationsByFile(@TempDir Path tempDir) throws Exception {
			copyFixture(CHECK_FIXTURES_DIR, "combined", tempDir);

			StringWriter err = new StringWriter();
			int exitCode = execute(new StringWriter(), err, "--header-type", "UNCHECKED", tempDir.toString());

			assertThat(exitCode).isEqualTo(1);
			assertThat(err.toString()).contains("Violations found in 1 file");
			assertThat(err.toString()).contains("Main.java").contains("format, checkstyle");
			assertThat(err.toString()).doesNotContain("Checkstyle violations found");
			assertThat(err.toString()).doesNotContain(":4:");
		}

	}

}
