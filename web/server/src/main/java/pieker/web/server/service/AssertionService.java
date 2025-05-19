package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.repository.AssertionRepository;

@Service
public class AssertionService {

    private AssertionRepository assertionRepository;

    @Autowired
    private AssertionService(AssertionRepository assertionRepository){
        this.assertionRepository = assertionRepository;
    }

}
