package store.bookscamp.front.order.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import store.bookscamp.front.address.controller.response.AddressListResponse;
import store.bookscamp.front.address.feign.AddressFeignClient;
import store.bookscamp.front.member.controller.MemberFeignClient;
import store.bookscamp.front.member.controller.response.MemberGetResponse;
import store.bookscamp.front.order.dto.OrderCreateRequest;
import store.bookscamp.front.order.dto.OrderCreateResponse;
import store.bookscamp.front.order.dto.OrderDetailResponse;
import store.bookscamp.front.order.dto.OrderListResponse;
import store.bookscamp.front.order.dto.OrderPrepareRequest;
import store.bookscamp.front.order.dto.OrderPrepareResponse;
import store.bookscamp.front.order.dto.PageResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFeignClient orderFeignClient;
    private final AddressFeignClient addressFeignClient;
    private final MemberFeignClient memberFeignClient;

    @PostMapping("/prepare")
    public String prepareOrder(
            @RequestBody OrderPrepareRequest prepareRequest,
            HttpServletRequest request,
            Model model
    ) {
        log.info("=== 주문 준비 요청 시작 ===");
        log.info("요청 데이터: {}", prepareRequest);

        boolean isMember = isAuthenticatedMember(request);

        ResponseEntity<OrderPrepareResponse> response = orderFeignClient.prepareOrder(prepareRequest);
        OrderPrepareResponse orderData = response.getBody();

        log.info("주문 준비 응답 상태: {}", response.getStatusCode());
        log.info("주문 준비 응답 데이터: {}", orderData);
        log.info("=== 주문 준비 요청 완료 ===");

        model.addAttribute("orderData", orderData);
        model.addAttribute("isMember", isMember);

        if (isMember) {
            try {
                MemberGetResponse memberInfo = memberFeignClient.getMember();
                String username = memberInfo.username();
                
                ResponseEntity<AddressListResponse> addressResponse = addressFeignClient.getAddresses(username);
                AddressListResponse addressList = addressResponse.getBody();
                
                List<AddressListResponse.AddressResponse> sortedAddresses = addressList != null && addressList.addresses() != null
                    ? addressList.addresses().stream()
                        .sorted(Comparator.comparing(AddressListResponse.AddressResponse::isDefault).reversed())
                        .collect(Collectors.toList())
                    : List.of();
                
                model.addAttribute("addresses", sortedAddresses);
                model.addAttribute("username", username);
            } catch (Exception e) {
                model.addAttribute("addresses", List.of());
                model.addAttribute("username", null);
            }
        } else {
            model.addAttribute("addresses", List.of());
            model.addAttribute("username", null);
        }

        return "order/order-prepare";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<OrderCreateResponse> createOrder(
            @RequestBody OrderCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("=== 주문 생성 요청 시작 ===");
        log.info("요청 데이터: {}", request);

        boolean isMember = isAuthenticatedMember(httpRequest);

        if (!isMember && request.nonMemberInfo() == null) {
            log.error("주문 생성 실패: 비회원 정보 누락");
            throw new IllegalArgumentException("비회원 정보는 필수입니다.");
        }

        if (isMember && request.nonMemberInfo() != null) {
            log.error("주문 생성 실패: 회원이 비회원 정보 입력");
            throw new IllegalArgumentException("회원은 비회원 정보를 입력할 수 없습니다.");
        }

        ResponseEntity<OrderCreateResponse> response = orderFeignClient.createOrder(request);

        log.info("주문 생성 응답 상태: {}", response.getStatusCode());
        log.info("주문 생성 응답 데이터: {}", response.getBody());
        log.info("=== 주문 생성 요청 완료 ===");

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    private boolean isAuthenticatedMember(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        return Arrays.stream(cookies)
                .anyMatch(cookie -> "Authorization".equals(cookie.getName())
                        && cookie.getValue() != null
                        && !cookie.getValue().isEmpty());
    }

    /**
     * 주문 내역 조회
     */
    @GetMapping("/list")
    public String getOrderList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model
    ) {
        ResponseEntity<PageResponse<OrderListResponse>> response =
                orderFeignClient.getOrderList(page, size);

        PageResponse<OrderListResponse> orderPage = response.getBody();

        List<OrderListResponse> orders =
                (orderPage != null) ? orderPage.content() : List.of();

        model.addAttribute("orderPage", orderPage); // 페이징 정보 전체
        model.addAttribute("orders", orders);       // 실제 주문 리스트
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "order/order-list";
    }

    /**
     * 각각의 주문 내역 상세 조회
     */
    @GetMapping("/{orderId}")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {
        ResponseEntity<OrderDetailResponse> response = orderFeignClient.getOrderDetail(orderId);
        model.addAttribute("order", response.getBody());
        return "order/order-detail";
    }
}