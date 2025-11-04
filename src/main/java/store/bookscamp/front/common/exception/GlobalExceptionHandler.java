package store.bookscamp.front.common.exception;

import feign.FeignException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public String handleFeignException(FeignException ex, Model model) {
        int statusCode = ex.status();
        String errorMessage = ex.contentUTF8();

        model.addAttribute("errorCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);

        return "error/error";
    }
}