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
package stormy.pythian.core.topology;

import java.util.HashMap;
import java.util.Map;

import storm.trident.state.State;
import storm.trident.state.StateFactory;
import storm.trident.testing.MemoryMapState;
import stormy.pythian.core.configuration.InMemoryStateConfiguration;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;
import stormy.pythian.core.configuration.PythianStateConfiguration;
import backtype.storm.task.IMetricsContext;

public class PythianStateFactory {

	public Map<String, StateFactory> createStateFactories(PythianToplogyConfiguration topologyConfiguration) {
		Map<String, StateFactory> stateFactoriesByIds = new HashMap<>();

		for (PythianStateConfiguration stateConfiguration : topologyConfiguration.getStates()) {
			StateFactory stateFactory = this.createStateFactory(stateConfiguration);
			stateFactoriesByIds.put(stateConfiguration.getId(), stateFactory);
		}

		return stateFactoriesByIds;
	}

	protected StateFactory createStateFactory(PythianStateConfiguration configuration) {
		if (configuration instanceof InMemoryStateConfiguration) {
			return createInMemoryStateFactory((InMemoryStateConfiguration) configuration);
		} else {
			throw new UnsupportedOperationException("Can not create state factory for backend : " + configuration.getBackend());
		}
	}

	private StateFactory createInMemoryStateFactory(InMemoryStateConfiguration configuration) {
		StateFactory stateFactory = null;
		switch (configuration.getTransactionType()) {
		case NONE:
			stateFactory = new NoneTransactionalInMemoryStateFactory(configuration.getId());
			break;
		case OPAQUE:
			stateFactory = new OpaqueTransactionalInMemoryStateFactory(configuration.getId());
			break;
		case TRANSACTIONAL:
			stateFactory = new TransactionalInMemoryStateFactory(configuration.getId());
			break;
		default:
			throw new UnsupportedOperationException("Can not create in memory state factory for transaction type : " + configuration.getTransactionType());
		}

		return stateFactory;
	}

	public abstract static class InMemoryStateFactory implements StateFactory {

		private static final long serialVersionUID = -3589505813476300002L;

		private final String uuid;

		public InMemoryStateFactory(String uuid) {
			this.uuid = uuid;
		}

		public String getUuid() {
			return uuid;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public State makeState(Map conf, IMetricsContext metrics, int partitionIndex, int numPartitions) {
			return createState(uuid);
		}

		protected abstract State createState(String uuid);

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
			InMemoryStateFactory other = (InMemoryStateFactory) obj;
			if (uuid == null) {
				if (other.uuid != null)
					return false;
			} else if (!uuid.equals(other.uuid))
				return false;
			return true;
		}

	}

	public static class NoneTransactionalInMemoryStateFactory extends InMemoryStateFactory {

		public NoneTransactionalInMemoryStateFactory(String uuid) {
			super(uuid);
		}

		private static final long serialVersionUID = 7032563580553707943L;

		@SuppressWarnings("rawtypes")
		@Override
		protected State createState(String uuid) {
			return new MemoryMapState(uuid);
		}
	}

	public static class OpaqueTransactionalInMemoryStateFactory extends InMemoryStateFactory {

		private static final long serialVersionUID = -6433397890576750143L;

		public OpaqueTransactionalInMemoryStateFactory(String uuid) {
			super(uuid);
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected State createState(String uuid) {
			return new MemoryMapState(uuid);
		}
	}

	public static class TransactionalInMemoryStateFactory extends InMemoryStateFactory {

		private static final long serialVersionUID = -6512983913668194068L;

		public TransactionalInMemoryStateFactory(String uuid) {
			super(uuid);
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected State createState(String uuid) {
			return new MemoryMapState(uuid);
		}
	}
}
