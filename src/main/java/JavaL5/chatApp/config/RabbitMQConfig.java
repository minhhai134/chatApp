package JavaL5.chatApp.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    /*
    exchange -> queue -> binding
     */

    // Define Email Queue
    @Bean
    public Queue emailQueue(){
        return QueueBuilder.durable("emailQueue")
                .withArgument("x-dead-letter-exchange", "email-dlx-exchange") // Forward faild message to DLX "key - value"
//                .withArgument()
                .withArgument("x-message-ttl", 60000) // Time to live for the messages in the original queue (optional)
                .build();
    }

    // Define exchange for email queue
    @Bean
    public DirectExchange exchange(){
        return new DirectExchange("email-exchange");
    }

    // Binding email queue to email exchange with routing key
    @Bean
    public Binding emailQueueBinding(Queue emailQueue, DirectExchange exchange){
        return BindingBuilder.bind(emailQueue).to(exchange).with("email-routing-key");
    }


    // Define the Dead Letter Queue (DLQ)
    @Bean
    public Queue emailDLQ(){
        return new Queue("emailDLQ");
    }

    // Define Dead Letter Exchange
    @Bean
    public DirectExchange deadLetterExchange(){
        return new DirectExchange("email-dlx-exchange");
    }

    @Bean
    public Binding emailDLQBinding(Queue emailDLQ, DirectExchange deadLetterExchange){
        return BindingBuilder.bind(emailDLQ).to(deadLetterExchange).with("email-dlx-routing-key");
    }
}
