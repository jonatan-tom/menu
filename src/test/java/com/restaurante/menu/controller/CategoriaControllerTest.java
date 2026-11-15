package com.restaurante.menu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.menu.entity.Categoria;
import com.restaurante.menu.service.CategoriaService;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

	@Mock
	private CategoriaService categoriaService;

	@InjectMocks
	private CategoriaController categoriaController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = standaloneSetup(categoriaController).build();
	}

	@Test
	void listarCategoriasDevuelveLista() throws Exception {
		Categoria c = new Categoria(1, "Hamburguesas", 1, true);
		when(categoriaService.listar(false)).thenReturn(List.of(c));

		mockMvc.perform(get("/api/categorias"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].nombre").value("Hamburguesas"));
	}

	@Test
	void obtenerCategoriaPorIdDevuelveCategoria() throws Exception {
		Categoria c = new Categoria(1, "Hamburguesas", 1, true);
		when(categoriaService.obtener(1)).thenReturn(c);

		mockMvc.perform(get("/api/categorias/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Hamburguesas"));
	}

	@Test
	void crearCategoriaDevuelveCategoriaCreada() throws Exception {
		Categoria entrada = new Categoria(null, "Bebidas", 2, true);
		Categoria salida = new Categoria(2, "Bebidas", 2, true);
		when(categoriaService.crear(any(Categoria.class))).thenReturn(salida);

		mockMvc.perform(post("/api/categorias")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.nombre").value("Bebidas"));
	}

	@Test
	void actualizarCategoriaDevuelveCategoriaActualizada() throws Exception {
		Categoria entrada = new Categoria(null, "Bebidas", 2, false);
		Categoria salida = new Categoria(1, "Bebidas", 2, false);
		when(categoriaService.actualizar(eq(1), any(Categoria.class))).thenReturn(salida);

		mockMvc.perform(put("/api/categorias/1")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activa").value(false));
	}

	@Test
	void eliminarCategoriaDevuelveNoContent() throws Exception {
		doNothing().when(categoriaService).eliminar(1);

		mockMvc.perform(delete("/api/categorias/1"))
				.andExpect(status().isNoContent());
	}
}
