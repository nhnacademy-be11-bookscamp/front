package store.bookscamp.front.member.controller;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.member.controller.request.MemberCreateRequest;
import store.bookscamp.front.member.controller.request.MemberPasswordUpdateRequest;
import store.bookscamp.front.member.controller.request.MemberUpdateRequest;
import store.bookscamp.front.member.controller.response.MemberGetResponse;


@FeignClient(name = "memberApiClient", url = "${gateway.base-url}")
public interface MemberFeignClient {

    @GetMapping("/api-server/member/{id}")
    MemberGetResponse getMember(@PathVariable("id") String id);

    @GetMapping("/api-server/member/check-id")
    ResponseEntity<String> checkIdDuplicate(@RequestParam("id") String id);

    @PostMapping("/api-server/member")
    ResponseEntity<Void> createMember(@RequestBody MemberCreateRequest memberCreateRequest);


    @PutMapping("/api-server/member/{id}")
    ResponseEntity<MemberGetResponse> updateMember(
            @PathVariable("id") String id,
            @RequestBody MemberUpdateRequest memberUpdateRequest);

    @PutMapping("/api-server/member/{id}/change-password")
    ResponseEntity<Void> updatePassword(
            @PathVariable("id") String id,
            @RequestBody MemberPasswordUpdateRequest memberPasswordUpdateRequest);

    @DeleteMapping("/api-server/member/{id}")
    ResponseEntity<Void> deleteMember(@PathVariable("id") String id);
}