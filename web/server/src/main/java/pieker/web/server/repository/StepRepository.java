package pieker.web.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pieker.web.server.dbo.Step;

import java.util.Optional;

public interface StepRepository extends JpaRepository<Step, Long> {

    Optional<Step> findByIdentifier(String identifier);
}
