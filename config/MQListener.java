package com.tag.biometric.ifService.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.tag.biometric.ifService.config.webClient.tokenGenerator.TokenScheduler;
import com.tag.biometric.ifService.institutionOnboarding.dto.Processor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.rabbitmq.enabled", havingValue = "true")
@AllArgsConstructor
public class MQListener {

    private final ObjectMapper objectMapper;
    private final TokenScheduler tokenScheduler;

    @RabbitListener(queues = MQConfig.IF_QUEUE, containerFactory = "jsonListenerContainerFactory")
    public void processClientStatusUpdate(JsonNode processorRequest, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            log.info("Received processor update message: {}", processorRequest);

            Processor processorDto = mapJsonToProcessorDto(processorRequest);

            if (!isValidClient(processorDto)) {
                log.error("Invalid processor data received: {}", processorRequest);
                channel.basicNack(tag, false, false);
                return;
            }
            switch (processorDto.getStatus()) {
                case "ACT":
                    log.info("Processing ACTIVE processor with ID: {}", processorDto.getId());
                    if (processorDto.getProcessorCode().toLowerCase().contains("if")) {
                        log.info("Generate token for ACTIVE processor with IBAN module: {}", processorDto.getId());

                        tokenScheduler.generateBearerToken(processorDto)
                                .doOnSuccess(v -> {
                                    log.info("Successfully generated token for processor {}", processorDto.getId());
                                    acknowledgeMessage(channel, tag);
                                })
                                .doOnError(e -> log.error("Failed to generate token for processor {}: {}", processorDto.getId(), e.getMessage()))
                                .subscribe();
                    } else {
                        log.info("Disabling token for ACTIVE processor but without IBAN module: {}", processorDto.getId());
                        tokenScheduler.disableProcessor(processorDto.getId());
                    }
                    break;
                case "INA":
                    log.info("Disabling token for INACTIVE processor with ID: {}", processorDto.getId());
                    tokenScheduler.disableProcessor(processorDto.getId());
                    acknowledgeMessage(channel, tag);
                    break;
                default:
                    log.error("Invalid client status received: {}", processorDto.getStatus());
                    rejectMessage(channel, tag);
            }
        } catch (JsonProcessingException e) {
            log.error("JSON parsing error for message: {}", processorRequest, e);
            rejectMessage(channel, tag);
        } catch (Exception e) {
            log.error("Error processing processor update message: {}", e.getMessage(), e);
            try {
                channel.basicNack(tag, false, true); // Requeue the message for retry
            } catch (IOException ioException) {
                log.error("Failed to nack message", ioException);
            }
        }
    }

    private Processor mapJsonToProcessorDto(JsonNode jsonNode) throws JsonProcessingException {
        return objectMapper.treeToValue(jsonNode, Processor.class);
    }

    private boolean isValidClient(Processor processor) {
        return processor != null
                && processor.getId() != null
                && processor.getInstanceClientId() != null
                && processor.getInstanceUrl() != null;
    }

    private void acknowledgeMessage(Channel channel, long tag) {
        try {
            channel.basicAck(tag, false);
        } catch (IOException e) {
            log.error("Failed to acknowledge message", e);
        }
    }

    private void rejectMessage(Channel channel, long tag) {
        try {
            channel.basicNack(tag, false, false);
        } catch (IOException e) {
            log.error("Failed to reject message", e);
        }
    }
}
