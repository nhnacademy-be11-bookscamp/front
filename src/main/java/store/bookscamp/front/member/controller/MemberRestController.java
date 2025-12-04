package store.bookscamp.front.member.controller;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.member.controller.request.MemberPasswordUpdateRequest;
import store.bookscamp.front.member.controller.request.MemberStatusUpdateRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberFeignClient memberFeignClient;

    @GetMapping("/members/check-id")
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

    @PostMapping("/admin/members/{memberId}/status")
    public ResponseEntity<Void> updateMemberStatus(@PathVariable Long memberId, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        MemberStatusUpdateRequest request = new MemberStatusUpdateRequest(memberId, newStatus);
        memberFeignClient.updateMemberStatus(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/members/delete")
    public ResponseEntity<Void> deleteMember(HttpServletRequest request,
                                             HttpServletResponse response){
        memberFeignClient.deleteMember();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/members/change-password")
    public ResponseEntity<Void> updatePassword(@RequestBody MemberPasswordUpdateRequest request){
        memberFeignClient.updatePassword(request);
        return ResponseEntity.ok().build();
    }
}
