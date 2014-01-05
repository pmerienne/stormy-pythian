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
package stormy.pythian.core.testing;

import java.util.List;

import org.json.simple.JSONValue;

public class DRPCUtils {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Object> List<List<T>> extractValues(String drpcResult) {
		List<List<T>> values = (List) JSONValue.parse(drpcResult);
		return values;
	}

	public static <T extends Object> T extractSingleValue(String drpcResult) {
		return extractSingleValue(drpcResult, 0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Object> T extractSingleValue(String drpcResult, int index) {
		List<List<Object>> values = (List) JSONValue.parse(drpcResult);
		return (T) values.get(0).get(index);
	}
}