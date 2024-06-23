package finalproject.ss3v2.web;

import finalproject.ss3v2.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app-testing")
public class TestingController {

    @GetMapping("")
    public String redirectToUserSession() {
        return "app-testing";

    }
}
