package bootcamp.kakao.community.platform.terms.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/policy")
public class PolicyController {

    @Value("${cors.front.host}")
    private String frontendUrl;

    /// 약관에 대한 내용
    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("companyName", "개발자커뮤니티");
        model.addAttribute("frontendUrl", frontendUrl);
        return "policy/terms";
    }

    /// 개인정보에 대한 내용
    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("companyName", "개발자커뮤니티");
        model.addAttribute("frontendUrl", frontendUrl);
        return "policy/privacy";
    }
}
