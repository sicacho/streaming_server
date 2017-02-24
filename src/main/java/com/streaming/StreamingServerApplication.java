package com.streaming;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StreamingServerApplication {

	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv6Addresses","true");
		SpringApplication.run(StreamingServerApplication.class, args);
	}
}
