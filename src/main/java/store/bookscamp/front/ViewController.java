package store.bookscamp.front;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/layout")
    public String layout() {
        return "layout";
    }

    @GetMapping("/template-no-sidebar")
    public String templateNoSidebar() {
        return "template-no-siderbar";
    }

    @GetMapping("/image")
    public String image() { return "image"; }
}