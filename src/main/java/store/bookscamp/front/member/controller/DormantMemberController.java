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

    /**
     * [페이지] 휴면 해제 화면 보여주기
     * /dormant?username=... 형태로 진입
     */
    @GetMapping
    public String dormantPage() {
        return "member/dormant"; // dormant.html 뷰 반환
    }

    /**
     * [동작 1] 인증번호 발송 요청
     * HTML Form: action="/dormant/send" method="post"
     */
    @PostMapping("/send")
    public String sendDormantCode(@RequestParam("username") String username,
                                  RedirectAttributes redirectAttributes) {
        try {
            // 1. FeignClient 요청을 위한 Map 생성
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put(USER_NAME, username);

            // 2. Auth Server로 요청 전송
            authFeignClient.sendDormantCode(requestMap);

            // 3. 성공 시: 다시 dormant 페이지로 가되, 'sent=true' 파라미터를 붙임
            redirectAttributes.addAttribute(USER_NAME, username);
            redirectAttributes.addAttribute("sent", "true"); // 이 파라미터가 있으면 입력창이 보임

            return REDIRECTION_DORMANT;

        } catch (Exception e) {
            // 실패 시 (Auth서버 에러 등): 에러 메시지와 함께 리다이렉트
            log.error("인증번호 발송 실패: {}", e.getMessage());

            redirectAttributes.addAttribute(USER_NAME, username);
            redirectAttributes.addAttribute("error", "send_failed");

            return REDIRECTION_DORMANT;
        }
    }

    /**
     * [동작 2] 인증번호 검증 요청
     * HTML Form: action="/dormant/verify" method="post"
     */
    @PostMapping("/verify")
    public String verifyDormantCode(@RequestParam("username") String username,
                                    @RequestParam("code") String code,
                                    RedirectAttributes redirectAttributes) {
        try {
            // 1. FeignClient 요청 Map 생성
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put(USER_NAME, username);
            requestMap.put("code", code);

            // 2. Auth Server로 검증 요청
            authFeignClient.verifyDormantCode(requestMap);

            // 3. 성공 시: 로그인 페이지로 이동 (메시지 등 추가 가능)
            redirectAttributes.addAttribute("logout", true); // "로그아웃되었습니다" 대신 다른 메시지를 띄우고 싶으면 수정
            return "redirect:/login";

        } catch (Exception e) {
            // 4. 실패 시 (인증번호 틀림 등): 다시 입력 화면으로 돌려보냄
            log.error("휴면 해제 검증 실패: {}", e.getMessage());

            redirectAttributes.addAttribute(USER_NAME, username);
            redirectAttributes.addAttribute("sent", "true"); // 입력창 유지
            redirectAttributes.addAttribute("error", "invalid"); // "인증번호가 틀렸습니다" 표시용

            return REDIRECTION_DORMANT;
        }
    }
}