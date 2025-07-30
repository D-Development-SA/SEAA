package com.softel.seaa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softel.seaa.Entity.Seaa;
import com.softel.seaa.Entity.Specialist;
import com.softel.seaa.Entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SeaaApplicationTests {

	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	public void testCreateUserWithNullValues() {
		User user = new User();

		assertThrows(MethodArgumentNotValidException.class, () -> {
			mockMvc.perform(post("/create")
							.contentType(MediaType.APPLICATION_JSON)
							.content(asJsonString(user)))
					.andExpect(status().isBadRequest());
		});
	}

	@Test
	public void testCreateUserWithoutRole() throws Exception {
		User user = new User();
		user.setName("John");
		user.setLastName("Doe");
		user.setPassword("password123");

		mockMvc.perform(post("/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(user)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.roles[0].name").value("ROLE_USER"));
	}

	@Test
	public void testCreateUserSuccess() throws Exception {
		User user = new User();
		user.setName("John");
		user.setLastName("Doe");
		user.setPassword("password123");

		Specialist specialist = new Specialist();
		specialist.setSeaaList(Set.of(new Seaa(), new Seaa()));
		user.setSpecialist(specialist);

		mockMvc.perform(post("/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(user)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("John"))
				.andExpect(jsonPath("$.roles[0].name").value("ROLE_USER"))
				.andExpect(jsonPath("$.enabled").value(true));
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
