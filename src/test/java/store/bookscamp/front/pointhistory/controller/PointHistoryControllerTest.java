package store.bookscamp.front.pointhistory.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import store.bookscamp.front.pointhistory.PointType;
import store.bookscamp.front.pointhistory.controller.response.PageResponse;
import store.bookscamp.front.pointhistory.controller.response.PointHistoryResponse;
import store.bookscamp.front.pointhistory.feign.PointHistoryFeignClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class PointHistoryControllerTest {

    MockMvc mvc;

    @Mock
    PointHistoryFeignClient feignClient;

    @InjectMocks
    PointHistoryController controller;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("[GET] 마이 포인트 조회 페이지 성공")
    void getMyPoints_success() throws Exception {

        PageResponse<PointHistoryResponse> mockPage = new PageResponse<>();
        var field = PageResponse.class.getDeclaredField("content");
        field.setAccessible(true);
        field.set(mockPage, List.of(
                new PointHistoryResponse(
                        1L,
                        10L,
                        PointType.EARN,
                        300,
                        "구매 적립",
                        LocalDateTime.now()
                )
        ));

        when(feignClient.getMyPointHistories(0, 10))
                .thenReturn(ResponseEntity.ok(mockPage));

        mvc.perform(get("/mypage/points"))
                .andExpect(status().isOk())
                .andExpect(view().name("pointHistory/mypoint"))
                .andExpect(model().attributeExists("points"))
                .andExpect(model().attributeExists("page"));

        verify(feignClient).getMyPointHistories(0, 10);
    }

    @Test
    @DisplayName("[GET] 마이 포인트 - page 파라미터 지정")
    void getMyPoints_with_page_param() throws Exception {

        PageResponse<PointHistoryResponse> emptyPage = new PageResponse<>();
        var field = PageResponse.class.getDeclaredField("content");
        field.setAccessible(true);
        field.set(emptyPage, List.of());

        when(feignClient.getMyPointHistories(2, 10))
                .thenReturn(ResponseEntity.ok(emptyPage));

        mvc.perform(get("/mypage/points?page=2"))
                .andExpect(status().isOk())
                .andExpect(view().name("pointHistory/mypoint"))
                .andExpect(model().attributeExists("points"))
                .andExpect(model().attributeExists("page"));

        verify(feignClient).getMyPointHistories(2, 10);
    }
}
