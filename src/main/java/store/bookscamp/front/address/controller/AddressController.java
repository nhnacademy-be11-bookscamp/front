package store.bookscamp.front.address.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import store.bookscamp.front.address.controller.request.AddressCreateRequest;
import store.bookscamp.front.address.controller.request.AddressUpdateRequest;
import store.bookscamp.front.address.controller.response.AddressListResponse;
import store.bookscamp.front.address.feign.AddressFeignClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/address")
public class AddressController {

    private final AddressFeignClient addressFeignClient;

    @Value("${app.api.prefix}")
    private String apiPrefix;

    @GetMapping
    public ModelAndView getAddresses() {

        AddressListResponse body = addressFeignClient.getAddresses().getBody();

        ModelAndView mav = new ModelAndView("member/address/list");
        mav.addObject("apiPrefix", apiPrefix);
        mav.addObject("addresses", body == null ? java.util.List.of() : body.addresses());
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
        mav.addObject("apiPrefix", apiPrefix);
        mav.addObject("form", new AddressCreateRequest(null, null, null, null, null));
        return mav;
    }

    @PostMapping
    public String create(@ModelAttribute("form") AddressCreateRequest form) {
        addressFeignClient.createAddress(form);

        return "redirect:/mypage/address";
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Void> createJson(@RequestBody AddressCreateRequest form) {
        return addressFeignClient.createAddress(form);
    }

    // 예: /members/address/{id}/edit?label=집&roadNameAddress=서울시..&zipCode=12345
    @GetMapping("/{id}/edit")
    public ModelAndView showEditForm(@PathVariable Long id,
                                     @RequestParam String label,
                                     @RequestParam String roadNameAddress,
                                     @RequestParam Integer zipCode,
                                     @RequestParam boolean isDefault,
                                     @RequestParam String detailAddress) {

        ModelAndView mav = new ModelAndView("member/address/edit");
        mav.addObject("apiPrefix", apiPrefix);
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
        return "redirect:/mypage/address";
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes ra) {
        addressFeignClient.deleteAddress(id);
        ra.addFlashAttribute("message", "주소가 삭제되었습니다!");
        return "redirect:/mypage/address";
    }
}
