package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.dbo.Run;
import pieker.web.server.repository.RunRepository;

import java.util.List;

@Service
public class RunService {

    private RunRepository runRepository;

    @Autowired
    public RunService(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public List<Run> getRuns() {
        return runRepository.findAll();
    }

    public Run getRun(Long id) {
        return runRepository.findById(id).orElse(null);
    }
}
