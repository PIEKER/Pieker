package pieker.web.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pieker.web.server.dbo.Scenario;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {
}
