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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import picocli.CommandLine.Help.Ansi;

public final class RenderHelper {

	private static final Path CWD = Paths.get("").toAbsolutePath();

	private RenderHelper() {
	}

	public static String plural(long count, String noun) {
		return count + " " + noun + (count == 1 ? "" : "s");
	}

	public static String styledPath(File file) {
		return Ansi.AUTO.string("@|cyan " + relativize(file) + "|@");
	}

	public static String styledPath(String path) {
		return Ansi.AUTO.string("@|cyan " + path + "|@");
	}

	public static String styledLineNumber(int line) {
		return Ansi.AUTO.string("@|bold " + line + "|@");
	}

	public static String styledMessage(String message) {
		return Ansi.AUTO.string("@|faint " + message + "|@");
	}

	public static String success(String message) {
		return Ansi.AUTO.string("@|bold,green " + message + "|@");
	}

	public static String error(String message) {
		return Ansi.AUTO.string("@|bold,red error:|@ " + message);
	}

	public static String formattingHeader(int count) {
		return "\n" + Ansi.AUTO.string("@|bold,underline,red Formatting violations found|@") + " in "
				+ plural(count, "file") + ":";
	}

	public static String checkstyleHeader(List<AuditEvent> violations) {
		long fileCount = violations.stream().map(AuditEvent::getFileName).distinct().count();
		return "\n" + Ansi.AUTO.string("@|bold,underline,red Checkstyle violations found|@") + " in "
				+ plural(fileCount, "file") + " (" + plural(violations.size(), "violation") + "):";
	}

	public static String combinedHeader(int count) {
		return "\n" + Ansi.AUTO.string("@|bold,underline,red Violations found|@") + " in " + plural(count, "file")
				+ ":";
	}

	public static List<String> combinedLines(List<File> formattingProblems, List<AuditEvent> checkstyleViolations) {
		return Stream
			.concat(formattingProblems.stream().map((f) -> Map.entry(relativize(f), "format")),
					checkstyleViolations.stream()
						.map((v) -> Map.entry(relativize(new File(v.getFileName())), "checkstyle")))
			.collect(Collectors.groupingBy(Map.Entry::getKey, LinkedHashMap::new,
					Collectors.mapping(Map.Entry::getValue, Collectors.toCollection(LinkedHashSet::new))))
			.entrySet()
			.stream()
			.map((e) -> styledPath(e.getKey()) + " "
				+ Ansi.AUTO.string(e.getValue().stream().collect(Collectors.joining(", ", "@|faint [", "]|@"))))
			.toList();
	}

	public static String relativize(File file) {
		try {
			return CWD.relativize(file.toPath().toAbsolutePath()).toString();
		}
		catch (IllegalArgumentException ex) {
			return file.getAbsolutePath();
		}
	}

}
