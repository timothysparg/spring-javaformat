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

package io.spring.javaformat.cli.check;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import org.springframework.stereotype.Component;

import io.spring.javaformat.cli.InputOptions;
import io.spring.javaformat.cli.format.FormattingService;
import io.spring.javaformat.cli.scan.FileScanner;
import io.spring.javaformat.formatter.FileEdit;

@Component
public class CheckRunner {

	private final FormattingService formattingService;

	private final CheckstyleService checkstyleService;

	private final FileScanner fileScanner;

	CheckRunner(FormattingService formattingService, CheckstyleService checkstyleService, FileScanner fileScanner) {
		this.formattingService = formattingService;
		this.checkstyleService = checkstyleService;
		this.fileScanner = fileScanner;
	}

	CheckReport run(Inputs inputs) {
		try {
			List<File> files = this.fileScanner.scan(inputs.formatOptions());
			List<File> formattingProblems = inputs.skipFormat() ? List.of() : collectFormattingProblems(files, inputs);
			List<AuditEvent> checkstyleViolations = inputs.skipCheckstyle() ? List.of()
					: this.checkstyleService.run(files, inputs.checkstyleProperties());
			return new CheckReport(formattingProblems, checkstyleViolations, inputs.skipFormat(), inputs.skipCheckstyle(),
					null, false);
		}
		catch (CheckstyleException ex) {
			return CheckReport.failure(inputs.skipFormat(), inputs.skipCheckstyle(),
					"unable to run checkstyle: " + ex.getMessage(), true);
		}
		catch (Exception ex) {
			return CheckReport.failure(inputs.skipFormat(), inputs.skipCheckstyle(),
					"unable to check formatting: " + ex.getMessage(), false);
		}
	}

	private List<File> collectFormattingProblems(List<File> files, Inputs inputs) throws Exception {
		return this.formattingService.format(files, inputs.formatOptions())
			.filter(FileEdit::hasEdits)
			.map(FileEdit::getFile)
			.collect(Collectors.toList());
	}

	record Inputs(InputOptions formatOptions, Properties checkstyleProperties,
						boolean skipFormat, boolean skipCheckstyle) {
	}

}
