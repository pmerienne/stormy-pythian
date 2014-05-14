/**
 * Copyright 2013-2015 Pierre Merienne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stormy.pythian.component.source.file;

import static com.google.common.collect.Lists.newArrayList;
import static stormy.pythian.model.annotation.ComponentType.STREAM_SOURCE;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import stormy.pythian.component.source.ListedFeaturesSource;
import stormy.pythian.model.annotation.Documentation;
import stormy.pythian.model.annotation.Property;
import stormy.pythian.model.instance.Feature;
import stormy.pythian.model.instance.Instance;
import stormy.pythian.model.instance.TextFeature;
import com.google.common.base.Splitter;

@SuppressWarnings("serial")
@Documentation(name = "Csv source", description = "Read and parse a given csv file", type = STREAM_SOURCE)
public class CsvSource extends ListedFeaturesSource {

    private final static Logger LOGGER = Logger.getLogger(CsvSource.class);

    @Property(name = "File", description = "The full path of the file to read", mandatory = true)
    private String filename;

    private Long currentPosition = 0L;

    @Override
    protected List<Instance> nextBatch() {
        List<Instance> instances = new ArrayList<>(maxBatchSize);

        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(filename, "r");
            file.seek(currentPosition);

            String line;
            while (instances.size() < maxBatchSize && (line = file.readLine()) != null) {
                try {
                    List<String> parts = newArrayList(Splitter.on(",").limit(mapper.size()).trimResults().split(line));
                    List<Feature<?>> features = new ArrayList<>(parts.size());
                    for (String part : parts) {
                        features.add(new TextFeature(part));
                    }

                    Instance instance = Instance.create(mapper);
                    instance.addFeatures(features);

                    instances.add(instance);
                } catch (Exception ex) {
                    LOGGER.warn(ex.getMessage() + " : Instance skipped '" + line + "'");
                }
            }

            currentPosition = file.getFilePointer();
            LOGGER.debug("Progress : " + new DecimalFormat("##.##").format(100.0 * (double) currentPosition / (double) file.length()) + "%");
            return instances;
        } catch (IOException e) {
            LOGGER.error("Error while reading " + filename + " : " + e.getMessage());
            return Collections.emptyList();
        } finally {
            try {
                if (file != null)
                    file.close();
            } catch (Exception e) {
                LOGGER.error("Error while closing " + filename + " : " + e.getMessage());
            }
        }
    }

    @Override
    protected void close() {
    }

    @Override
    protected void open() {
    }

}
