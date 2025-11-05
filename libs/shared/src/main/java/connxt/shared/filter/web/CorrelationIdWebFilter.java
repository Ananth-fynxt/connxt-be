package connxt.shared.filter.web;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorrelationIdWebFilter extends OncePerRequestFilter {

  public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
  public static final String CORRELATION_ID_ATTRIBUTE = "correlationId";

  public CorrelationIdWebFilter() {}

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String correlationId = request.getHeader(CORRELATION_ID_HEADER);

    if (StringUtils.isBlank(correlationId)) {
      correlationId = generateCorrelationId();
    }

    response.setHeader(CORRELATION_ID_HEADER, correlationId);
    request.setAttribute(CORRELATION_ID_ATTRIBUTE, correlationId);

    filterChain.doFilter(request, response);
  }

  private String generateCorrelationId() {
    return UUID.randomUUID().toString();
  }
}
