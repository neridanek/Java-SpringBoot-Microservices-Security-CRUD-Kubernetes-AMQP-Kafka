package com.example.demo;

import com.example.demo.shape.ShapeRepository;
import com.example.demo.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(
        scanBasePackages = {
                "com.example.shape",
                "com.example.amqp"
        }
)
@EnableDiscoveryClient
@EnableFeignClients(
        basePackages = "com.example.clients"
)
@PropertySources({
        @PropertySource("classpath:clients-${spring.profiles.active}.properties")
})
public class ShapeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShapeApplication.class,args);
    }

//    @Bean
//    CommandLineRunner commandLineRunner(
//            RabbitMQMessageProducer producer,
//            NotificationConfig notificationConfig
//    ) {
//        return args -> {
//            producer.publish(new Person("Wiktor",21),
//                    notificationConfig.getInternalExchange()
//                    ,notificationConfig.getInternalNotificationRoutingKey());
//        };
//    }

//    record Person(String name,int age){}

    @Bean
    CommandLineRunner runner(
            ShapeRepository shapeRepository,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository
    ){
        return args -> {
//			createRandomShape(shapeRepository,passwordEncoder, User.Role.CREATOR,userRepository);
//			createRandomShape(shapeRepository,passwordEncoder, User.Role.ADMIN,userRepository);
        };
    }

//	private static void createRandomShape(ShapeRepository shapeRepository,
//										  PasswordEncoder passwordEncoder,
//										  User.Role userRole,
//										  UserRepository userRepository
//										  ){
//		var faker = new Faker();
//		User user = User.builder().login("wiktorn").build();
//		userRepository.save(user);
//		Double radius = faker.number().randomDouble(2,2,3);
//		Shape circle = Circle.builder()
//				.parameters(List.of(radius))
//				.type(Shape.ShapeType.CIRCLE)
//				.createdBy(user)
//				.createdAt(LocalDate.now())
//				.build();
//		shapeRepository.save(circle);
//		System.out.println(circle);
//		System.out.println(user);
//	}
}