package finalproject.ss3v2.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {
// THIS CONTROLLER IS FOR TESTING PURPOSES

    @GetMapping("error")
    public String getErrorMessage () {
        return "error";
    }

    @GetMapping("userExists")
    public String getUserExistsMessage (Model model) {
        model.addAttribute("userExists",true);
        return "error";
    }

}
