package store.bookscamp.front.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error/forbidden")
    public String forbiddenPage(HttpServletRequest request, Model model) {
        String errorCode = (String) request.getAttribute("errorCode");
        String errorMessage = (String) request.getAttribute("errorMessage");
        model.addAttribute("errorCode",errorCode);
        model.addAttribute("errorMessage", errorMessage);
        return "error/error";
    }
}
