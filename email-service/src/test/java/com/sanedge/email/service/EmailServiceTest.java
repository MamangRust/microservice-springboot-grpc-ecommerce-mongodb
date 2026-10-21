package com.sanedge.email.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanedge.email.EmailMetrics;
import com.sanedge.email.EmailProperties;
import com.sanedge.email.EmailService;
import com.sanedge.email.JsonMap;

/**
 * Unit tests for the private {@code sendEmail} step of the Spring Boot email
 * worker. Redis / Kafka / mail are mocked; only the payload-gating logic is
 * under test (mirrors the previous Quarkus {@code Vertx/ReactiveMailer} test).
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    JavaMailSender mailSender;

    @Mock
    StringRedisTemplate redis;

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    EmailMetrics metrics;

    private final EmailProperties properties = new EmailProperties();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EmailService emailService;

    private JsonMap validEmailPayload;
    private JsonMap incompleteEmailPayload;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, redis, kafkaTemplate, metrics, properties,
                objectMapper, "no-reply@example.test");

        validEmailPayload = new JsonMap()
                .put("email", "test@example.com")
                .put("subject", "Test Subject")
                .put("body", "<h1>Test Body</h1>");

        incompleteEmailPayload = new JsonMap()
                .put("email", "test@example.com")
                .put("subject", "Test Subject");
    }

    @Test
    void sendEmail_shouldSucceed_withValidPayload() throws Exception {
        invokePrivateMethod("sendEmail", validEmailPayload);

        verify(mailSender).send(any(SimpleMailMessage.class));
        verify(metrics).recordSent();
    }

    @Test
    void sendEmail_shouldNotSend_whenPayloadIsIncomplete() throws Exception {
        invokePrivateMethod("sendEmail", incompleteEmailPayload);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldHandleNullEmail() throws Exception {
        JsonMap payloadWithNullEmail = new JsonMap()
                .put("email", null)
                .put("subject", "Test")
                .put("body", "Test Body");

        invokePrivateMethod("sendEmail", payloadWithNullEmail);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldHandleNullSubject() throws Exception {
        JsonMap payloadWithNullSubject = new JsonMap()
                .put("email", "test@example.com")
                .put("subject", null)
                .put("body", "Test Body");

        invokePrivateMethod("sendEmail", payloadWithNullSubject);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldHandleNullBody() throws Exception {
        JsonMap payloadWithNullBody = new JsonMap()
                .put("email", "test@example.com")
                .put("subject", "Test Subject")
                .put("body", null);

        invokePrivateMethod("sendEmail", payloadWithNullBody);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldCallMailerWithAnyMailObject() throws Exception {
        JsonMap customPayload = new JsonMap()
                .put("email", "recipient@example.com")
                .put("subject", "Custom Subject")
                .put("body", "<p>Custom HTML body</p>");

        invokePrivateMethod("sendEmail", customPayload);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldHandleEmptyPayload() throws Exception {
        invokePrivateMethod("sendEmail", new JsonMap());

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldHandleNullPayload() throws Exception {
        invokePrivateMethod("sendEmail", new Object[] { null });

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldHandleEmptyStrings() throws Exception {
        JsonMap emptyStringPayload = new JsonMap()
                .put("email", "")
                .put("subject", "")
                .put("body", "");

        invokePrivateMethod("sendEmail", emptyStringPayload);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_shouldPropagate_whenMailerFails() throws Exception {
        doThrow(new RuntimeException("SMTP connection failed"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // The private method is invoked reflectively, so the target exception is
        // wrapped in InvocationTargetException; assert on its cause.
        assertThatCode(() -> invokePrivateMethod("sendEmail", validEmailPayload))
                .hasRootCauseInstanceOf(RuntimeException.class)
                .hasRootCauseMessage("SMTP connection failed");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void recordCommittedPosition_shouldNotThrow() throws Exception {
        assertThatCode(() -> invokePrivateMethod("recordCommittedPosition", "topic", 0, 42L))
                .doesNotThrowAnyException();
    }

    /** Invokes a private method, using the exact declared parameter types. */
    private Object invokePrivateMethod(String methodName, Object... args) throws Exception {
        Class<?>[] paramTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i] == null ? null : args[i].getClass();
        }
        Method method = findMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(emailService, args);
    }

    private Method findMethod(String name, Class<?>[] paramTypes) throws NoSuchMethodException {
        for (Method candidate : EmailService.class.getDeclaredMethods()) {
            if (!candidate.getName().equals(name) || candidate.getParameterCount() != paramTypes.length) {
                continue;
            }
            Class<?>[] declared = candidate.getParameterTypes();
            boolean matches = true;
            for (int i = 0; i < declared.length && matches; i++) {
                if (!isCompatible(declared[i], paramTypes[i])) {
                    matches = false;
                }
            }
            if (matches) {
                return candidate;
            }
        }
        throw new NoSuchMethodException(name);
    }

    /** Matches a declared parameter type against the (possibly boxed) argument type. */
    private boolean isCompatible(Class<?> declared, Class<?> argType) {
        if (argType == null) {
            return !declared.isPrimitive();
        }
        if (declared.isAssignableFrom(argType)) {
            return true;
        }
        if (!declared.isPrimitive()) {
            return false;
        }
        return (declared == int.class && argType == Integer.class)
                || (declared == long.class && argType == Long.class)
                || (declared == boolean.class && argType == Boolean.class)
                || (declared == double.class && argType == Double.class)
                || (declared == float.class && argType == Float.class)
                || (declared == short.class && argType == Short.class)
                || (declared == byte.class && argType == Byte.class)
                || (declared == char.class && argType == Character.class);
    }
}
