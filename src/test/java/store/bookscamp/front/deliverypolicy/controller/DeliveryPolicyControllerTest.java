package store.bookscamp.front.deliverypolicy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyCreateRequest;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyUpdateRequest;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryPolicyResponse;
import store.bookscamp.front.deliverypolicy.service.DeliveryPolicyService;
import feign.FeignException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class DeliveryPolicyControllerTest {

    private MockMvc mvc;

    @Mock
    private DeliveryPolicyService deliveryPolicyService;

    @InjectMocks
    private DeliveryPolicyController deliveryPolicyController;

    private final String baseUrl = "/admin/delivery-policy";
    private final Long testPolicyId = 1L;
    private final int testThreshold = 50000;
    private final int testFee = 3000;
    private DeliveryPolicyResponse createResponse() {
        return new DeliveryPolicyResponse(testPolicyId, testThreshold, testFee);
    }

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(deliveryPolicyController).build();
    }

    @Nested
    @DisplayName("GET " + baseUrl)
    class GetDeliveryPolicyTest {

        @Test
        @DisplayName("정책 조회 성공 시 기존 정책을 수정 폼과 함께 Model에 담는다")
        void getDeliveryPolicy_Success() throws Exception {
            DeliveryPolicyResponse mockPolicy = createResponse();
            given(deliveryPolicyService.getDeliveryPolicy()).willReturn(mockPolicy);

            mvc.perform(get(baseUrl))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/delivery-policy"))
                    .andExpect(model().attribute("policy", mockPolicy))
                    .andExpect(model().attribute("isNew", false)) // 정책이 존재함
                    .andExpect(model().attribute("form",
                            new DeliveryPolicyUpdateRequest(testThreshold, testFee)));

            verify(deliveryPolicyService).getDeliveryPolicy();
        }

        @Test
        @DisplayName("정책 조회 실패 시 (정책 없음) 등록 폼과 isNew=true를 Model에 담는다")
        void getDeliveryPolicy_Failure_NoPolicy() throws Exception {
            doThrow(FeignException.NotFound.class).when(deliveryPolicyService).getDeliveryPolicy();

            mvc.perform(get(baseUrl))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/delivery-policy"))
                    .andExpect(model().attribute("isNew", true)) // 정책이 없으므로 등록 폼
                    .andExpect(model().attribute("form", new DeliveryPolicyCreateRequest()));

            verify(deliveryPolicyService).getDeliveryPolicy();
        }
    }

    @Nested
    @DisplayName("POST " + baseUrl)
    class SubmitDeliveryPolicyTest {

        private final DeliveryPolicyUpdateRequest validUpdateForm =
                new DeliveryPolicyUpdateRequest(testThreshold, testFee);

        private final DeliveryPolicyUpdateRequest invalidUpdateForm =
                new DeliveryPolicyUpdateRequest(-100, 3000);

        @Test
        @DisplayName("정책이 존재할 때: 유효한 폼 제출 시 기존 정책을 수정하고 리다이렉트한다")
        void submitPolicy_Update_Success() throws Exception {
            given(deliveryPolicyService.updateDeliveryPolicy(any(DeliveryPolicyUpdateRequest.class)))
                    .willReturn(createResponse());

            mvc.perform(post(baseUrl)
                            .param("freeDeliveryThreshold", String.valueOf(testThreshold))
                            .param("baseDeliveryFee", String.valueOf(testFee))
                            .flashAttr("form", validUpdateForm))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/delivery-policy"));

            verify(deliveryPolicyService).updateDeliveryPolicy(any(DeliveryPolicyUpdateRequest.class));

        }

        @Test
        @DisplayName("정책이 없을 때: update 실패 후 create를 시도하여 성공하고 리다이렉트한다")
        void submitPolicy_Create_Success() throws Exception {
            doThrow(FeignException.NotFound.class).when(deliveryPolicyService).updateDeliveryPolicy(any(DeliveryPolicyUpdateRequest.class));
            given(deliveryPolicyService.createDeliveryPolicy(any(DeliveryPolicyCreateRequest.class)))
                    .willReturn(createResponse());

            mvc.perform(post(baseUrl)
                            .param("freeDeliveryThreshold", String.valueOf(testThreshold))
                            .param("baseDeliveryFee", String.valueOf(testFee))
                            .flashAttr("form", validUpdateForm))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/delivery-policy"));

            verify(deliveryPolicyService).updateDeliveryPolicy(any(DeliveryPolicyUpdateRequest.class));
            verify(deliveryPolicyService).createDeliveryPolicy(any(DeliveryPolicyCreateRequest.class));
        }

        @Test
        @DisplayName("유효성 검사 실패 시: 기존 정책을 다시 조회하여 Model에 담고 폼 페이지를 반환한다")
        void submitPolicy_BindingError_ExistingPolicy() throws Exception {
            DeliveryPolicyResponse mockPolicy = createResponse();
            given(deliveryPolicyService.getDeliveryPolicy()).willReturn(mockPolicy);

            mvc.perform(post(baseUrl)
                            .param("freeDeliveryThreshold", String.valueOf(invalidUpdateForm.freeDeliveryThreshold()))
                            .param("baseDeliveryFee", String.valueOf(invalidUpdateForm.baseDeliveryFee()))
                            .flashAttr("form", invalidUpdateForm))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/delivery-policy"))
                    .andExpect(model().attributeHasErrors("form")) // 유효성 검사 오류 확인
                    .andExpect(model().attribute("isNew", false))
                    .andExpect(model().attribute("policy", mockPolicy)); // 기존 정책 데이터 유지

            verify(deliveryPolicyService).getDeliveryPolicy();
        }

        @Test
        @DisplayName("유효성 검사 실패 시 (정책 없음): isNew=true를 Model에 담고 폼 페이지를 반환한다")
        void submitPolicy_BindingError_NoPolicy() throws Exception {
            doThrow(FeignException.NotFound.class).when(deliveryPolicyService).getDeliveryPolicy();

            mvc.perform(post(baseUrl)
                            .param("freeDeliveryThreshold", String.valueOf(invalidUpdateForm.freeDeliveryThreshold()))
                            .param("baseDeliveryFee", String.valueOf(invalidUpdateForm.baseDeliveryFee()))
                            .flashAttr("form", invalidUpdateForm))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/delivery-policy"))
                    .andExpect(model().attributeHasErrors("form"))
                    .andExpect(model().attribute("isNew", true));

            verify(deliveryPolicyService).getDeliveryPolicy();
        }
    }
}