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
import com.restaurante.menu.entity.Mesa;
import com.restaurante.menu.service.MesaService;

@ExtendWith(MockitoExtension.class)
class MesaControllerTest {

	@Mock
	private MesaService mesaService;

	@InjectMocks
	private MesaController mesaController;

	private MockMvc mockMvc;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = standaloneSetup(mesaController).build();
	}

	@Test
	void listarMesasDevuelveLista() throws Exception {
		Mesa m = new Mesa(1, 1, "QR001", true);
		when(mesaService.listar()).thenReturn(List.of(m));

		mockMvc.perform(get("/api/mesas"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].numero").value(1));
	}

	@Test
	void obtenerMesaPorIdDevuelveMesa() throws Exception {
		Mesa m = new Mesa(1, 1, "QR001", true);
		when(mesaService.obtener(1)).thenReturn(m);

		mockMvc.perform(get("/api/mesas/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.codigo").value("QR001"));
	}

	@Test
	void obtenerMesaPorCodigoDevuelveMesa() throws Exception {
		Mesa m = new Mesa(1, 1, "QR001", true);
		when(mesaService.obtenerPorCodigo("QR001")).thenReturn(m);

		mockMvc.perform(get("/api/mesas/codigo/QR001"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.numero").value(1));
	}

	@Test
	void crearMesaDevuelveMesaCreada() throws Exception {
		Mesa entrada = new Mesa(null, 2, null, true);
		Mesa salida = new Mesa(2, 2, "QR002", true);
		when(mesaService.crear(any(Mesa.class))).thenReturn(salida);

		mockMvc.perform(post("/api/mesas")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.codigo").value("QR002"));
	}

	@Test
	void actualizarMesaDevuelveMesaActualizada() throws Exception {
		Mesa entrada = new Mesa(null, 3, null, false);
		Mesa salida = new Mesa(1, 3, "QR001", false);
		when(mesaService.actualizar(eq(1), any(Mesa.class))).thenReturn(salida);

		mockMvc.perform(put("/api/mesas/1")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(entrada)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activa").value(false));
	}

	@Test
	void regenerarCodigoMesaDevuelveMesa() throws Exception {
		Mesa salida = new Mesa(1, 1, "QR999", true);
		when(mesaService.regenerarCodigo(1)).thenReturn(salida);

		mockMvc.perform(patch("/api/mesas/1/codigo"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.codigo").value("QR999"));
	}

	@Test
	void eliminarMesaDevuelveNoContent() throws Exception {
		doNothing().when(mesaService).eliminar(1);

		mockMvc.perform(delete("/api/mesas/1"))
				.andExpect(status().isNoContent());
	}
}
