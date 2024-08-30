package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(final Exception e) {
        log.info("Произошло исключение валидации с кодом 400: {}", e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleIllegalArgumentException(final Exception e) {
        log.info("Конфликт запроса, произошло исключение с кодом 409: {}", e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(final NotFoundException e) {
        log.info("Объект не найден, произошло исключение с кодом 404: {}", e.getMessage());
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleException(final Exception e) {
        log.error("Error", e);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(out, true, StandardCharsets.UTF_8));
        return new ErrorMessage(out.toString(StandardCharsets.UTF_8));
    }
}
