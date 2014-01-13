package stormy.pythian.core.configuration;

public abstract class StateConfiguration {

	protected final String name;
	protected final StateBackend backend;
	protected final TransactionType transactionType;

	public StateConfiguration(String name, TransactionType transactionType, StateBackend backend) {
		this.name = name;
		this.transactionType = transactionType;
		this.backend = backend;
	}

	public String getName() {
		return name;
	}

	public StateBackend getBackend() {
		return backend;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((backend == null) ? 0 : backend.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
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
		StateConfiguration other = (StateConfiguration) obj;
		if (backend != other.backend)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (transactionType != other.transactionType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StateConfiguration [name=" + name + ", backend=" + backend + ", transactionType=" + transactionType + "]";
	}

	public static enum StateBackend {
		IN_MEMORY;
	}

	public static enum TransactionType {
		TRANSACTIONAL, NONE, OPAQUE;
	}
}
