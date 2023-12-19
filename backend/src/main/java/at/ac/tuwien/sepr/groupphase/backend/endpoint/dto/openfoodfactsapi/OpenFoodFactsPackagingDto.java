package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.openfoodfactsapi;

import java.util.List;

public record OpenFoodFactsPackagingDto(
    List<OpenFoodFactsPackagingsDto> packagings
) {
}
