package pl.piomin.services.rabbit.listener;

import java.util.logging.Logger;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import pl.piomin.services.rabbit.commons.message.Order;

@SpringBootApplication
@EnableRabbit
public class Listener {

	private static Logger logger = Logger.getLogger("Listener");

	private Long timestamp;

	public static void main(String[] args) {
		SpringApplication.run(Listener.class, args);
	}

	@RabbitListener(queues = "queue_demo")
	public void onMessage(Order order) {
		if (timestamp == null)
			timestamp = System.currentTimeMillis();
		logger.info((System.currentTimeMillis() - timestamp) + " : " + order.toString());
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setUsername("root");
		connectionFactory.setPassword("root123");
		connectionFactory.setAddresses("master:5672,slave01:5672,slave02:5672");
		connectionFactory.setChannelCacheSize(10);
		return connectionFactory;
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setConcurrentConsumers(10);
		factory.setMaxConcurrentConsumers(20);
		return factory;
	}

//	@Bean
//	public Queue queue() {
//		return new Queue("queue_demo");
//	}

}
