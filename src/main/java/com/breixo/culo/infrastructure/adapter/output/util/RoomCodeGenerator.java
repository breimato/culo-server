package com.breixo.culo.infrastructure.adapter.output.util;

import com.breixo.culo.domain.port.output.room.RoomCodeGenerationPort;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.stream.IntStream;

/**
 * The Class RoomCodeGenerator.
 */
@Component
@RequiredArgsConstructor
public class RoomCodeGenerator implements RoomCodeGenerationPort {

  /** The Constant CODE_CHARS. */
  private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  
  /** The Constant CODE_LENGTH. */
  private static final int CODE_LENGTH = 4;
  
  /** The Constant MAX_ATTEMPTS. */
  private static final int MAX_ATTEMPTS = 50;

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  /** The secure random. */
  private final SecureRandom secureRandom = new SecureRandom();

  /**
	 * Generate unique.
	 *
	 * @return the string
	 */
  @Override
  public String generateUnique() {
    return IntStream.range(0, MAX_ATTEMPTS)
        .mapToObj(attempt -> this.generateCode())
        .filter(code -> !this.roomPersistencePort.existsByCode(code))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No se pudo generar un código de sala único"));
  }

  /**
	 * Generate code.
	 *
	 * @return the string
	 */
  private String generateCode() {

final var codeBuilder = new StringBuilder(CODE_LENGTH);
    IntStream.range(0, CODE_LENGTH)
        .forEach(index -> {
          final var charIndex = this.secureRandom.nextInt(CODE_CHARS.length());
          codeBuilder.append(CODE_CHARS.charAt(charIndex));
        });
    return codeBuilder.toString();
  }
}
