package pieker.web.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pieker.web.server.repository.AssertableRepository;

@Service
public class AssertableService {

    private AssertableRepository assertableRepository;

    @Autowired
    private AssertableService(AssertableRepository assertableRepository){
        this.assertableRepository = assertableRepository;
    }

}
