package pieker.web.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pieker.web.server.dbo.Step;

public interface StepRepository extends JpaRepository<Step, Long> {
}
