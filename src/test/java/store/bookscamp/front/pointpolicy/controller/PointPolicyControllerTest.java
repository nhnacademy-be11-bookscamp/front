package store.bookscamp.front.pointpolicy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static store.bookscamp.front.pointpolicy.controller.enums.PointPolicyType.REVIEW_TEXT;
import static store.bookscamp.front.pointpolicy.controller.enums.PointPolicyType.STANDARD;
import static store.bookscamp.front.pointpolicy.controller.enums.RewardType.AMOUNT;
import static store.bookscamp.front.pointpolicy.controller.enums.RewardType.RATE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.pointpolicy.controller.request.PointPolicyCreateRequest;
import store.bookscamp.front.pointpolicy.controller.request.PointPolicyUpdateRequest;
import store.bookscamp.front.pointpolicy.controller.response.PointPolicyResponse;
import store.bookscamp.front.pointpolicy.feign.PointPolicyFeignClient;

@ExtendWith(MockitoExtension.class)
class PointPolicyControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    PointPolicyFeignClient pointPolicyFeignClient;

    @InjectMocks
    PointPolicyController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("GET /admin/point-policies - 포인트 정책 리스트 페이지 조회")
    void listPointPolicies() throws Exception {

        List<PointPolicyResponse> responses = List.of(
                new PointPolicyResponse(1L, STANDARD, AMOUNT, 5000),
                new PointPolicyResponse(2L, REVIEW_TEXT, RATE, 10)
        );

        when(pointPolicyFeignClient.listPointPolicies())
                .thenReturn(ResponseEntity.ok(responses));

        mockMvc.perform(get("/admin/point-policies"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("pointPolicies"))
                .andExpect(model().attributeExists("pointPolicyTypes"))
                .andExpect(model().attributeExists("rewardTypes"))
                .andExpect(view().name("admin/point-policy"));
    }

    @Test
    @DisplayName("POST /admin/point-policies - 포인트 정책 생성")
    void createPointPolicy() throws Exception {

        PointPolicyCreateRequest request = new PointPolicyCreateRequest(
                STANDARD,
                AMOUNT,
                1000
        );

        when(pointPolicyFeignClient.createPointPolicy(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(post("/admin/point-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /admin/point-policies/{id}/update - 포인트 정책 수정")
    void updatePointPolicy() throws Exception {

        PointPolicyUpdateRequest request = new PointPolicyUpdateRequest(STANDARD, AMOUNT, 500);

        when(pointPolicyFeignClient.updatePointPolicy(eq(1L), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/admin/point-policies/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /admin/point-policies/{id}/delete - 포인트 정책 삭제")
    void deletePointPolicy() throws Exception {

        when(pointPolicyFeignClient.deletePointPolicy(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/admin/point-policies/1/delete"))
                .andExpect(status().isOk());
    }
}
