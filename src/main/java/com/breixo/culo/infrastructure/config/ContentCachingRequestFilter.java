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

  /** {@inheritDoc} */
  @Override
  protected void doFilterInternal(
      final HttpServletRequest httpServletRequest,
      final HttpServletResponse httpServletResponse,
      final FilterChain filterChain) throws ServletException, IOException {
 
    final var contentCachingRequestWrapper = new ContentCachingRequestWrapper(httpServletRequest);
    filterChain.doFilter(contentCachingRequestWrapper, httpServletResponse);
  }
}
