package store.bookscamp.front.member.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import store.bookscamp.front.member.controller.request.MemberCreateRequest;
import store.bookscamp.front.member.controller.request.MemberPasswordUpdateRequest;
import store.bookscamp.front.member.controller.request.MemberUpdateRequest;
import store.bookscamp.front.member.controller.response.MemberGetResponse;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberFeignClient memberFeignClient;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.api.prefix}")
    private String apiPrefix;

    @GetMapping("/sign-up")
    public String showPage(Model model){
        model.addAttribute("apiPrefix", apiPrefix);
        return "member/signup-form";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("apiPrefix", apiPrefix);
        return "member/login";
    }

    @GetMapping("/members/edit-info/{id}")
    public ModelAndView editInfo(@PathVariable String id) {
        MemberGetResponse memberInfo = memberFeignClient.getMember(id);
        ModelAndView mav = new ModelAndView("/member/edit-info");
        mav.addObject("memberInfo",memberInfo);
        return mav;
    }

    @GetMapping("/members/{id}/change-password")
    public ModelAndView changePassword(@PathVariable String id){
        MemberGetResponse memberInfo = memberFeignClient.getMember(id);
        ModelAndView mav = new ModelAndView("/member/change-password");
        mav.addObject("memberInfo",memberInfo);
        return mav;
    }

    @GetMapping("/members/{id}")
    public ModelAndView getMember(@PathVariable String id){
        MemberGetResponse memberInfo = memberFeignClient.getMember(id);
        ModelAndView modelAndView = new ModelAndView("/member/mypage");
        modelAndView.addObject("memberInfo",memberInfo);
        return modelAndView;
    }

    @GetMapping("/members/check-id")
    public String checkId(@RequestParam String id){
        String response = String.valueOf(memberFeignClient.checkIdDuplicate(id));
        return response;
    }

    @PostMapping("/members")
    public String createMember(MemberCreateRequest memberCreateRequest, Model model){
        try{
            String rawPassword = memberCreateRequest.password();

            String encodedPassword = passwordEncoder.encode(rawPassword);

            MemberCreateRequest encodedRequest = new MemberCreateRequest(
                    memberCreateRequest.username(),
                    encodedPassword,
                    memberCreateRequest.name(),
                    memberCreateRequest.email(),
                    memberCreateRequest.phone(),
                    memberCreateRequest.birthDate()
            );

            memberFeignClient.createMember(encodedRequest);

            return "member/login";
        } catch(FeignException e){
            String errorMessage = "회원가입 중 알 수 없는 서버 오류가 발생했습니다. (" + e.status() + ")";
            try {
                errorMessage = e.contentUTF8();

            } catch (Exception ex) {
            }

            model.addAttribute("errorMessage", errorMessage);

            model.addAttribute("memberCreateRequest", memberCreateRequest);


            return "member/signup-form";
        }
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<Void> updateMember(@PathVariable String id,@RequestBody MemberUpdateRequest request){
        memberFeignClient.updateMember(id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/members/{id}/change-password")
    public ResponseEntity<Void> updatePassword(@PathVariable String id,@RequestBody MemberPasswordUpdateRequest request){
        memberFeignClient.updatePassword(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("members/{id}")
    public String deleteMember(@PathVariable String id){
        memberFeignClient.deleteMember(id);
        return "member/login";
    }





}
