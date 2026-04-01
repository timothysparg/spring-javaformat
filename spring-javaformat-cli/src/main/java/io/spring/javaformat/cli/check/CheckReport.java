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

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import org.jspecify.annotations.Nullable;

record CheckReport(List<File> formattingProblems, List<AuditEvent> checkstyleViolations, boolean skipFormat,
		boolean skipCheckstyle, @Nullable String errorMessage, boolean checkstyleFailure) {

	static CheckReport failure(boolean skipFormat, boolean skipCheckstyle, String errorMessage, boolean checkstyleFailure) {
		return new CheckReport(List.of(), List.of(), skipFormat, skipCheckstyle, errorMessage, checkstyleFailure);
	}

	boolean hasViolations() {
		return !this.formattingProblems.isEmpty() || !this.checkstyleViolations.isEmpty();
	}

	boolean hasError() {
		return this.errorMessage != null;
	}

	int exitCode() {
		return (hasError() || hasViolations()) ? 1 : 0;
	}

	boolean combined() {
		return !this.skipFormat && !this.skipCheckstyle;
	}

}
