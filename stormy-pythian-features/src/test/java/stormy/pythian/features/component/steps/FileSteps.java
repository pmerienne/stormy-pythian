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
package stormy.pythian.features.component.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;

public class FileSteps {

    private List<File> created_files = new ArrayList<>();

    @Given("^a file \"([^\"]*)\" containing:$")
    public void a_file_containing(String pathname, String content) throws Throwable {
        File file = new File(pathname);
        file.delete();
        FileUtils.writeStringToFile(file, content);
        created_files.add(file);
    }

    @When("^I append to the file \"(.*?)\":$")
    public void i_append_to_the(String pathname, String content) throws Throwable {
        File file = new File(pathname);
        FileUtils.writeStringToFile(file, content, true);
    }

    @After
    public void delete_files() {
        for (File file : created_files) {
            file.delete();
        }

        created_files.clear();
    }
}
