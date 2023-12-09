package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

public record DebitDto(
    UserDetailDto user,
    Long percentage
) {
}
