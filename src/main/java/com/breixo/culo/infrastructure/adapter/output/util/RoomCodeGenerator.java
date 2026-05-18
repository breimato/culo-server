package com.breixo.culo.infrastructure.adapter.output.util;

import com.breixo.culo.domain.port.output.room.RoomCodeGenerationPort;
import com.breixo.culo.domain.port.output.room.RoomPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class RoomCodeGenerator implements RoomCodeGenerationPort {

  private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  private static final int CODE_LENGTH = 4;
  private static final int MAX_ATTEMPTS = 50;

  /** The room persistence port. */
  private final RoomPersistencePort roomPersistencePort;

  private final SecureRandom secureRandom = new SecureRandom();

  @Override
  public String generateUnique() {
    return IntStream.range(0, MAX_ATTEMPTS)
        .mapToObj(attempt -> this.generateCode())
        .filter(code -> !this.roomPersistencePort.existsByCode(code))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No se pudo generar un código de sala único"));
  }

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
