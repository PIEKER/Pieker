package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.dto.RunDto;
import pieker.web.server.repository.RunRepository;

import java.util.List;

@Service
public class RunService {

    private RunRepository runRepository;

    @Autowired
    public RunService(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    public List<RunDto> getRuns() {
        return runRepository.findAll().stream().map(RunDto::toRunDto).toList();
    }

    public RunDto getRun(Long id) {
        return RunDto.toRunDto(runRepository.findById(id).orElse(null));
    }
}
