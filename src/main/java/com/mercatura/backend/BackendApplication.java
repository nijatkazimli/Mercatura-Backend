package com.mercatura.backend;

import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Role;
import com.mercatura.backend.repository.RoleRepository;
import com.mercatura.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository,
						  UserRepository userRepository,
						  PasswordEncoder passwordEncoder) {
		return args -> {
			if (roleRepository.findByAuthority("USER").isEmpty()) {
				roleRepository.save(new Role("USER"));
			}
			if (roleRepository.findByAuthority("MERCHANDISER").isEmpty()) {
				roleRepository.save(new Role("MERCHANDISER"));
			}
			if (roleRepository.findByAuthority("ADMIN").isEmpty()) {
				Role adminRole = roleRepository.save(new Role("ADMIN"));
				Set<Role> roles = new HashSet<>();
				roles.add(adminRole);

				ApplicationUser admin = new ApplicationUser(
						"admin",
						"admin",
						"admin",
						passwordEncoder.encode("password"),
						roles
				);
				userRepository.save(admin);
			}
		};
	}
}
