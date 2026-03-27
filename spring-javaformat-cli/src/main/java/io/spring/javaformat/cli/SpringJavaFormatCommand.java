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

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

/**
 * Root picocli command.
 *
 * @author Tim Sparg
 */
@Component
@Command(name = "spring-javaformat", mixinStandardHelpOptions = true, versionProvider = CliVersionProvider.class,
		description = "Formats and checks Java source files")
@PicocliManaged
class SpringJavaFormatCommand implements Runnable {

	@Spec
	private CommandSpec spec;

	@Override
	public void run() {
		this.spec.commandLine().usage(System.out);
	}

}
