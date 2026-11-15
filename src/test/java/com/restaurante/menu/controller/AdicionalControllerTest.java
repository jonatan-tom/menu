package com.restaurante.menu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.menu.entity.Adicional;
import com.restaurante.menu.service.AdicionalService;

@ExtendWith(MockitoExtension.class)
class AdicionalControllerTest {

	@Mock
	private AdicionalService adicionalService;

	@InjectMocks
	private AdicionalController adicionalController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = standaloneSetup(adicionalController).build();
	}

	@Test
	void listarAdicionalesDevuelveLista() throws Exception {
		Adicional a = new Adicional(1, 1, "Queso extra", new BigDecimal("2000"), true);
		when(adicionalService.listar(null, false)).thenReturn(List.of(a));

		mockMvc.perform(get("/api/adicionales"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].nombre").value("Queso extra"));
	}

	@Test
	void obtenerAdicionalPorIdDevuelveAdicional() throws Exception {
		Adicional a = new Adicional(1, 1, "Queso extra", new BigDecimal("2000"), true);
		when(adicionalService.obtener(1)).thenReturn(a);

		mockMvc.perform(get("/api/adicionales/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.precio").value(2000));
	}

	@Test
	void crearAdicionalDevuelveAdicionalCreado() throws Exception {
		Adicional entrada = new Adicional(null, 1, "Tocina", new BigDecimal("3000"), true);
		Adicional salida = new Adicional(1, 1, "Tocina", new BigDecimal("3000"), true);
		when(adicionalService.crear(any(Adicional.class))).thenReturn(salida);

		mockMvc.perform(post("/api/adicionales")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void actualizarAdicionalDevuelveAdicionalActualizado() throws Exception {
		Adicional entrada = new Adicional(null, 1, "Tocina", new BigDecimal("3500"), true);
		Adicional salida = new Adicional(1, 1, "Tocina", new BigDecimal("3500"), true);
		when(adicionalService.actualizar(eq(1), any(Adicional.class))).thenReturn(salida);

		mockMvc.perform(put("/api/adicionales/1")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.precio").value(3500));
	}

	@Test
	void eliminarAdicionalDevuelveNoContent() throws Exception {
		doNothing().when(adicionalService).eliminar(1);

		mockMvc.perform(delete("/api/adicionales/1"))
				.andExpect(status().isNoContent());
	}
}
