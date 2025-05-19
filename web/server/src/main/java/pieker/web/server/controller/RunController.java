package pieker.web.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pieker.web.server.dto.RunDto;
import pieker.web.server.service.RunService;

import java.util.List;


@CrossOrigin(origins = "http://localhost:5173") // allow frontend origin
@RestController
@RequestMapping("/runs")
public class RunController {

    private final RunService runService;

    @Autowired
    public RunController(RunService runService) {
        this.runService = runService;
    }

    @RequestMapping("/all")
    public List<RunDto> getRuns(){
        return this.runService.getRuns();
    }

    @RequestMapping("/{id}")
    public RunDto getRun(@PathVariable Long id){
        return this.runService.getRun(id);
    }
}
