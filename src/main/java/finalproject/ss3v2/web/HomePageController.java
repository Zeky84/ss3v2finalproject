package finalproject.ss3v2.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/homepage")
public class HomePageController {

    @GetMapping("")
    public String getHomePage() {
        return "homepage";
    }
}
