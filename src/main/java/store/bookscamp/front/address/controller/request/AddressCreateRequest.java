package store.bookscamp.front.address.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressCreateRequest {

    @NotBlank
    private String label;

    @NotBlank
    @JsonProperty("road_name_address")
    private String roadNameAddress;

    @NotNull
    @JsonProperty("zip_code")
    private Integer zipCode;

    @NotNull
    @JsonProperty("is_default")
    private Boolean isDefault;

    @NotBlank
    @JsonProperty("detail_address")
    private String detailAddress;
}