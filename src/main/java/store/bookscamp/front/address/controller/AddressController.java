package store.bookscamp.front.address.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import store.bookscamp.front.address.controller.request.AddressCreateRequest;
import store.bookscamp.front.address.controller.request.AddressUpdateRequest;
import store.bookscamp.front.address.controller.response.AddressListResponse;
import store.bookscamp.front.address.feign.AddressFeignClient;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/address")
public class AddressController {

    private final AddressFeignClient addressFeignClient;

    private static final String REDIRECT_TO_LIST = "redirect:/mypage/address";

    @Value("${app.api.prefix}")
    private String apiPrefix;

    @ModelAttribute("apiPrefix")
    public String getApiPrefix() {
        return apiPrefix;
    }

    @GetMapping
    public ModelAndView getAddresses() {
        AddressListResponse body = addressFeignClient.getAddresses().getBody();

        ModelAndView mav = new ModelAndView("member/address/list");
        mav.addObject("addresses", body == null ? List.of() : body.addresses());
        return mav;
    }

    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<AddressListResponse> getAddressesJson() {
        return addressFeignClient.getAddresses();
    }

    @GetMapping("/new")
    public ModelAndView showCreateAddressForm() {
        ModelAndView mav = new ModelAndView("member/address/new");
        mav.addObject("form", new AddressCreateRequest());
        return mav;
    }

    @PostMapping
    public String create(@ModelAttribute("form") AddressCreateRequest form) {
        addressFeignClient.createAddress(form);
        return REDIRECT_TO_LIST;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Void> createJson(@RequestBody AddressCreateRequest form) {
        return addressFeignClient.createAddress(form);
    }

    @GetMapping("/{id}/edit")
    public ModelAndView showEditForm(@PathVariable Long id,
                                     @RequestParam String label,
                                     @RequestParam String roadNameAddress,
                                     @RequestParam Integer zipCode,
                                     @RequestParam boolean isDefault,
                                     @RequestParam String detailAddress) {

        ModelAndView mav = new ModelAndView("member/address/edit");
        mav.addObject("id", id);
        mav.addObject("form", new AddressUpdateRequest(label, roadNameAddress, zipCode, isDefault, detailAddress));
        return mav;
    }

    @PutMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute("form") AddressUpdateRequest form,
                         RedirectAttributes ra) {
        addressFeignClient.updateAddress(id, form);
        ra.addFlashAttribute("message", "주소가 수정되었습니다!");
        return REDIRECT_TO_LIST;
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra) {
        addressFeignClient.deleteAddress(id);
        ra.addFlashAttribute("message", "주소가 삭제되었습니다!");
        return REDIRECT_TO_LIST;
    }
}