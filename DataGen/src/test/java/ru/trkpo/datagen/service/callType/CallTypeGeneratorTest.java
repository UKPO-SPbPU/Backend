package ru.trkpo.datagen.service.callType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class CallTypeGeneratorTest {

    @Mock
    private Random randomMock;

    @InjectMocks
    private CallTypeGeneratorImpl underTestGenerator;

    private static final double INCOMING_CALL_CHANCE = 0.5;
    private static final String INCOMING_CALL_CODE = "02";
    private static final String OUTCOMING_CALL_CODE = "01";

    @BeforeEach
    void setUp() {
        setField(underTestGenerator, "incomingCallChance", INCOMING_CALL_CHANCE);
        setField(underTestGenerator, "incomingCallCode", INCOMING_CALL_CODE);
        setField(underTestGenerator, "outcomingCallCode", OUTCOMING_CALL_CODE);
        setField(underTestGenerator, "random", randomMock);
    }

    @Test
    void testGenerateCallTypeShouldReturnIncomingCallTypeCodeString() {
        // Arrange
        // Act
        when(randomMock.nextDouble()).thenReturn(INCOMING_CALL_CHANCE - 0.0001);
        String incomingCallTypeCode = underTestGenerator.generateCallType();
        // Assert
        assertThat(incomingCallTypeCode).isNotNull().isEqualTo(INCOMING_CALL_CODE);
        verify(randomMock, times(1)).nextDouble();
    }

    @Test
    void testGenerateCallTypeShouldReturnOutcomingCallTypeCodeString() {
        // Arrange
        // Act
        when(randomMock.nextDouble()).thenReturn(INCOMING_CALL_CHANCE + 0.0001);
        String outcomingCallTypeCode = underTestGenerator.generateCallType();
        // Assert
        assertThat(outcomingCallTypeCode).isNotNull().isEqualTo(OUTCOMING_CALL_CODE);
        verify(randomMock, times(1)).nextDouble();
    }
}
