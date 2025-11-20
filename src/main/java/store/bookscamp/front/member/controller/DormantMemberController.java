package store.bookscamp.front.member.controller;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.auth.repository.AuthFeignClient;

@RestController
@RequiredArgsConstructor
public class DormantMemberController {
    private final AuthFeignClient authFeignClient;

    @PostMapping("/dormant/send")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> request){
        return authFeignClient.sendDormantCode(request);
    }

    @PostMapping("/dormant/verify")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        return authFeignClient.verifyDormantCode(request);
    }
}
