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

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application for the Spring JavaFormat CLI.
 *
 * @author Tim Sparg
 */
@SpringBootApplication
public class SpringJavaFormatCliApplication {

	protected SpringJavaFormatCliApplication() {
	}

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SpringJavaFormatCliApplication.class);
		application.setBannerMode(Banner.Mode.OFF);
		application.setLogStartupInfo(false);
		System.exit(SpringApplication.exit(application.run(args)));
	}

}
