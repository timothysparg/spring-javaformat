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

package io.spring.javaformat.cli.scan;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.codehaus.plexus.util.DirectoryScanner;
import org.springframework.stereotype.Component;

import io.spring.javaformat.cli.InputOptions;

/**
 * Scans files from a directory using include and exclude patterns.
 *
 * @author Tim Sparg
 */
@Component
public class FileScanner {

	public List<File> scan(InputOptions options) {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(options.path);
		scanner.setIncludes(options.includes);
		scanner.setExcludes(options.excludes);
		scanner.addDefaultExcludes();
		scanner.setCaseSensitive(false);
		scanner.setFollowSymlinks(false);
		scanner.scan();
		return Arrays.stream(scanner.getIncludedFiles())
			.map((name) -> new File(options.path, name))
			.collect(Collectors.toList());
	}

}
