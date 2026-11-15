package com.restaurante.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.menu.entity.Mesa;
import com.restaurante.menu.repository.MesaRepository;

@ExtendWith(MockitoExtension.class)
class MesaServiceTest {

	@Mock
	private MesaRepository mesaRepository;

	@InjectMocks
	private MesaService mesaService;

	@Test
	void listarMesasDevuelveLista() {
		Mesa m1 = new Mesa(1, 1, "QR001", true);
		Mesa m2 = new Mesa(2, 2, "QR002", true);
		when(mesaRepository.listar()).thenReturn(List.of(m1, m2));

		List<Mesa> resultado = mesaService.listar();

		assertEquals(2, resultado.size());
		assertEquals("QR001", resultado.get(0).getCodigo());
	}

	@Test
	void obtenerMesaExistenteDevuelveMesa() {
		Mesa m = new Mesa(1, 1, "QR001", true);
		when(mesaRepository.buscarPorId(1)).thenReturn(Optional.of(m));

		Mesa resultado = mesaService.obtener(1);

		assertEquals(1, resultado.getId());
		assertEquals(1, resultado.getNumero());
		assertEquals("QR001", resultado.getCodigo());
	}

	@Test
	void obtenerMesaInexistenteLanzaExcepcion() {
		when(mesaRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> mesaService.obtener(99));
	}

	@Test
	void obtenerMesaPorCodigoExistenteDevuelveMesa() {
		Mesa m = new Mesa(1, 1, "QR001", true);
		when(mesaRepository.buscarPorCodigo("QR001")).thenReturn(Optional.of(m));

		Mesa resultado = mesaService.obtenerPorCodigo("QR001");

		assertEquals(1, resultado.getId());
		assertEquals("QR001", resultado.getCodigo());
	}

	@Test
	void obtenerMesaPorCodigoInexistenteLanzaExcepcion() {
		when(mesaRepository.buscarPorCodigo("QR999")).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> mesaService.obtenerPorCodigo("QR999"));
	}

	@Test
	void crearMesaConNumeroValidoGuardaYRetorna() {
		Mesa entrada = new Mesa(null, 5, null, true);
		Mesa salida = new Mesa(1, 5, "QR005", true);
		when(mesaRepository.insertar(any(Mesa.class))).thenAnswer(inv -> {
			Mesa mesa = inv.getArgument(0);
			mesa.setId(1);
			mesa.setCodigo("QR005");
			return mesa;
		});

		Mesa resultado = mesaService.crear(entrada);

		assertEquals(1, resultado.getId());
		assertEquals(5, resultado.getNumero());
		assertEquals("QR005", resultado.getCodigo());
		verify(mesaRepository).insertar(any(Mesa.class));
	}

	@Test
	void crearMesaConNumeroCeroLanzaExcepcion() {
		Mesa entrada = new Mesa(null, 0, null, true);

		assertThrows(IllegalArgumentException.class, () -> mesaService.crear(entrada));
		verify(mesaRepository, never()).insertar(any(Mesa.class));
	}

	@Test
	void crearMesaConNumeroNuloLanzaExcepcion() {
		Mesa entrada = new Mesa(null, null, null, true);

		assertThrows(IllegalArgumentException.class, () -> mesaService.crear(entrada));
	}

	@Test
	void actualizarMesaExistenteDevuelveActualizada() {
		Mesa existente = new Mesa(1, 1, "QR001", true);
		Mesa entrada = new Mesa(null, 3, null, false);
		Mesa salida = new Mesa(1, 3, "QR001", false);
		when(mesaRepository.buscarPorId(1)).thenReturn(Optional.of(existente));
		when(mesaRepository.buscarPorId(1)).thenReturn(Optional.of(salida));

		Mesa resultado = mesaService.actualizar(1, entrada);

		assertEquals(1, resultado.getId());
		assertEquals(3, resultado.getNumero());
		assertEquals(false, resultado.getActiva());
	}

	@Test
	void actualizarMesaInexistenteLanzaExcepcion() {
		Mesa entrada = new Mesa(null, 5, null, true);
		when(mesaRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> mesaService.actualizar(99, entrada));
	}

	@Test
	void regenerarCodigoMesaExistenteDevuelveConNuevoCodigo() {
		Mesa existente = new Mesa(1, 1, "QR001", true);
		Mesa salida = new Mesa(1, 1, "QR999", true);
		when(mesaRepository.buscarPorId(1)).thenReturn(Optional.of(existente));
		when(mesaRepository.buscarPorId(1)).thenReturn(Optional.of(salida));

		Mesa resultado = mesaService.regenerarCodigo(1);

		assertEquals(1, resultado.getId());
		assertEquals("QR999", resultado.getCodigo());
	}

	@Test
	void regenerarCodigoMesaInexistenteLanzaExcepcion() {
		when(mesaRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> mesaService.regenerarCodigo(99));
	}

	@Test
	void eliminarMesaExistenteNoLanzaExcepcion() {
		Mesa existente = new Mesa(1, 1, "QR001", true);
		when(mesaRepository.buscarPorId(1)).thenReturn(Optional.of(existente));

		mesaService.eliminar(1); // no debe lanzar excepción
	}

	@Test
	void eliminarMesaInexistenteLanzaExcepcion() {
		when(mesaRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> mesaService.eliminar(99));
		verify(mesaRepository, never()).eliminar(any());
	}
}
