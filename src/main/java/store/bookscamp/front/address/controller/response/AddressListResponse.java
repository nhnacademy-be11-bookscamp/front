// store.bookscamp.front.address.controller.response.AddressListResponse
package store.bookscamp.front.address.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AddressListResponse(
        @JsonProperty("addresses") List<AddressResponse> addresses
) {
    public record AddressResponse(
            Long id,
            @NotBlank String label,
            @NotBlank @JsonProperty("road_name_address") String roadNameAddress,
            @NotNull @JsonProperty("zip_code") Integer zipCode,
            @NotNull @JsonProperty("is_default") Boolean isDefault,
            @NotBlank @JsonProperty("detail_address") String detailAddress
    ) {

    }
}
