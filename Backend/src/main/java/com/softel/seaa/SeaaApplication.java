package com.softel.seaa;

import com.softel.seaa.Controller.SeaaManager.FileSystem;
import com.softel.seaa.Entity.Extra.Roles;
import com.softel.seaa.Entity.Rol;
import com.softel.seaa.Entity.Specialist;
import com.softel.seaa.Entity.User;
import com.softel.seaa.Services.Contract.RolService;
import com.softel.seaa.Services.Contract.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication
public class SeaaApplication {
	@Autowired
	private UserService userService;
	@Autowired
	private RolService rolService;

	public static void main(String[] args) throws Exception {
		DatabaseInitializer.createDatabase();
		SpringApplication.run(SeaaApplication.class, args);
	}

	@PostConstruct
	public void initData() throws IOException {
		long timeMillis = System.currentTimeMillis();
		System.out.println("[ " + LocalDateTime.now() + "] Starting configuration of system SOFTEL...");

		createAdminIfNotExist();
		FileSystem.newFileSystem();

		System.out.println("[ " + LocalDateTime.now() + "] **FINISHED** configuration in "
				+ ((double) (System.currentTimeMillis() - timeMillis) / 1000) + " seconds.");
	}

	 private void createAdminIfNotExist() {
		boolean notFoundUser = userService.findByNameContains("Admin").isEmpty();

		if (notFoundUser){
			System.out.println("[ " + LocalDateTime.now() + "] Inserting Admin and roles...");
			User admin = new User();
			admin.setName("Admin");
			admin.setEnabled(true);
			admin.setLastName("Admin Admin");
			admin.setPhoneNumber("11111111");
			admin.setPassword(new BCryptPasswordEncoder().encode("Admin#*01"));

			if (admin.getRoles().isEmpty()) {
				admin.getRoles().add(new Rol(Roles.ROLE_ADMIN));
				admin.getRoles().add(new Rol(Roles.ROLE_USER));
				admin.getRoles().add(new Rol(Roles.ROLE_EXPERT));
				userService.save(admin);
			}
			System.out.println("[ " + LocalDateTime.now() + "] Inserted Data...");
		}

	}
}
