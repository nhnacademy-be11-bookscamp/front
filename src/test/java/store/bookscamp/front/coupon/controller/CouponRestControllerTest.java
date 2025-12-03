package store.bookscamp.front.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.coupon.controller.request.CouponCreateRequest;
import store.bookscamp.front.coupon.feign.CouponFeignClient;
import store.bookscamp.front.coupon.type.DiscountType;
import store.bookscamp.front.coupon.type.TargetType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CouponRestControllerTest {

    MockMvc mvc;

    @Mock
    CouponFeignClient couponFeignClient;

    @InjectMocks
    CouponRestController couponRestController;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(couponRestController)
                .build();
    }

    @Test
    @DisplayName("[POST] 쿠폰 생성 요청 성공")
    void createCoupon_success() throws Exception {
        // given
        // Record 생성자 순서: targetType, targetId, discountType, discountValue, minOrderAmount, maxDiscountAmount, validDays, name
        // 주의: TargetType.BOOK, DiscountType.AMOUNT 등은 실제 프로젝트의 Enum 상수로 변경해주세요.
        CouponCreateRequest request = new CouponCreateRequest(
                TargetType.BOOK,    // targetType (임시 상수)
                1L,                 // targetId
                DiscountType.AMOUNT,// discountType (임시 상수)
                1000,               // discountValue
                5000,               // minOrderAmount
                2000,               // maxDiscountAmount (nullable)
                30,                 // validDays (nullable)
                "신규 도서 할인 쿠폰" // name
        );

        // when
        mvc.perform(post("/api-server/admin/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // then
        verify(couponFeignClient).createCoupon(any(CouponCreateRequest.class));
    }

    @Test
    @DisplayName("[POST] 쿠폰 삭제 요청 성공")
    void deleteCoupon_success() throws Exception {
        Long couponId = 1L;

        // Controller 매핑이 @PostMapping이므로 post() 사용
        mvc.perform(post("/api-server/admin/coupons/{couponId}", couponId))
                .andExpect(status().isOk());

        verify(couponFeignClient).deleteCoupon(couponId);
    }
}