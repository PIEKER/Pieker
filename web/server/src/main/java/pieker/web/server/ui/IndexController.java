package pieker.web.server.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
public class IndexController {

    @GetMapping(value = "/{path:[^\\.]*}")
    public String forward(@PathVariable String path) {
        log.debug("forwarding path: {}", path);
        return "forward:/index.html";
    }
}
