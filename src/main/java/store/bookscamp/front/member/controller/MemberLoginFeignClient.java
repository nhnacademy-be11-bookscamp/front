//package store.bookscamp.front.member.controller;
//
//
//import jakarta.validation.Valid;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import store.bookscamp.front.member.controller.request.MemberLoginRequest;
//
//@FeignClient(name = "memberLoginClient", url = "${auth.base-url")
//public interface MemberLoginFeignClient {
//
//    @PostMapping("/auth-server/login")
//    public ResponseEntity<Void> doLogin(@Valid @RequestBody MemberLoginRequest memberLoginRequest);
//
//}
