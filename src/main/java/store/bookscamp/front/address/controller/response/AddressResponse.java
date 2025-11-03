package store.bookscamp.front.address.controller.response;

public record AddressResponse(
        Long id,
        String label,
        String roadNameAddress,
        Integer zipCode
        ) {
}
