package com.derso.disparotimeouts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("com.derso.controlesessao.persistencia")
@EntityScan("com.derso.controlesessao.persistencia")
@ComponentScan(basePackages = {
		"com.derso.disparotimeouts",
		"com.derso.controlesessao.persistencia",
		"com.derso.controlesessao.trava"
})
public class DisparoTimeoutsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DisparoTimeoutsApplication.class, args);
	}

}
