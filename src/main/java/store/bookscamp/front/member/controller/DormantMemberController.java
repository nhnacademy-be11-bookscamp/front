package store.bookscamp.front.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import store.bookscamp.front.auth.repository.AuthFeignClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/dormant")
@Controller
public class DormantMemberController {

    private static final String REDIRECTION_DORMANT = "redirect:/dormant";
    private static final String USER_NAME = "username";

    private final AuthFeignClient authFeignClient;

    @GetMapping
    public String dormantPage() {
        return "member/dormant"; // dormant.html 뷰 반환
    }

    @PostMapping("/send")
    public String sendDormantCode(@RequestParam("username") String username,
                                  RedirectAttributes redirectAttributes) {
        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put(USER_NAME, username);

            authFeignClient.sendDormantCode(requestMap);

            redirectAttributes.addAttribute(USER_NAME, username);
            redirectAttributes.addAttribute("sent", "true");

            return REDIRECTION_DORMANT;

        } catch (Exception e) {
            log.error("인증번호 발송 실패: {}", e.getMessage());

            redirectAttributes.addAttribute(USER_NAME, username);
            redirectAttributes.addAttribute("error", "send_failed");

            return REDIRECTION_DORMANT;
        }
    }

    @PostMapping("/verify")
    public String verifyDormantCode(@RequestParam("username") String username,
                                    @RequestParam("code") String code,
                                    RedirectAttributes redirectAttributes) {
        try {
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put(USER_NAME, username);
            requestMap.put("code", code);

            authFeignClient.verifyDormantCode(requestMap);

            redirectAttributes.addAttribute("logout", true);
            return "redirect:/login";

        } catch (Exception e) {
            log.error("휴면 해제 검증 실패: {}", e.getMessage());

            redirectAttributes.addAttribute(USER_NAME, username);
            redirectAttributes.addAttribute("sent", "true");
            redirectAttributes.addAttribute("error", "invalid");

            return REDIRECTION_DORMANT;
        }
    }
}