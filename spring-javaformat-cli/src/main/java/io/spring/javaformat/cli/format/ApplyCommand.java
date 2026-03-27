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
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import io.spring.javaformat.cli.InputOptions;
import io.spring.javaformat.formatter.FileEdit;
import io.spring.javaformat.formatter.FileFormatterException;

/**
 * Apply formatting command.
 *
 * @author Tim Sparg
 */
@Component
@Command(name = "apply", mixinStandardHelpOptions = true, description = "Apply formatting to the codebase")
public class ApplyCommand implements Callable<Integer> {

	private static final Path CWD = Paths.get("").toAbsolutePath();

	@Spec
	private CommandSpec spec;

	@Mixin
	private InputOptions options;

	private final FormattingService formattingService;

	@SuppressWarnings("NullAway.Init")
	ApplyCommand(FormattingService formattingService) {
		this.formattingService = formattingService;
	}

	@Override
	public Integer call() {
		try {
			this.formattingService.format(this.options).filter(FileEdit::hasEdits).forEach(this::save);
			return 0;
		}
		catch (FileFormatterException ex) {
			err().println(ansi(
					"@|bold,red error:|@ unable to format file " + relativize(ex.getFile()) + ": " + ex.getMessage()));
			return 1;
		}
	}

	private PrintWriter err() {
		return this.spec.commandLine().getErr();
	}

	private PrintWriter out() {
		return this.spec.commandLine().getOut();
	}

	private void save(FileEdit edit) {
		out().println(ansi("@|bold,green formatted|@  " + relativize(edit.getFile())));
		edit.save();
	}

	private String ansi(String markup) {
		return Ansi.AUTO.string(markup);
	}

	private static String relativize(File file) {
		try {
			return CWD.relativize(file.toPath().toAbsolutePath()).toString();
		}
		catch (IllegalArgumentException ex) {
			return file.getAbsolutePath();
		}
	}

}
