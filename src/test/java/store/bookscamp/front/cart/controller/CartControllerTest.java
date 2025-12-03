package store.bookscamp.front.cart.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.cart.controller.request.CartItemAddRequest;
import store.bookscamp.front.cart.controller.request.CartItemUpdateRequest;
import store.bookscamp.front.cart.controller.response.CartItemsResponse;
import store.bookscamp.front.cart.feign.CartFeignClient;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    CartFeignClient cartFeignClient;

    @InjectMocks
    CartController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("GET /carts - 장바구니 화면 조회")
    void viewCart() throws Exception {

        List<CartItemsResponse> items = List.of(
                new CartItemsResponse(1L, 1L, "책1", "", 2, 20000, 18000, 36000),
                new CartItemsResponse(2L, 2L, "책2", "", 1, 15000, 13500, 13500)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "cartToken=abc123; Path=/; HttpOnly");

        when(cartFeignClient.getCartItems())
                .thenReturn(new ResponseEntity<>(items, headers, HttpStatus.OK));

        mockMvc.perform(get("/carts"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(header().stringValues("Set-Cookie", "cartToken=abc123; Path=/; HttpOnly"))
                .andExpect(view().name("cart/cart"));
    }

    @Test
    @DisplayName("POST /carts - 장바구니 아이템 추가")
    void addCartItem() throws Exception {

        CartItemAddRequest request = new CartItemAddRequest(10L, 2);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "cartToken=xyz456; Path=/; HttpOnly");

        when(cartFeignClient.addCartItems(any()))
                .thenReturn(new ResponseEntity<>(headers, HttpStatus.CREATED));

        mockMvc.perform(post("/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Set-Cookie", "cartToken=xyz456; Path=/; HttpOnly"));
    }

    @Test
    @DisplayName("POST /carts/{id}/update - 장바구니 수량 변경")
    void updateCart() throws Exception {

        CartItemUpdateRequest request = new CartItemUpdateRequest(5);

        when(cartFeignClient.updateCartItem(eq(1L), any()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/carts/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /carts/{id}/delete - 장바구니 아이템 삭제")
    void deleteCartItem() throws Exception {

        when(cartFeignClient.deleteCartItem(1L))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/carts/1/delete"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /carts/clear - 장바구니 전체 비우기")
    void clearCart() throws Exception {

        when(cartFeignClient.clearCart())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/carts/clear"))
                .andExpect(status().isOk());
    }
}
