package com.breixo.culo.infrastructure.adapter.output.util;

import com.breixo.culo.domain.exception.RoomException;
import com.breixo.culo.domain.exception.constants.RoomExceptionConstants;
import com.breixo.culo.domain.port.output.room.RoomExistencePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The Class RoomCodeGeneratorTest.
 */
@ExtendWith(MockitoExtension.class)
class RoomCodeGeneratorTest {

  /** The Constant CODE_CHARS. */
  private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

  /** The Constant CODE_LENGTH. */
  private static final int CODE_LENGTH = 4;

  /** The Constant MAX_ATTEMPTS. */
  private static final int MAX_ATTEMPTS = 50;

  /** The room existence persistence port. */
  @Mock
  RoomExistencePersistencePort roomExistencePersistencePort;

  /** The secure random. */
  @Mock
  SecureRandom secureRandom;

  /** The room code generator. */
  RoomCodeGenerator roomCodeGenerator;

  /**
	 * Inits the.
	 */
  @BeforeEach
  void init() {
    this.roomCodeGenerator = new RoomCodeGenerator(this.roomExistencePersistencePort, this.secureRandom);
    when(this.secureRandom.nextInt(CODE_CHARS.length())).thenReturn(0);
  }

  /**
	 * Test execute when code available then return four char code from charset.
	 */
  @Test
  void testExecute_whenCodeAvailable_thenReturnFourCharCodeFromCharset() {
    // Given
    when(this.roomExistencePersistencePort.existsByCode("AAAA")).thenReturn(Boolean.FALSE);

    // When
    final var roomCode = this.roomCodeGenerator.execute();

    // Then
    verify(this.roomExistencePersistencePort, times(1)).existsByCode("AAAA");
    assertEquals(CODE_LENGTH, roomCode.length());
    assertTrue(roomCode.chars().allMatch(character -> CODE_CHARS.indexOf(character) >= 0));
    assertEquals("AAAA", roomCode);
  }

  /**
	 * Test execute when all attempts collide then throw room exception.
	 */
  @Test
  void testExecute_whenAllAttemptsCollide_thenThrowRoomException() {
    // Given
    when(this.roomExistencePersistencePort.existsByCode("AAAA")).thenReturn(Boolean.TRUE);

    // When
    final var roomException = assertThrows(RoomException.class, () -> this.roomCodeGenerator.execute());

    // Then
    verify(this.roomExistencePersistencePort, times(MAX_ATTEMPTS)).existsByCode("AAAA");
    assertEquals(RoomExceptionConstants.UNIQUE_CODE_GENERATION_FAILED, roomException.getMessage());
  }

  /**
	 * Test execute when first attempt collides then retry and return code.
	 */
  @Test
  void testExecute_whenFirstAttemptCollides_thenRetryAndReturnCode() {
    // Given
    when(this.roomExistencePersistencePort.existsByCode("AAAA"))
        .thenReturn(Boolean.TRUE)
        .thenReturn(Boolean.FALSE);

    // When
    final var roomCode = this.roomCodeGenerator.execute();

    // Then
    verify(this.roomExistencePersistencePort, times(2)).existsByCode("AAAA");
    assertEquals("AAAA", roomCode);
  }
}
