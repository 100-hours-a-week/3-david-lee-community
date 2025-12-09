package bootcamp.kakao.community.platform;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actuator")
public class HomeController {

    @GetMapping("/health")
    public String healthCheck() {
        return "Health Check";
    }

}
