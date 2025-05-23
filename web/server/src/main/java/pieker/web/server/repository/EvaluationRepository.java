package pieker.web.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pieker.web.server.dbo.Evaluation;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

}
