package store.bookscamp.front.member.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import store.bookscamp.front.member.controller.request.MemberCreateRequest;
import store.bookscamp.front.member.controller.request.MemberPasswordUpdateRequest;
import store.bookscamp.front.member.controller.request.MemberUpdateRequest;
import store.bookscamp.front.member.controller.response.MemberGetResponse;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberFeignClient memberFeignClient;
    private final PasswordEncoder passwordEncoder;


    @GetMapping("/signup")
    public String showPage(Authentication authentication){
        if(authentication !=null && authentication.isAuthenticated()){
            return "redirect:/";
        }
        return "member/signup";
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "member/login";
    }

    @GetMapping("/mypage/edit-info")
    public ModelAndView editInfo() {
        MemberGetResponse memberInfo = memberFeignClient.getMember();
        ModelAndView mav = new ModelAndView("member/edit-info");
        mav.addObject("memberInfo",memberInfo);
        return mav;
    }

    @GetMapping("/mypage/change-password")
    public ModelAndView changePassword(){
        MemberGetResponse memberInfo = memberFeignClient.getMember();
        ModelAndView mav = new ModelAndView("member/change-password");
        mav.addObject("memberInfo",memberInfo);
        return mav;
    }

    @GetMapping("/mypage")
    public ModelAndView getMember(){
        MemberGetResponse memberInfo = memberFeignClient.getMember();
        ModelAndView modelAndView = new ModelAndView("member/mypage");
        modelAndView.addObject("memberInfo",memberInfo);
        return modelAndView;
    }

    @GetMapping("/members/check-id")
    @ResponseBody
    public ResponseEntity<String> checkId(@RequestParam("id") String id) {
        try {

            return memberFeignClient.checkIdDuplicate(id);

        } catch (FeignException e) {
            return ResponseEntity
                    .status(e.status())
                    .body(e.contentUTF8());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/members")
    public String createMember(MemberCreateRequest memberCreateRequest, RedirectAttributes redirectAttributes){
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

            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            return "redirect:/signup";
        }
    }

    @PutMapping("/members/update-info")
    public String updateMemberByForm(@ModelAttribute MemberUpdateRequest request, Model model) {
        try {
            memberFeignClient.updateMember(request);
            return "redirect:/mypage";
        } catch (FeignException e) {
            String errorMessage = "회원 정보 수정 중 오류가 발생했습니다. 다시 시도해 주세요.";
            model.addAttribute("errorMessage", errorMessage);
            return "redirect:/mypage/edit-info";
        }
    }

    @PutMapping("/members/change-password")
    public ResponseEntity<Void> updatePassword(@PathVariable String id,@RequestBody MemberPasswordUpdateRequest request){
        memberFeignClient.updatePassword(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/member")
    public String deleteMember(){
        memberFeignClient.deleteMember();
        return "member/login";
    }
}