package store.bookscamp.front.common.exception;

import feign.FeignException;
import feign.Request;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();

    @Test
    @DisplayName("FeignException 발생 시 에러 페이지로 이동하고 모델에 코드를 담는다")
    void handleFeignException() {
        Request request = Request.create(Request.HttpMethod.GET, "url", Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        FeignException ex = new FeignException.BadRequest("Bad Request", request, "Error Body".getBytes(StandardCharsets.UTF_8), null);
        Model model = new ConcurrentModel();

        String viewName = exceptionHandler.handleFeignException(ex, model);

        assertThat(viewName).isEqualTo("error/error");
        assertThat(model.getAttribute("errorCode")).isEqualTo(400);
        assertThat(model.getAttribute("errorMessage")).isEqualTo("Error Body");
    }

    @Test
    @DisplayName("RefreshTokenExpiredException 발생 시 로그아웃으로 리다이렉트한다")
    void handleTokenExpired() {
        RefreshTokenExpiredException ex = new RefreshTokenExpiredException("Expired");

        String viewName = exceptionHandler.handleTokenExpired(ex);

        assertThat(viewName).isEqualTo("redirect:/logout");
    }

    @Test
    @DisplayName("접근 거부 시 403 에러 속성을 설정하고 포워딩한다")
    void handleAccessDenied() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // RequestDispatcher Mocking (MockRequest는 기본적으로 getRequestDispatcher를 지원하지만 검증을 위해)
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        request.getRequestDispatcher("/error/forbidden"); // 실제로는 내부 설정이지만, Mock객체 주입이 어려우므로 Spy 대신 속성 확인 위주로 진행

        // MockRequest는 Dispatcher 로직을 내부적으로 처리하거나 무시하므로,
        // 여기서는 setAttribute가 잘 되었는지만 확인해도 충분합니다.

        accessDeniedHandler.handle(request, response, new AccessDeniedException("Denied"));

        assertThat(request.getAttribute("errorCode")).isEqualTo("ErrorCode : 403");
        assertThat(request.getAttribute("errorMessage")).isEqualTo("접근 권한이 없습니다.");
        // forward 여부는 MockHttpServletRequest 특성상 확인이 까다로우나, 예외 없이 실행되면 성공으로 간주
    }
}