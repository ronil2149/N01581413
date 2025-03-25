package org.example.class_activity_10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("hello")

public class ClassActivity10Application {

	public static void main(String[] args) {
		SpringApplication.run(ClassActivity10Application.class, args);
	}

	@GetMapping("/{firstName}/{lastName}")
	public String hello(@PathVariable("firstName") String firstName, @PathVariable("lastName") String lastName) {
		return String.format("{\"message\":\"Hello %s %s\"}", firstName, lastName);
	}
}
