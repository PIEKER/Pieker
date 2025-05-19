package pieker.web.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pieker.web.server.dbo.Run;

public interface RunRepository extends JpaRepository<Run, Long> {
}
