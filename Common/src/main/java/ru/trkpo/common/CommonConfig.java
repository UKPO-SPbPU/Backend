package ru.trkpo.common;

import jakarta.jms.JMSException;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJpaRepositories(basePackages = {"ru.trkpo.common"})
@EntityScan(basePackages = {"ru.trkpo.common"})
@ComponentScan(basePackages = {"ru.trkpo.common"})
@EnableCaching
public class CommonConfig {

    @Value("${spring.artemis.broker-url}")
    private String brokerUrl;

    @Value("${spring.artemis.user}")
    private String username;

    @Value("${spring.artemis.password}")
    private String password;

    @Bean(name = "MyAMQConnFactory")
    public ActiveMQConnectionFactory senderActiveMQConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        factory.setUser(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory(@Qualifier("MyAMQConnFactory") ActiveMQConnectionFactory senderActiveMqConnectionFactory) {
        return new CachingConnectionFactory(senderActiveMqConnectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(CachingConnectionFactory cachingConnectionFactory) {
        JmsTemplate template = new JmsTemplate(cachingConnectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate(JmsTemplate jmsTemplate) {
        return new JmsMessagingTemplate(jmsTemplate);
    }

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
