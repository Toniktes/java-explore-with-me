package ru.practicum.main;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.exception.*;
import ru.practicum.main.models.ApiError;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestControllerAdvice
public class ErrorHandler {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Pattern.DATE);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleCategoryIsNotEmptyException(CategoryIsNotEmptyException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleCategoryNotExistException(CategoryNotExistException exception) {
        return new ApiError(exception.getMessage(), "Category with this id doesn't exist",
                HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleUserNameAlreadyExistException(NameAlreadyExistException exception) {
        return new ApiError(exception.getMessage(), "the duplication restriction has been violated.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiError handleValidationException(ValidationException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.BAD_REQUEST.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ApiError handleWrongTimeOfEventException(final WrongTimeException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleUserNotExistException(final UserNotExistException exception) {
        return new ApiError(exception.getMessage(), "User with this id doesn't exist",
                HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleEventNotExistException(final EventNotExistException exception) {
        return new ApiError(exception.getMessage(), "Event with this id doesn't exist",
                HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleAlreadyPublishedException(final AlreadyPublishedException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleRequestAlreadyExistException(final RequestAlreadyExistException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleWrongUserException(final WrongUserException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleExceedingLimitException(final ExceedingLimitException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiError handleRequestNotExistException(final RequestNotExistException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.NOT_FOUND.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleRequestAlreadyConfirmedException(final RequestAlreadyConfirmedException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleEventAlreadyCanceledException(final EventAlreadyCanceledException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ApiError handleAlreadyExistsException(final AlreadyExistsException exception) {
        return new ApiError(exception.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.getReasonPhrase().toUpperCase(), LocalDateTime.now().format(dateFormatter));
    }

}
