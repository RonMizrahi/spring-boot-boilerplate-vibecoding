package com.template.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KafkaConfig.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class KafkaConfigTest {

    // Mock Kafka properties to avoid actual Kafka connection
    @MockBean
    private org.springframework.boot.autoconfigure.kafka.KafkaProperties kafkaProperties;

    @Test
    void testKafkaConfigContextLoads() {
        // This test verifies that the application context loads successfully
        // with Kafka configuration enabled
        assertTrue(true);
    }

    @Test
    void testProducerFactoryConfiguration() {
        // Given
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
        
        // When
        ProducerFactory<String, String> stringProducerFactory = kafkaConfig.stringProducerFactory();
        ProducerFactory<String, Object> jsonProducerFactory = kafkaConfig.jsonProducerFactory();
        
        // Then
        assertNotNull(stringProducerFactory);
        assertNotNull(jsonProducerFactory);
        
        // Verify string producer configuration
        var stringConfigs = stringProducerFactory.getConfigurationProperties();
        assertEquals(StringSerializer.class, stringConfigs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(StringSerializer.class, stringConfigs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        assertEquals("all", stringConfigs.get(ProducerConfig.ACKS_CONFIG));
        assertEquals(3, stringConfigs.get(ProducerConfig.RETRIES_CONFIG));
        assertEquals(true, stringConfigs.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        
        // Verify JSON producer configuration
        var jsonConfigs = jsonProducerFactory.getConfigurationProperties();
        assertEquals(StringSerializer.class, jsonConfigs.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(JsonSerializer.class, jsonConfigs.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        assertEquals("all", jsonConfigs.get(ProducerConfig.ACKS_CONFIG));
        assertEquals(3, jsonConfigs.get(ProducerConfig.RETRIES_CONFIG));
        assertEquals(true, jsonConfigs.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
    }

    @Test
    void testConsumerFactoryConfiguration() {
        // Given
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
        
        // When
        ConsumerFactory<String, String> stringConsumerFactory = kafkaConfig.stringConsumerFactory();
        ConsumerFactory<String, Object> jsonConsumerFactory = kafkaConfig.jsonConsumerFactory();
        
        // Then
        assertNotNull(stringConsumerFactory);
        assertNotNull(jsonConsumerFactory);
        
        // Verify string consumer configuration
        var stringConfigs = stringConsumerFactory.getConfigurationProperties();
        assertEquals(StringDeserializer.class, stringConfigs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(StringDeserializer.class, stringConfigs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals("earliest", stringConfigs.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        assertEquals(false, stringConfigs.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
        
        // Verify JSON consumer configuration
        var jsonConfigs = jsonConsumerFactory.getConfigurationProperties();
        assertEquals(StringDeserializer.class, jsonConfigs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(ErrorHandlingDeserializer.class, jsonConfigs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        assertEquals(JsonDeserializer.class, jsonConfigs.get(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS));
        assertEquals("com.template.event", jsonConfigs.get(JsonDeserializer.TRUSTED_PACKAGES));
        assertEquals("earliest", jsonConfigs.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        assertEquals(false, jsonConfigs.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
    }

    @Test
    void testKafkaTemplateConfiguration() {
        // Given
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
        
        // When
        KafkaTemplate<String, String> stringKafkaTemplate = kafkaConfig.stringKafkaTemplate();
        KafkaTemplate<String, Object> jsonKafkaTemplate = kafkaConfig.jsonKafkaTemplate();
        
        // Then
        assertNotNull(stringKafkaTemplate);
        assertNotNull(jsonKafkaTemplate);
        
        // Verify that templates use the correct producer factories
        assertNotNull(stringKafkaTemplate.getProducerFactory());
        assertNotNull(jsonKafkaTemplate.getProducerFactory());
    }

    @Test
    void testKafkaObjectMapperConfiguration() {
        // Given
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
        
        // When
        var objectMapper = kafkaConfig.kafkaObjectMapper();
        
        // Then
        assertNotNull(objectMapper);
        
        // Verify that JavaTimeModule is registered for LocalDateTime support
        assertTrue(objectMapper.getRegisteredModuleIds().contains("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule"));
    }
}
