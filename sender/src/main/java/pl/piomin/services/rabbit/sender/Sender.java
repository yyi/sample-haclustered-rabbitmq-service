package pl.piomin.services.rabbit.sender;

import java.util.Random;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import pl.piomin.services.rabbit.commons.message.Order;
import pl.piomin.services.rabbit.commons.message.OrderType;

@SpringBootApplication
public class Sender {

	private static Logger logger = Logger.getLogger("Sender");

	@Autowired
	RabbitTemplate template;

	public static void main(String[] args) {
		SpringApplication.run(Sender.class, args);
	}

	@PostConstruct
	public void send() {
		for (int i = 0; i < 100000; i++) {
			int id = new Random().nextInt(100000);
			template.convertAndSend("routing_demo",new Order(id, "TEST"+id, OrderType.values()[(id%2)]));
		}
		logger.info("Sending completed.");
	}

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root123");
        connectionFactory.setAddresses("master:5672,slave01:5672,slave02:5672");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate template() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange("exchange_demo");
        return rabbitTemplate;
    }

}
