package store.bookscamp.front.address.controller.request;


public record AddressUpdateRequest(
        String label,
        String roadNameAddress,
        Integer zipCode
) {

}
