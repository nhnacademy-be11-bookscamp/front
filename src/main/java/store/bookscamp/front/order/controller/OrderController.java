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
import store.bookscamp.front.address.controller.response.AddressListResponse;
import store.bookscamp.front.address.feign.AddressFeignClient;
import store.bookscamp.front.member.controller.MemberFeignClient;
import store.bookscamp.front.member.controller.response.MemberGetResponse;
import store.bookscamp.front.order.dto.NonMemberOrderRequest;
import store.bookscamp.front.order.dto.OrderDetailResponse;
import store.bookscamp.front.order.dto.OrderListResponse;
import store.bookscamp.front.order.dto.OrderPrepareRequest;
import store.bookscamp.front.order.dto.OrderPrepareResponse;
import store.bookscamp.front.order.dto.PageResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final String ADDRESSES = "addresses";
    private static final String USER_NAME = "username";

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
        model.addAttribute("orderType", prepareRequest.orderType());

        if (isMember) {
            try {
                MemberGetResponse memberInfo = memberFeignClient.getMember();
                String username = memberInfo.username();
                
                ResponseEntity<AddressListResponse> addressResponse = addressFeignClient.getAddresses();
                AddressListResponse addressList = addressResponse.getBody();
                
                List<AddressListResponse.AddressResponse> sortedAddresses = addressList != null && addressList.addresses() != null
                    ? addressList.addresses().stream()
                        .sorted(Comparator.comparing(AddressListResponse.AddressResponse::isDefault).reversed())
                        .toList()
                    : List.of();
                
                model.addAttribute(ADDRESSES, sortedAddresses);
                model.addAttribute(USER_NAME, username);
            } catch (Exception e) {
                model.addAttribute(ADDRESSES, List.of());
                model.addAttribute(USER_NAME, null);
            }
        } else {
            model.addAttribute(ADDRESSES, List.of());
            model.addAttribute(USER_NAME, null);
        }

        return "order/order-prepare";
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

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("orders", orders);
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

    /**
     * 비회원 주문 내역 상세 조회
     * 주문번호, 비밀번호
     */
    @PostMapping("/non-member/detail")
    public String getNonMemberDetail(
            @RequestParam("orderNumber") String orderNumber,
            @RequestParam("password") String password,
            Model model
    ) {
        log.info("====테스트!! : 비회원 주문 상세 조회 요청 시작===");
        log.info("주문번호 : {}", orderNumber);

        NonMemberOrderRequest request = new NonMemberOrderRequest(password);

        ResponseEntity<OrderDetailResponse> response = orderFeignClient.getNonMemberOrderDetail(orderNumber, request);

        OrderDetailResponse orderDetail = response.getBody();

        log.info("비회원 주문 상세 조회 응답 상태: {}", response.getStatusCode());
        log.info("비회원 주문 상세 조회 응답 데이터: {}", orderDetail);
        log.info("=== 비회원 주문 상세 조회 요청 완료 ===");

        model.addAttribute("order", orderDetail);
        model.addAttribute("isMember", false);

        return "order/non-member-detail";
    }

}