package store.bookscamp.front.address.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.bookscamp.front.address.controller.request.AddressCreateRequest;
import store.bookscamp.front.address.controller.request.AddressUpdateRequest;
import store.bookscamp.front.address.controller.response.AddressListResponse;
import store.bookscamp.front.address.controller.response.AddressResponse;

@FeignClient(name = "addressApiClient", url = "${gateway.base-url}")
public interface AddressFeignClient {

    @GetMapping("/api-server/member/{username}/address")
    ResponseEntity<AddressListResponse> getAddresses(@PathVariable("username") String username);

    @PostMapping("/api-server/member/{username}/address")
    ResponseEntity<Void> createAddress(@PathVariable("username") String username,
                                       @RequestBody AddressCreateRequest request);

    @PatchMapping("/api-server/member/{username}/address/{addressId}")
    ResponseEntity<AddressResponse> updateAddress(
            @PathVariable("username") String username,
            @PathVariable("addressId") Long addressId,
            @RequestBody AddressUpdateRequest request);

    @DeleteMapping("/api-server/member/{username}/address/{addressId}")
    ResponseEntity<Void> deleteAddress(@PathVariable("username") String username,
                                       @PathVariable("addressId") Long addressId);
}
