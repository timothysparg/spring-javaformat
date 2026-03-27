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
import java.nio.charset.Charset;
import java.nio.file.FileSystems;

import org.jspecify.annotations.Nullable;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.TypeConversionException;

/**
 * Shared options for commands that scan and format files.
 *
 * @author Tim Sparg
 */
@PicocliManaged
public class InputOptions {

	/** Base directory to scan. */
	@Parameters(index = "0", arity = "0..1", defaultValue = ".", paramLabel = "PATH",
			description = "Base directory to scan. Default: ${DEFAULT-VALUE}", converter = DirectoryConverter.class)
	public File path;

	/** Glob patterns for files to include. */
	@Option(names = { "-i", "--includes" }, split = ",", defaultValue = "**/*.java",
			description = "Repeatable or comma-separated include patterns. Default: ${DEFAULT-VALUE}",
			converter = GlobConverter.class)
	public String[] includes;

	/** Glob patterns for files to exclude. */
	@Option(names = { "-x", "--excludes" }, split = ",",
			defaultValue = "**/target/**,**/generated-sources/**,**/generated-test-sources/**",
			description = "Repeatable or comma-separated exclude patterns. Default: ${DEFAULT-VALUE}",
			converter = GlobConverter.class)
	public String[] excludes;

	/** File encoding to use when reading and writing files. */
	@Option(names = { "-e", "--encoding" }, defaultValue = "UTF-8",
			description = "File encoding. Default: ${DEFAULT-VALUE}")
	public Charset encoding;

	@Option(names = { "-l", "--line-separator" },
			description = "Line separator (${COMPLETION-CANDIDATES}). If not specified, the existing line separator in each file is preserved.")
	@Nullable
	LineSeparator lineSeparator;

	@Nullable
	public String resolveLineSeparator() {
		return (this.lineSeparator != null) ? this.lineSeparator.value : null;
	}

	enum LineSeparator {

		CR("\r"), LF("\n"), CRLF("\r\n");

		private final String value;

		LineSeparator(String value) {
			this.value = value;
		}

	}

	static final class GlobConverter implements ITypeConverter<String> {

		@Override
		public String convert(String value) throws TypeConversionException {
			try {
				FileSystems.getDefault().getPathMatcher("glob:" + value);
			}
			catch (IllegalArgumentException ex) {
				throw new TypeConversionException("Invalid glob pattern '" + value + "': " + ex.getMessage());
			}
			return value;
		}

	}

	static final class DirectoryConverter implements ITypeConverter<File> {

		@Override
		public File convert(String value) throws TypeConversionException {
			File dir = new File(value).getAbsoluteFile();
			if (!dir.exists()) {
				throw new TypeConversionException("Directory does not exist: " + dir);
			}
			if (!dir.isDirectory()) {
				throw new TypeConversionException("Not a directory: " + dir);
			}
			return dir;
		}

	}

}
