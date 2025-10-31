package store.bookscamp.front.address.controller.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressCreateRequest(String label, @NotBlank @JsonProperty("road_name_address") String address,
                                   @NotNull Integer zipCode) {


}
