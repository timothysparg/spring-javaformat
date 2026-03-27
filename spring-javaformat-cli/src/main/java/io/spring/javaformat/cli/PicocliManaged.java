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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class whose fields are initialized by picocli via field injection rather than
 * via a constructor. This covers {@code @Spec}, {@code @Mixin}, and {@code @Option}
 * fields with a {@code defaultValue}, all of which picocli sets before invoking the
 * command.
 *
 * <p>
 * This annotation is registered with NullAway as an {@code ExternalInitAnnotation},
 * suppressing "field not initialized" errors for the annotated class without requiring
 * per-class {@code @SuppressWarnings("NullAway.Init")}.
 *
 * @author Tim Sparg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PicocliManaged {

}
