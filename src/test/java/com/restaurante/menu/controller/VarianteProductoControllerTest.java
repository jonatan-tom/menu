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
import com.restaurante.menu.entity.VarianteProducto;
import com.restaurante.menu.service.VarianteProductoService;

@ExtendWith(MockitoExtension.class)
class VarianteProductoControllerTest {

	@Mock
	private VarianteProductoService varianteService;

	@InjectMocks
	private VarianteProductoController varianteController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = standaloneSetup(varianteController).build();
	}

	@Test
	void listarVariantesPorProductoDevuelveLista() throws Exception {
		VarianteProducto v = new VarianteProducto(1, 1, "Individual", new BigDecimal("10000"), true);
		when(varianteService.listarPorProducto(1, false)).thenReturn(List.of(v));

		mockMvc.perform(get("/api/productos/1/variantes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].nombre").value("Individual"));
	}

	@Test
	void crearVarianteDevuelveVarianteCreada() throws Exception {
		VarianteProducto entrada = new VarianteProducto(null, 1, "Familiar", new BigDecimal("15000"), true);
		VarianteProducto salida = new VarianteProducto(1, 1, "Familiar", new BigDecimal("15000"), true);
		when(varianteService.crear(eq(1), any(VarianteProducto.class))).thenReturn(salida);

		mockMvc.perform(post("/api/productos/1/variantes")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void obtenerVariantePorIdDevuelveVariante() throws Exception {
		VarianteProducto v = new VarianteProducto(1, 1, "Individual", new BigDecimal("10000"), true);
		when(varianteService.obtener(1)).thenReturn(v);

		mockMvc.perform(get("/api/variantes/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Individual"));
	}

	@Test
	void actualizarVarianteDevuelveVarianteActualizada() throws Exception {
		VarianteProducto entrada = new VarianteProducto(null, 1, "Individual", new BigDecimal("11000"), true);
		VarianteProducto salida = new VarianteProducto(1, 1, "Individual", new BigDecimal("11000"), true);
		when(varianteService.actualizar(eq(1), any(VarianteProducto.class))).thenReturn(salida);

		mockMvc.perform(put("/api/variantes/1")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.precio").value(11000));
	}

	@Test
	void eliminarVarianteDevuelveNoContent() throws Exception {
		doNothing().when(varianteService).eliminar(1);

		mockMvc.perform(delete("/api/variantes/1"))
				.andExpect(status().isNoContent());
	}
}
