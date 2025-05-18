package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import pieker.web.server.repository.AssertionRepository;

public class AssertionService {

    private AssertionRepository assertionRepository;

    @Autowired
    private AssertionService(AssertionRepository assertionRepository){
        this.assertionRepository = assertionRepository;
    }

}
