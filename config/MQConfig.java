package com.tag.biometric.ifService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class MQConfig {

    public static final String IF_QUEUE = "if_processor_queue";
    public static final String EXCHANGE = "processor_exchange";
    public static final String ROUTING_KEY = "processor_routingkey";
    public static final String DLX_EXCHANGE = "processor.dlx";
    public static final String IF_DLQ = "if_processor_queue.dlq";

    @Bean
    public Queue ifQueue() {
        return QueueBuilder.durable(IF_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", IF_DLQ)
                .build();
    }

    @Bean
    public DirectExchange dlx() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean
    public Queue ifDlq() {
        return QueueBuilder.durable(IF_DLQ).build();
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding ifBinding() {
        return BindingBuilder.bind(ifQueue())
                .to(exchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding ifDlqBinding() {
        return BindingBuilder.bind(ifDlq())
                .to(dlx())
                .with(IF_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory jsonListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setAdviceChain(retryInterceptor());
        return factory;
    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1000, 2.0, 10000)
                .build();
    }
}