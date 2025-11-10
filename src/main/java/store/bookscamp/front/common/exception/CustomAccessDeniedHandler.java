package store.bookscamp.front.common.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("Access Denied: {}", "접근 권한이 없습니다.");

        request.setAttribute("errorCode","ErrorCode : 403");
        request.setAttribute("errorMessage", "접근 권한이 없습니다.");
        request.getRequestDispatcher("/error/forbidden").forward(request, response);
    }
}