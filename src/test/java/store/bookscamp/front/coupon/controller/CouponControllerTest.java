package store.bookscamp.front.coupon.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.coupon.controller.response.CouponResponse;
import store.bookscamp.front.coupon.feign.CouponFeignClient;
import store.bookscamp.front.coupon.type.DiscountType;
import store.bookscamp.front.coupon.type.TargetType;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CouponControllerTest {

    MockMvc mvc;

    @Mock
    CouponFeignClient couponFeignClient;

    @InjectMocks
    CouponController couponController;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(couponController)
                .build();
    }

    @Test
    @DisplayName("[GET] 쿠폰 관리 페이지 조회 - 리스트 모델 담기 성공")
    void listCoupons_success() throws Exception {
        // given
        List<CouponResponse> mockList = List.of(
                new CouponResponse(
                        1L,              // id
                        TargetType.BOOK,    // targetType
                        10L,                // targetId
                        DiscountType.AMOUNT,// discountType
                        1000,               // discountValue
                        20000,              // minOrderAmount
                        null,               // maxDiscountAmount (nullable)
                        30,                 // validDays
                        "오픈 기념 쿠폰"      // name
                ),
                new CouponResponse(
                        2L,
                        TargetType.CATEGORY,
                        20L,
                        DiscountType.RATE,
                        10,
                        15000,
                        5000,
                        7,
                        "카테고리 할인"
                )
        );

        when(couponFeignClient.listCoupons())
                .thenReturn(ResponseEntity.ok(mockList));

        // when & then
        mvc.perform(get("/admin/coupons/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/coupon"))
                .andExpect(model().attributeExists("coupons"))
                .andExpect(model().attribute("coupons", mockList));
    }
}