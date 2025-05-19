package pieker.web.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pieker.web.server.dbo.Run;
import pieker.web.server.dbo.Scenario;

import java.util.List;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

}
