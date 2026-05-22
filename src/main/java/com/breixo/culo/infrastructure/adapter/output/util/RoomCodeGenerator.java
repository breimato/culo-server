package com.breixo.culo.infrastructure.adapter.output.util;

import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.port.output.room.RoomCodeGenerationPort;
import com.breixo.culo.domain.port.output.room.RoomExistencePersistencePort;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpStatus;
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

  /** The room existence persistence port. */
  private final RoomExistencePersistencePort roomExistencePersistencePort;

  /** The secure random. */
  private final SecureRandom secureRandom;

  /**
	 * Execute.
	 *
	 * @return the string
	 */
  @Override
  public String execute() {

    return IntStream.range(0, MAX_ATTEMPTS)
        .mapToObj(attempt -> this.generateCode())
        .filter(code -> BooleanUtils.isFalse(this.roomExistencePersistencePort.existsByCode(code)))
        .findFirst()
        .orElseThrow(
                () -> new RoomException(
                    RoomExceptionConstants.UNIQUE_CODE_GENERATION_FAILED,
                    HttpStatus.INTERNAL_SERVER_ERROR));
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
