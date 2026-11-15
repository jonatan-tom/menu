package com.restaurante.menu.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.restaurante.menu.dto.CategoriaMenu;
import com.restaurante.menu.entity.Categoria;
import com.restaurante.menu.entity.Producto;
import com.restaurante.menu.service.MenuService;

@ExtendWith(MockitoExtension.class)
class MenuControllerTest {

	@Mock
	private MenuService menuService;

	@InjectMocks
	private MenuController menuController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = standaloneSetup(menuController).build();
	}

	@Test
	void obtenerMenuDevuelveListaDeCategorias() throws Exception {
		Categoria categoria = new Categoria(1, "Hamburguesas", 1, true);
		Producto producto = new Producto(1, 1, "Hamburguesa", "Clásica", new BigDecimal("10000"), null, true);
		CategoriaMenu categoriaMenu = new CategoriaMenu(categoria, List.of(
				new CategoriaMenu.ProductoMenu(producto, List.of())));
		when(menuService.obtenerMenu()).thenReturn(List.of(categoriaMenu));

		mockMvc.perform(get("/api/menu"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].categoria.nombre").value("Hamburguesas"))
				.andExpect(jsonPath("$[0].productos[0].producto.nombre").value("Hamburguesa"));
	}
}
