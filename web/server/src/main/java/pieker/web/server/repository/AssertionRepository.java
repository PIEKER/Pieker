package pieker.web.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pieker.web.server.dbo.Assertion;

public interface AssertionRepository extends JpaRepository<Assertion, Long> {
}
