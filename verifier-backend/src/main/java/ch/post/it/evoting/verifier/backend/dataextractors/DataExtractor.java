/*
 * Copyright 2023 Post CH Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.post.it.evoting.verifier.backend.dataextractors;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import org.jsfr.json.Collector;
import org.jsfr.json.JsonSurfer;

/**
 * Class to be extended by all data extractors that want to be able to extract particular fields of JSON files. Based on JsonSurfer, each data
 * extractor needs to define its own configuration of value boxes as well its configuration for the extraction of fields.
 */
public abstract class DataExtractor<V, R> {
	private static final String DATA_EXTRACTION_ERROR_MESSAGE = "Could not extract data of payload from file. [file name: %s]";

	private final JsonSurfer jsonSurfer;
	private final Function<Collector, V> valueBoxConfiguration;
	private final Function<V, R> dataExtractionConfiguration;

	protected DataExtractor(final JsonSurfer jsonSurfer, final Function<Collector, V> valueBoxConfiguration,
			final Function<V, R> dataExtractionConfiguration) {
		this.jsonSurfer = jsonSurfer;
		this.valueBoxConfiguration = valueBoxConfiguration;
		this.dataExtractionConfiguration = dataExtractionConfiguration;
	}

	public R load(final Path path) {
		try (final InputStream fileInputStream = new BufferedInputStream(Files.newInputStream(path))) {
			final Collector collector = jsonSurfer.collector(fileInputStream);
			final V valueBoxes = valueBoxConfiguration.apply(collector);
			collector.exec();
			return dataExtractionConfiguration.apply(valueBoxes);

		} catch (final IOException e) {
			final String errorMessage = String.format(DATA_EXTRACTION_ERROR_MESSAGE, path.toFile().getName());
			throw new UncheckedIOException(errorMessage, e);
		}
	}
}
