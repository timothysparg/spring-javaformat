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

import java.util.Objects;

import gg.jte.generated.precompiled.StaticTemplates;
import gg.jte.generated.precompiled.Templates;
import gg.jte.output.PrintWriterOutput;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
public class CheckReportRenderer {

	private final Templates templates = new StaticTemplates();

	void render(CommandLine commandLine, CheckReport report) {
		if (report.hasError()) {
			renderError(commandLine, report);
		}
		else if (!report.hasViolations()) {
			renderSuccess(commandLine);
		}
		else if (report.combined()) {
			renderCombined(commandLine, report);
		}
		else {
			renderSeparate(commandLine, report);
		}
		commandLine.getOut().flush();
		commandLine.getErr().flush();
	}

	private void renderError(CommandLine commandLine, CheckReport report) {
		this.templates.checkError(Objects.requireNonNull(report.errorMessage()))
			.render(new PrintWriterOutput(commandLine.getErr()));
	}

	private void renderSuccess(CommandLine commandLine) {
		this.templates.checkSuccess().render(new PrintWriterOutput(commandLine.getOut()));
	}

	private void renderCombined(CommandLine commandLine, CheckReport report) {
		this.templates.checkCombined(report.formattingProblems(), report.checkstyleViolations())
			.render(new PrintWriterOutput(commandLine.getErr()));
	}

	private void renderSeparate(CommandLine commandLine, CheckReport report) {
		if (!report.skipFormat()) {
			this.templates.checkFormatting(report.formattingProblems())
				.render(new PrintWriterOutput(commandLine.getErr()));
		}
		if (!report.skipCheckstyle()) {
			this.templates.checkCheckstyleDetailed(report.checkstyleViolations())
				.render(new PrintWriterOutput(commandLine.getErr()));
		}
	}

}
