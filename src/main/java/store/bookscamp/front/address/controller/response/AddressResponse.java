package store.bookscamp.front.address.controller.response;

public record AddressResponse(
        String label,
        String roadNameAddress,
        Integer zipCode
        ) {
}
