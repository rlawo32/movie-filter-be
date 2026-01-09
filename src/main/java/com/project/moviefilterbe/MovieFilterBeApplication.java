package com.project.moviefilterbe;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// supabase db연결 잠시 막아둠 - ms 20260109
//@SpringBootApplication
//public class MovieFilterBeApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(MovieFilterBeApplication.class, args);
//    }
//
//}

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MovieFilterBeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieFilterBeApplication.class, args);
    }
}
