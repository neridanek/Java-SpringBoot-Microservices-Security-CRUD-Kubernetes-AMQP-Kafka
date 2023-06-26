package com.example.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(topics = "exampleTopic",groupId = " groupId")
    void listener(MessageRequest data){
      System.out.println("Listener received: " + data);
    }
}
