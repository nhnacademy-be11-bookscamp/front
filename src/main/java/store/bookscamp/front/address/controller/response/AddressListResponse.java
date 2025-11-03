// store.bookscamp.front.address.controller.response.AddressListResponse
package store.bookscamp.front.address.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AddressListResponse(
        @JsonProperty("addresses") List<AddressResponse> addresses
) {
    public record AddressResponse(
            Long id,
            String label,
            @JsonProperty("road_name_address") String roadNameAddress,
            @JsonProperty("zip_code") Integer zipCode
    ) {

    }
}
