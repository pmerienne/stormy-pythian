package stormy.pythian.service.topology;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stormy.pythian.core.configuration.PythianToplogyConfiguration;

@Service
public class TopologyService {

    @Autowired
    private TopologyRepository repository;

    public PythianToplogyConfiguration save(PythianToplogyConfiguration topology) {
        checkNotNull(topology, "topology is mandatory");
        topology.ensureId();

        repository.save(topology);
        return topology;
    }

    public void delete(String topologyId) {
        checkNotNull(topologyId, "topology's id is mandatory");
        repository.delete(topologyId);
    }

    public Collection<PythianToplogyConfiguration> findAll() {
        return repository.findAll();
    }

    public PythianToplogyConfiguration findById(String topologyId) {
        checkNotNull(topologyId, "topology's id is mandatory");
        return repository.findById(topologyId);
    }

}
