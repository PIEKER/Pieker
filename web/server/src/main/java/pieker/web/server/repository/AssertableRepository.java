package pieker.web.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pieker.web.server.dbo.Assertable;

public interface AssertableRepository extends JpaRepository<Assertable, Long> {
}
