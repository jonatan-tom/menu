package com.restaurante.menu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurante.menu.dto.NuevoPedidoRequest;
import com.restaurante.menu.dto.NuevoPedidoRequest.Item;
import com.restaurante.menu.dto.PedidoCompleto;
import com.restaurante.menu.entity.Pedido;
import com.restaurante.menu.service.PedidoService;

@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

	@Mock
	private PedidoService pedidoService;

	@InjectMocks
	private PedidoController pedidoController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = standaloneSetup(pedidoController).build();
	}

	@Test
	void listarPedidosActivosDevuelveLista() throws Exception {
		Pedido pedido = new Pedido(1, 1, "MESA", "Juan", "3000000000", "Cra 1",
				LocalDateTime.now(), "RECIBIDO", new BigDecimal("10000"), null);
		PedidoCompleto completo = new PedidoCompleto(pedido, 1, List.of());
		when(pedidoService.listarActivos()).thenReturn(List.of(completo));

		mockMvc.perform(get("/api/pedidos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].pedido.id").value(1))
				.andExpect(jsonPath("$[0].pedido.estado").value("RECIBIDO"));
	}

	@Test
	void listarPedidosPorEstadoDevuelveListaFiltrada() throws Exception {
		Pedido pedido = new Pedido(1, 1, "MESA", "Juan", "3000000000", "Cra 1",
				LocalDateTime.now(), "ENTREGADO", new BigDecimal("10000"), null);
		PedidoCompleto completo = new PedidoCompleto(pedido, 1, List.of());
		when(pedidoService.listarPorEstado("ENTREGADO")).thenReturn(List.of(completo));

		mockMvc.perform(get("/api/pedidos?estado=ENTREGADO"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].pedido.estado").value("ENTREGADO"));
	}

	@Test
	void obtenerPedidoPorIdDevuelvePedido() throws Exception {
		Pedido pedido = new Pedido(1, 1, "MESA", "Juan", "3000000000", "Cra 1",
				LocalDateTime.now(), "RECIBIDO", new BigDecimal("10000"), null);
		PedidoCompleto completo = new PedidoCompleto(pedido, 1, List.of());
		when(pedidoService.obtener(1)).thenReturn(completo);

		mockMvc.perform(get("/api/pedidos/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pedido.id").value(1));
	}

	@Test
	void crearPedidoDevuelvePedidoCreado() throws Exception {
		NuevoPedidoRequest request = new NuevoPedidoRequest(
				"MESA", "QR001", "Juan", "3000000000", "Cra 1", "Sin cebolla",
				List.of(new Item(1, null, 2, null, List.of())));
		Pedido pedido = new Pedido(1, 1, "MESA", "Juan", "3000000000", "Cra 1",
				LocalDateTime.now(), "RECIBIDO", new BigDecimal("20000"), null);
		PedidoCompleto completo = new PedidoCompleto(pedido, 1, List.of());
		when(pedidoService.crear(any(NuevoPedidoRequest.class))).thenReturn(completo);

		mockMvc.perform(post("/api/pedidos")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.pedido.id").value(1))
				.andExpect(jsonPath("$.pedido.total").value(20000));
	}

	@Test
	void cambiarEstadoPedidoDevuelvePedidoActualizado() throws Exception {
		Pedido pedido = new Pedido(1, 1, "MESA", "Juan", "3000000000", "Cra 1",
				LocalDateTime.now(), "ENTREGADO", new BigDecimal("10000"), null);
		PedidoCompleto completo = new PedidoCompleto(pedido, 1, List.of());
		when(pedidoService.cambiarEstado(eq(1), eq("ENTREGADO"))).thenReturn(completo);

		mockMvc.perform(patch("/api/pedidos/1/estado?estado=ENTREGADO"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pedido.estado").value("ENTREGADO"));
	}
}
