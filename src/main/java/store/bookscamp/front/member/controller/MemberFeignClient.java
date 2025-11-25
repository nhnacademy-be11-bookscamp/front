package store.bookscamp.front.member.controller;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.member.controller.request.MemberCreateRequest;
import store.bookscamp.front.member.controller.request.MemberPasswordUpdateRequest;
import store.bookscamp.front.member.controller.request.MemberStatusUpdateRequest;
import store.bookscamp.front.member.controller.request.MemberUpdateRequest;
import store.bookscamp.front.member.controller.response.MemberGetResponse;
import store.bookscamp.front.member.controller.response.MemberPageResponse;


@FeignClient(name = "memberApiClient", url = "${gateway.base-url}")
public interface MemberFeignClient {

    @GetMapping("/api-server/member")
    MemberGetResponse getMember();

    @GetMapping("/api-server/member/check-id")
    ResponseEntity<String> checkIdDuplicate(@RequestParam("id") String id);

    @PostMapping("/api-server/member/sign-up")
    ResponseEntity<Void> createMember(@RequestBody MemberCreateRequest memberCreateRequest);

    @PutMapping("/api-server/member")
    ResponseEntity<MemberGetResponse> updateMember(
            @RequestBody MemberUpdateRequest memberUpdateRequest);

    @PutMapping("/api-server/member/change-password")
    ResponseEntity<Void> updatePassword(
            @RequestBody MemberPasswordUpdateRequest memberPasswordUpdateRequest);

    @DeleteMapping("/api-server/member")
    ResponseEntity<Void> deleteMember();

    @GetMapping("/api-server/admin/member")
    ResponseEntity<RestPageImpl<MemberPageResponse>> getAllMembers(@PageableDefault(size = 10, page = 0) Pageable pageable);

    @PutMapping("/api-server/admin/member/updateStatus")
    ResponseEntity<Void> updateMemberStatus(@RequestBody MemberStatusUpdateRequest request);
}