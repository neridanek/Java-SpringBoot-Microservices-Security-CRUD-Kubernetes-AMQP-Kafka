package com.example.notification.consumer;

import com.example.clients.notification.NotificationRequest;
import com.example.notification.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queues.notification")
    public void consumer(NotificationRequest notificatonRequest){
        log.info("Consumed {} from queue",notificatonRequest);
        notificationService.send(notificatonRequest);
    }
}
