package store.bookscamp.front.address.controller.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressCreateRequest(
        @NotBlank String label,
        @NotBlank @JsonProperty("road_name_address") String roadNameAddress,
        @NotNull @JsonProperty("zip_code") Integer zipCode,
        @NotNull @JsonProperty("is_default") Boolean isDefault,
        @NotBlank @JsonProperty("detail_address") String detailAddress
) {


}
