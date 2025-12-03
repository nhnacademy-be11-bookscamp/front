package store.bookscamp.front.couponissue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.couponissue.controller.request.CouponIssueRequest;
import store.bookscamp.front.couponissue.controller.response.CouponIssueDownloadResponse;
import store.bookscamp.front.couponissue.feign.CouponIssueFeignClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CouponIssueRestControllerTest {

    MockMvc mvc;

    @Mock
    CouponIssueFeignClient couponIssueFeignClient;

    @InjectMocks
    CouponIssueRestController couponIssueRestController;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(couponIssueRestController)
                .build();
    }

    @Test
    @DisplayName("[DELETE] 쿠폰 발급 취소/삭제 성공")
    void deleteCouponIssue_success() throws Exception {
        Long couponIssueId = 1L;

        // 반환값이 void이므로 when 구문 생략 가능 (필요시 doNothing 사용)

        mvc.perform(delete("/api-server/coupon-issue/{couponIssueId}", couponIssueId))
                .andExpect(status().isOk());

        verify(couponIssueFeignClient).deleteCouponIssue(couponIssueId);
    }

    @Test
    @DisplayName("[GET] 다운로드 가능한 쿠폰 목록 조회 성공")
    void getDownloadableCoupons_success() throws Exception {
        Long bookId = 100L;
        List<CouponIssueDownloadResponse> mockResponse = List.of(
                new CouponIssueDownloadResponse(1L, "오픈기념 쿠폰", "10% 할인"),
                new CouponIssueDownloadResponse(2L, "신간 할인", "2000원 할인")
        );

        when(couponIssueFeignClient.getDownloadableCoupons(bookId))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mvc.perform(get("/api-server/coupon-issue/downloadable/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
    }

    @Test
    @DisplayName("[POST] 쿠폰 발급 요청 성공")
    void issueCoupon_success() throws Exception {
        CouponIssueRequest request = new CouponIssueRequest(50L);
        Long expectedIssueId = 123L;

        when(couponIssueFeignClient.issueCoupon(any(CouponIssueRequest.class)))
                .thenReturn(ResponseEntity.ok(expectedIssueId));

        mvc.perform(post("/api-server/coupon-issue/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedIssueId)));

        verify(couponIssueFeignClient).issueCoupon(any(CouponIssueRequest.class));
    }
}