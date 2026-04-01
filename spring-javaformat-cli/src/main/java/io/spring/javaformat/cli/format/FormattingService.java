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

package io.spring.javaformat.cli.format;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import io.spring.javaformat.cli.InputOptions;
import io.spring.javaformat.cli.scan.FileFormatterFactory;
import io.spring.javaformat.cli.scan.FileScanner;
import io.spring.javaformat.formatter.FileEdit;
import io.spring.javaformat.formatter.FileFormatter;
import io.spring.javaformat.formatter.FileFormatterException;

/**
 * Applies formatting to scanned files.
 *
 * @author Tim Sparg
 */
@Component
public class FormattingService {

	private final FileFormatterFactory fileFormatterFactory;

	private final FileScanner fileScanner;

	FormattingService(FileFormatterFactory fileFormatterFactory, FileScanner fileScanner) {
		this.fileFormatterFactory = fileFormatterFactory;
		this.fileScanner = fileScanner;
	}

	public Stream<FileEdit> format(InputOptions options) throws FileFormatterException {
		return format(this.fileScanner.scan(options), options);
	}

	public Stream<FileEdit> format(List<File> files, InputOptions options) throws FileFormatterException {
		FileFormatter formatter = this.fileFormatterFactory.create(options.path);
		return formatter.formatFiles(files, options.encoding, options.resolveLineSeparator());
	}

}
