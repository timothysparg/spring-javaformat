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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader.IgnoredModulesOptions;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.ThreadModeSettings;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.RootModule;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

/**
 * Runs Spring checkstyle checks against a list of files.
 *
 * @author Tim Sparg
 */
@Component
class CheckstyleService {

	List<AuditEvent> run(List<File> files, Properties properties) throws CheckstyleException {
		try (InputStream is = getClass().getResourceAsStream("checkstyle.xml")) {
			return run(files, is, properties);
		}
		catch (Exception ex) {
			throw new CheckstyleException("Failed to load checkstyle configuration", ex);
		}
	}

	private List<AuditEvent> run(List<File> files, InputStream configStream, Properties properties)
			throws CheckstyleException {
		Configuration config = ConfigurationLoader.loadConfiguration(new InputSource(configStream),
				new PropertiesExpander(properties), IgnoredModulesOptions.EXECUTE,
				ThreadModeSettings.SINGLE_THREAD_MODE_INSTANCE);
		ClassLoader cl = getClass().getClassLoader();
		ModuleFactory factory = new PackageObjectFactory(Checker.class.getPackage().getName(), cl);
		RootModule rootModule = (RootModule) factory.createModule(config.getName());
		rootModule.setModuleClassLoader(cl);
		rootModule.configure(config);
		CollectingAuditListener listener = new CollectingAuditListener();
		rootModule.addListener(listener);
		try {
			rootModule.process(files);
		}
		finally {
			rootModule.destroy();
		}
		return listener.getViolations();
	}

	private static final class CollectingAuditListener implements AuditListener {

		private final List<AuditEvent> violations = new ArrayList<>();

		@Override
		public void auditStarted(AuditEvent event) {
		}

		@Override
		public void auditFinished(AuditEvent event) {
		}

		@Override
		public void fileStarted(AuditEvent event) {
		}

		@Override
		public void fileFinished(AuditEvent event) {
		}

		@Override
		public void addError(AuditEvent event) {
			if (event.getSeverityLevel() != SeverityLevel.IGNORE) {
				this.violations.add(event);
			}
		}

		@Override
		public void addException(AuditEvent event, Throwable throwable) {
		}

		List<AuditEvent> getViolations() {
			return this.violations;
		}

	}

}
