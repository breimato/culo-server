package com.breixo.culo.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * The Class ContentCachingRequestFilter.
 */
@Component
public class ContentCachingRequestFilter extends OncePerRequestFilter {

  /**
	 * Do filter internal.
	 *
	 * @param httpServletRequest  the http servlet request
	 * @param httpServletResponse the http servlet response
	 * @param filterChain         the filter chain
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 */
  @Override
  protected void doFilterInternal(
      final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse,
      final FilterChain filterChain) throws ServletException, IOException {
 
    final var contentCachingRequestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
    filterChain.doFilter(contentCachingRequestWrapper, httpServletResponse);
  }
}
