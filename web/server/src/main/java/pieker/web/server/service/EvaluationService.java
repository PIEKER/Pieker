package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.dbo.Assertion;
import pieker.web.server.dbo.Evaluation;
import pieker.web.server.dbo.Run;
import pieker.web.server.dto.EvaluationDto;
import pieker.web.server.repository.EvaluationRepository;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;

    @Autowired
    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    public Evaluation createEvaluation(EvaluationDto evaluationDto, Run run, Assertion assertion){
        return this.evaluationRepository.save(
                Evaluation.builder()
                        .run(run)
                        .assertion(assertion)
                        .success(evaluationDto.isSuccess())
                        .errorMessage(evaluationDto.getErrorMessage())
                        .build()
        );
    }
}
