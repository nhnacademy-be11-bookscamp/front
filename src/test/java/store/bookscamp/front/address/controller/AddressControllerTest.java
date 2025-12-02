package store.bookscamp.front.address.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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

import store.bookscamp.front.address.controller.request.AddressCreateRequest;
import store.bookscamp.front.address.controller.request.AddressUpdateRequest;
import store.bookscamp.front.address.controller.response.AddressListResponse;
import store.bookscamp.front.address.feign.AddressFeignClient;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    MockMvc mvc;

    @Mock
    AddressFeignClient addressFeignClient;

    @InjectMocks
    AddressController addressController;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(addressController)
                .build();
    }

    @Test
    @DisplayName("[GET] 주소 목록 페이지 조회 성공")
    void getAddresses_view_success() throws Exception {
        // given
        AddressListResponse.AddressResponse addr =
                new AddressListResponse.AddressResponse(
                        1L,
                        "집",
                        "서울시 어딘가",
                        12345,
                        true,
                        "101동 101호"
                );

        AddressListResponse response = new AddressListResponse(List.of(addr));

        when(addressFeignClient.getAddresses())
                .thenReturn(ResponseEntity.ok(response));

        // when & then
        mvc.perform(get("/mypage/address"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/address/list"))
                // apiPrefix는 @Value 주입이 안 되므로 검증하지 않음
                .andExpect(model().attributeExists("addresses"));

        verify(addressFeignClient).getAddresses();
    }

    @Test
    @DisplayName("[GET] 주소 목록 JSON 조회 성공")
    void getAddresses_json_success() throws Exception {
        // given
        AddressListResponse.AddressResponse addr =
                new AddressListResponse.AddressResponse(
                        1L,
                        "회사",
                        "서울시 강남구",
                        54321,
                        false,
                        "202호"
                );
        AddressListResponse response = new AddressListResponse(List.of(addr));

        when(addressFeignClient.getAddresses())
                .thenReturn(ResponseEntity.ok(response));

        // when & then
        mvc.perform(get("/mypage/address").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                // DTO에서 @JsonProperty("road_name_address") 이므로 JSON 키는 road_name_address
                .andExpect(jsonPath("$.addresses[0].label").value("회사"))
                .andExpect(jsonPath("$.addresses[0].road_name_address").value("서울시 강남구"));

        verify(addressFeignClient).getAddresses();
    }

    @Test
    @DisplayName("[GET] 주소 목록 - API 응답 body가 null인 경우에도 빈 리스트로 처리")
    void getAddresses_view_null_body_success() throws Exception {
        // given
        when(addressFeignClient.getAddresses())
                .thenReturn(ResponseEntity.ok(null));

        // when & then
        mvc.perform(get("/mypage/address"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/address/list"))
                .andExpect(model().attributeExists("addresses"));

        verify(addressFeignClient).getAddresses();
    }

    @Test
    @DisplayName("[GET] 신규 주소 생성 폼 페이지 조회 성공")
    void showCreateAddressForm_success() throws Exception {
        mvc.perform(get("/mypage/address/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/address/new"))
                // apiPrefix는 null이라 검증하지 않음
                .andExpect(model().attributeExists("form"));
    }

    @Test
    @DisplayName("[POST] 신규 주소 생성 - 폼 제출 성공")
    void createAddress_form_success() throws Exception {
        // given
        when(addressFeignClient.createAddress(any(AddressCreateRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mvc.perform(post("/mypage/address")
                        .param("label", "집")
                        .param("roadNameAddress", "서울특별시 어딘가")
                        .param("zipCode", "12345")
                        .param("isDefault", "true")
                        .param("detailAddress", "101동 101호"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/address"));

        verify(addressFeignClient).createAddress(any(AddressCreateRequest.class));
    }

    @Test
    @DisplayName("[POST] 신규 주소 생성 - JSON 요청 성공")
    void createAddress_json_success() throws Exception {
        // given
        when(addressFeignClient.createAddress(any(AddressCreateRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        String json = """
                {
                  "label": "집",
                  "road_name_address": "서울특별시 어딘가",
                  "zip_code": 12345,
                  "is_default": true,
                  "detail_address": "101동 101호"
                }
                """;

        // when & then
        mvc.perform(post("/mypage/address")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(addressFeignClient).createAddress(any(AddressCreateRequest.class));
    }

    @Test
    @DisplayName("[GET] 주소 수정 폼 페이지 조회 성공")
    void showEditForm_success() throws Exception {
        mvc.perform(get("/mypage/address/{id}/edit", 1L)
                        .param("label", "집")
                        .param("roadNameAddress", "서울특별시 수정로 1")
                        .param("zipCode", "11111")
                        .param("isDefault", "false")
                        .param("detailAddress", "202동 303호"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/address/edit"))
                .andExpect(model().attributeExists("id"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    @DisplayName("[PUT] 주소 수정 - 폼 제출 성공")
    void updateAddress_form_success() throws Exception {
        // given
        when(addressFeignClient.updateAddress(anyLong(), any(AddressUpdateRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mvc.perform(put("/mypage/address/{id}/edit", 1L)
                        .param("label", "회사")
                        .param("roadNameAddress", "서울시 강남구 수정로 2")
                        .param("zipCode", "22222")
                        .param("isDefault", "true")
                        .param("detailAddress", "505호"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/address"));

        verify(addressFeignClient).updateAddress(eq(1L), any(AddressUpdateRequest.class));
    }

    @Test
    @DisplayName("[DELETE] 주소 삭제 성공")
    void deleteAddress_success() throws Exception {
        // given
        when(addressFeignClient.deleteAddress(1L))
                .thenReturn(ResponseEntity.ok().build());

        // when & then
        mvc.perform(delete("/mypage/address/{id}/delete", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage/address"));

        verify(addressFeignClient).deleteAddress(1L);
    }
}
