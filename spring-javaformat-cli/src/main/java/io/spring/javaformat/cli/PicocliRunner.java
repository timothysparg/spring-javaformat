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

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

/**
 * Runs the picocli command line.
 *
 * @author Tim Sparg
 */
@Component
class PicocliRunner implements CommandLineRunner, ExitCodeGenerator {

	private final SpringJavaFormatCommand command;

	private final IFactory factory;

	private int exitCode;

	PicocliRunner(SpringJavaFormatCommand command, IFactory factory) {
		this.command = command;
		this.factory = factory;
	}

	@Override
	public void run(String... args) {
		this.exitCode = new CommandLine(this.command, this.factory).execute(args);
	}

	@Override
	public int getExitCode() {
		return this.exitCode;
	}

}
