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

import java.util.Properties;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

import io.spring.javaformat.cli.InputOptions;

/**
 * Check formatting and checkstyle violations.
 *
 * @author Tim Sparg
 */
@Component
@Command(name = "check", mixinStandardHelpOptions = true,
		description = "Check the codebase for formatting and checkstyle violations")
public class CheckCommand implements Callable<Integer> {

	@Spec
	private CommandSpec spec;

	@Mixin
	private InputOptions formatOptions;

	@Option(names = { "-t", "--header-type" }, defaultValue = "APACHE2",
			description = "Header type (${COMPLETION-CANDIDATES}). Default: ${DEFAULT-VALUE}")
	private HeaderType headerType;

	@Option(names = { "-c", "--header-copyright-pattern" }, defaultValue = "20\\d\\d(-20\\d\\d|-present)?",
			description = "Copyright year regex pattern. Default: ${DEFAULT-VALUE}")
	private String headerCopyrightPattern;

	@Option(names = { "-f", "--header-file" }, defaultValue = "",
			description = "Path to a custom header file. Required when --header-type is 'file'.")
	private String headerFile;

	@Option(names = { "-r", "--project-root-package" }, defaultValue = "org.springframework",
			description = "Root package used for import ordering. Default: ${DEFAULT-VALUE}")
	private String projectRootPackage;

	@Option(names = { "-s", "--avoid-static-import-excludes" }, split = ",", defaultValue = "",
			description = "Repeatable or comma-separated static import patterns to allow.")
	private String[] avoidStaticImportExcludes;

	@ArgGroup
	private RunMode runMode;

	private final CheckRunner checkRunner;

	private final CheckReportRenderer checkReportRenderer;

	@SuppressWarnings("NullAway.Init")
	CheckCommand(CheckRunner checkRunner, CheckReportRenderer checkReportRenderer) {
		this.checkRunner = checkRunner;
		this.checkReportRenderer = checkReportRenderer;
	}

	@Override
	public Integer call() {
		CheckRunner.Inputs inputs = createInputs();
		CheckReport report = this.checkRunner.run(inputs);
		this.checkReportRenderer.render(this.spec.commandLine(), report);
		return report.exitCode();
	}

	private CheckRunner.Inputs createInputs() {
		boolean skipCheckstyle = this.runMode != null && this.runMode.skipCheckstyle;
		if (!skipCheckstyle && this.headerType == HeaderType.FILE && this.headerFile.isEmpty()) {
			throw new ParameterException(this.spec.commandLine(),
					"--header-file is required when --header-type is FILE");
		}
		return new CheckRunner.Inputs(this.formatOptions, buildCheckstyleProperties(),
				this.runMode != null && this.runMode.skipFormat, skipCheckstyle);
	}

	private Properties buildCheckstyleProperties() {
		Properties properties = new Properties();
		properties.setProperty("headerType", this.headerType.value);
		properties.setProperty("headerCopyrightPattern", this.headerCopyrightPattern);
		if (!this.headerFile.isEmpty()) {
			properties.setProperty("headerFile", this.headerFile);
		}
		properties.setProperty("projectRootPackage", this.projectRootPackage);
		properties.setProperty("avoidStaticImportExcludes", String.join(",",
				java.util.Arrays.stream(this.avoidStaticImportExcludes).filter((s) -> !s.isEmpty()).toList()));
		return properties;
	}

	static class RunMode {

		@Option(names = { "-S", "--skip-checkstyle" }, defaultValue = "false",
				description = "Skip checkstyle checks, only check source formatting")
		boolean skipCheckstyle;

		@Option(names = { "-F", "--skip-format" }, defaultValue = "false",
				description = "Skip source formatting check, only run checkstyle")
		boolean skipFormat;

	}

	enum HeaderType {

		APACHE2("apache2"), NONE("none"), UNCHECKED("unchecked"), REGEXP("regexp"), FILE("file");

		private final String value;

		HeaderType(String value) {
			this.value = value;
		}

	}

}
