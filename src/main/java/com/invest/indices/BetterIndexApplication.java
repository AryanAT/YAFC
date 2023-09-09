package com.invest.indices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class BetterIndexApplication {

	public static void main(String[] args) {
		SpringApplication.run(BetterIndexApplication.class, args);
	}

}
