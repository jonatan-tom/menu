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
import com.restaurante.menu.entity.Producto;
import com.restaurante.menu.service.ProductoService;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

	@Mock
	private ProductoService productoService;

	@InjectMocks
	private ProductoController productoController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = standaloneSetup(productoController).build();
	}

	@Test
	void listarProductosDevuelveLista() throws Exception {
		Producto p = new Producto(1, 1, "Hamburguesa", "Clásica", new BigDecimal("10000"), null, true);
		when(productoService.listar(null, false)).thenReturn(List.of(p));

		mockMvc.perform(get("/api/productos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].nombre").value("Hamburguesa"));
	}

	@Test
	void obtenerProductoPorIdDevuelveProducto() throws Exception {
		Producto p = new Producto(1, 1, "Hamburguesa", "Clásica", new BigDecimal("10000"), null, true);
		when(productoService.obtener(1)).thenReturn(p);

		mockMvc.perform(get("/api/productos/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.nombre").value("Hamburguesa"));
	}

	@Test
	void crearProductoDevuelveProductoCreado() throws Exception {
		Producto entrada = new Producto(null, 1, "Pizza", "Familiar", new BigDecimal("15000"), null, true);
		Producto salida = new Producto(1, 1, "Pizza", "Familiar", new BigDecimal("15000"), null, true);
		when(productoService.crear(any(Producto.class))).thenReturn(salida);

		mockMvc.perform(post("/api/productos")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.nombre").value("Pizza"));
	}

	@Test
	void actualizarProductoDevuelveProductoActualizado() throws Exception {
		Producto entrada = new Producto(null, 1, "Pizza", "Familiar", new BigDecimal("16000"), null, true);
		Producto salida = new Producto(1, 1, "Pizza", "Familiar", new BigDecimal("16000"), null, true);
		when(productoService.actualizar(eq(1), any(Producto.class))).thenReturn(salida);

		mockMvc.perform(put("/api/productos/1")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.precio").value(16000));
	}

	@Test
	void cambiarDisponibilidadProductoDevuelveProducto() throws Exception {
		Producto salida = new Producto(1, 1, "Hamburguesa", "Clásica", new BigDecimal("10000"), null, false);
		when(productoService.cambiarDisponibilidad(eq(1), eq(false))).thenReturn(salida);

		mockMvc.perform(patch("/api/productos/1/disponibilidad?disponible=false"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.disponible").value(false));
	}

	@Test
	void eliminarProductoDevuelveNoContent() throws Exception {
		doNothing().when(productoService).eliminar(1);

		mockMvc.perform(delete("/api/productos/1"))
				.andExpect(status().isNoContent());
	}
}
