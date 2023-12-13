package at.ac.tuwien.sepr.groupphase.backend.endpoint.exceptionhandler;

import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.FatalException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.invoke.MethodHandles;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Register all your Java exceptions here to map them into meaningful HTTP exceptions
 * If you have special cases which are only important for specific endpoints, use ResponseStatusExceptions
 * https://www.baeldung.com/exception-handling-for-rest-with-spring#responsestatusexception
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Handles NotFoundException and sends a NOT_FOUND status.
     */
    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Handles ValidationExceptions and sends a UNPROCESSABLE_ENTITY status.
     */
    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<Object> handleValidation(ValidationException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }


    /**
     * Handles ConflictException and sends a CONFLICT status.
     */
    @ExceptionHandler(value = {ConflictException.class})
    protected ResponseEntity<Object> handleConflict(ConflictException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {FatalException.class})
    protected ResponseEntity<Object> handleFatalException(FatalException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Handles AuthenticationException and sends a METHOD_NOT_ALLOWED status.
     */
    @ExceptionHandler(value = {AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthentication(AuthenticationException ex, WebRequest request) {
        LOGGER.warn(ex.getMessage());
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED, request);
    }


    /**
     * Override methods from ResponseEntityExceptionHandler to send a customized HTTP response for a know exception
     * from e.g. Spring
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        //Get all errors
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + " " + err.getDefaultMessage())
            .collect(Collectors.toList());
        body.put("Validation errors", errors);

        return new ResponseEntity<>(body.toString(), headers, status);

    }
}
