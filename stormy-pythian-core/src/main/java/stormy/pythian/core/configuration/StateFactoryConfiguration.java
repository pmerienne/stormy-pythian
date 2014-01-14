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
package stormy.pythian.core.configuration;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

public abstract class StateFactoryConfiguration {

	protected final String id;

	protected final TransactionType transactionType;
	protected final StateBackend backend;

	public StateFactoryConfiguration(String id, TransactionType transactionType, StateBackend backend) {
		this.id = id;
		this.transactionType = transactionType;
		this.backend = backend;
	}

	public StateFactoryConfiguration(TransactionType transactionType, StateBackend backend) {
		this.id = randomAlphabetic(6);
		this.transactionType = transactionType;
		this.backend = backend;
	}

	public String getId() {
		return id;
	}

	public StateBackend getBackend() {
		return backend;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((backend == null) ? 0 : backend.hashCode());
		result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateFactoryConfiguration other = (StateFactoryConfiguration) obj;
		if (backend != other.backend)
			return false;
		if (transactionType != other.transactionType)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StateFactoryConfiguration [id=" + id + ", transactionType=" + transactionType + ", backend=" + backend + "]";
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public static enum StateBackend {
		IN_MEMORY;
	}

	public static enum TransactionType {
		TRANSACTIONAL, NONE, OPAQUE;
	}
}