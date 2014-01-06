package stormy.pythian.service.topology;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import stormy.pythian.core.configuration.PythianToplogyConfiguration;

@Service
public class TopologyService {

	private Map<String, PythianToplogyConfiguration> dao = new HashMap<>();

	public void save(PythianToplogyConfiguration configuration) {
		checkNotNull(configuration, "topology's is mandatory");
		configuration.ensureId();

		dao.put(configuration.getId(), configuration);
	}

	public void delete(String configurationId) {
		checkNotNull(configurationId, "topology's id is mandatory");

		dao.remove(configurationId);
	}

	public Collection<PythianToplogyConfiguration> findAll() {
		return dao.values();
	}

	public PythianToplogyConfiguration findById(String configurationId) {
		return dao.get(configurationId);
	}
}
