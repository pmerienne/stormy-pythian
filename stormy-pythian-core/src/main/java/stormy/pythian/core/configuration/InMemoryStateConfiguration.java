package stormy.pythian.core.configuration;

public class InMemoryStateConfiguration extends StateConfiguration {

	public InMemoryStateConfiguration(String name, TransactionType transactionType) {
		super(name, transactionType, StateBackend.IN_MEMORY);
	}

}
