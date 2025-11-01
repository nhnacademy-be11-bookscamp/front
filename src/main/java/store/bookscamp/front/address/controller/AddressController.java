package store.bookscamp.front.address.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import store.bookscamp.front.address.controller.request.AddressCreateRequest;
import store.bookscamp.front.address.controller.response.AddressListResponse;
import store.bookscamp.front.address.feign.AddressFeignClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members/{username}/address")
public class AddressController {

    private final AddressFeignClient addressFeignClient;

    @Value("${app.api.prefix}")
    private String apiPrefix;

    @GetMapping
    public ModelAndView getAddresses(@PathVariable("username") String username) {

        AddressListResponse body = addressFeignClient.getAddresses(username).getBody();

        ModelAndView mav = new ModelAndView("member/address/list");
        mav.addObject("apiPrefix", apiPrefix);
        mav.addObject("username", username);
        mav.addObject("addresses", body == null ? java.util.List.of() : body.addresses());
        return mav;
    }

    @GetMapping("/new")
    public ModelAndView showCreateAddressForm(@PathVariable String username) {
        ModelAndView mav = new ModelAndView("/member/address/new");
        mav.addObject("apiPrefix", apiPrefix);
        mav.addObject("username", username);
        mav.addObject("form", new AddressCreateRequest(null, null, null));
        return mav;
    }

    @PostMapping
    public String create(@PathVariable String username,
                         @ModelAttribute("form") AddressCreateRequest form) {
        addressFeignClient.createAddress(username, form);
        return "redirect:/members/" + username + "/address";
    }
}
