package at.ac.tuwien.sepr.groupphase.backend.exception;

import java.util.List;

public class OpenFoodFactApiException extends ErrorListException {

    public OpenFoodFactApiException(String message) {
        super(message, List.of());
    }

    public OpenFoodFactApiException(String generalMessage, List<String> errors) {
        super(generalMessage, errors);
    }
}
