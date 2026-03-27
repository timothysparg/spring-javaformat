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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke test — verifies the Spring application context loads successfully.
 *
 * @author Tim Sparg
 */
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class SpringJavaFormatCliApplicationTests {

	@Autowired
	private SpringJavaFormatCommand command;

	@Autowired
	private IFactory factory;

	@Test
	void helpIsPrinted() {
		StringWriter out = new StringWriter();
		int exitCode = new CommandLine(this.command, this.factory).setOut(new PrintWriter(out)).execute("--help");
		assertThat(exitCode).isZero();
		assertThat(out.toString()).contains("Usage: spring-javaformat");
	}

	@Test
	void versionIsPrinted() {
		StringWriter out = new StringWriter();
		int exitCode = new CommandLine(this.command, this.factory).setOut(new PrintWriter(out)).execute("--version");
		assertThat(exitCode).isZero();
		assertThat(out.toString()).contains("spring-javaformat").containsPattern("\\S");
	}

}
