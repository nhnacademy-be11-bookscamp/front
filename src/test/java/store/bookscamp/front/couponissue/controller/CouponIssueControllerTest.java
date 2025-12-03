package store.bookscamp.front.couponissue.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.couponissue.controller.response.CouponIssueResponse;
import store.bookscamp.front.couponissue.controller.status.CouponFilterStatus;
import store.bookscamp.front.couponissue.feign.CouponIssueFeignClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CouponIssueControllerTest {

    MockMvc mvc;

    @Mock
    CouponIssueFeignClient couponIssueFeignClient;

    @InjectMocks
    CouponIssueController couponIssueController;

    @BeforeEach
    void setup() {
        // Pageable과 UserDetails 처리를 위해 ArgumentResolver 추가 설정
        mvc = MockMvcBuilders.standaloneSetup(couponIssueController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(), // Pageable 처리
                        new HandlerMethodArgumentResolver() { // @AuthenticationPrincipal Mock 처리
                            @Override
                            public boolean supportsParameter(MethodParameter parameter) {
                                return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
                            }

                            @Override
                            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                                // 테스트용 Mock UserDetails 반환
                                return mock(UserDetails.class);
                            }
                        }
                )
                .build();
    }

    @Test
    @DisplayName("[GET] 내 쿠폰함 조회 성공 - 로그인 상태")
    void getMyCoupons_success() throws Exception {
        // given
        // RestPageImpl은 실제 구현체가 없으므로 Mock 객체로 대체하거나 빈 응답을 가정
        RestPageImpl<CouponIssueResponse> mockPage = mock(RestPageImpl.class);

        when(couponIssueFeignClient.getMyCoupons(eq(CouponFilterStatus.ALL), any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(mockPage));

        // when & then
        mvc.perform(get("/mycoupon")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("couponissue/mycoupon"))
                .andExpect(model().attributeExists("couponPage"))
                .andExpect(model().attribute("currentStatus", CouponFilterStatus.ALL));
    }

    @Test
    @DisplayName("[GET] 내 쿠폰함 조회 - 특정 상태(USED) 필터링")
    void getMyCoupons_with_status_success() throws Exception {
        // given
        RestPageImpl<CouponIssueResponse> mockPage = mock(RestPageImpl.class);

        when(couponIssueFeignClient.getMyCoupons(eq(CouponFilterStatus.USED), any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(mockPage));

        // when & then
        mvc.perform(get("/mycoupon")
                        .param("status", "USED"))
                .andExpect(status().isOk())
                .andExpect(view().name("couponissue/mycoupon"))
                .andExpect(model().attribute("currentStatus", CouponFilterStatus.USED));
    }

    @Test
    @DisplayName("[GET] 내 쿠폰함 조회 실패 - 비로그인 상태 (Redirect)")
    void getMyCoupons_unauthorized() throws Exception {
        // 비로그인 상황을 테스트하기 위해 Resolver를 설정하되, null을 반환하도록 설정
        MockMvc noAuthMvc = MockMvcBuilders.standaloneSetup(couponIssueController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        // 여기를 추가해야 합니다!
                        new HandlerMethodArgumentResolver() {
                            @Override
                            public boolean supportsParameter(MethodParameter parameter) {
                                return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null;
                            }

                            @Override
                            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                                return null; // 비로그인 상태이므로 명시적으로 null 반환
                            }
                        }
                )
                .build();

        noAuthMvc.perform(get("/mycoupon"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}