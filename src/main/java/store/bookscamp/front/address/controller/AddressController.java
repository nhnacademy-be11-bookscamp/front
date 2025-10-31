package store.bookscamp.front.address.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import store.bookscamp.front.address.feign.AddressFeignClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/{username}/address")
public class AddressController {

    private final AddressFeignClient addressFeignClient;


}
