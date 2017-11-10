package com.dbs.escaperoom.mediator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MediatorApplication {

	public static void main(String[] args) {
		try {
			SSLUtil.turnOffSslChecking();
		}
		catch(Exception ex){
			System.out.println("Failed turning off ssl check, let's hope everything will keep working");
		}
		SpringApplication.run(MediatorApplication.class, args);
	}
}
