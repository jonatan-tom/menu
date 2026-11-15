package com.restaurante.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.restaurante.menu.entity.Adicional;
import com.restaurante.menu.entity.Categoria;
import com.restaurante.menu.repository.AdicionalRepository;
import com.restaurante.menu.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class AdicionalServiceTest {

	@Mock
	private AdicionalRepository adicionalRepository;

	@Mock
	private CategoriaRepository categoriaRepository;

	@InjectMocks
	private AdicionalService adicionalService;

	@Test
	void listarAdicionalesDevuelveLista() {
		Adicional a1 = new Adicional(1, 1, "Queso extra", new BigDecimal("2000"), true);
		Adicional a2 = new Adicional(2, 1, "Tocina", new BigDecimal("3000"), true);
		when(adicionalRepository.listar(null, false)).thenReturn(List.of(a1, a2));

		List<Adicional> resultado = adicionalService.listar(null, false);

		assertEquals(2, resultado.size());
		assertEquals("Queso extra", resultado.get(0).getNombre());
	}

	@Test
	void obtenerAdicionalExistenteDevuelveAdicional() {
		Adicional a = new Adicional(1, 1, "Queso extra", new BigDecimal("2000"), true);
		when(adicionalRepository.buscarPorId(1)).thenReturn(Optional.of(a));

		Adicional resultado = adicionalService.obtener(1);

		assertEquals(1, resultado.getId());
		assertEquals("Queso extra", resultado.getNombre());
	}

	@Test
	void obtenerAdicionalInexistenteLanzaExcepcion() {
		when(adicionalRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> adicionalService.obtener(99));
	}

	@Test
	void crearAdicionalConPrecioNuloAsignaCero() {
		Adicional entrada = new Adicional(null, 1, "Tocina", null, true);
		Adicional salida = new Adicional(1, 1, "Tocina", new BigDecimal("0"), true);
		when(categoriaRepository.buscarPorId(1)).thenReturn(Optional.of(new Categoria(1, "Hamburguesas", 1, true)));
		when(adicionalRepository.insertar(any(Adicional.class))).thenAnswer(inv -> {
			Adicional adicional = inv.getArgument(0);
			adicional.setId(1);
			return adicional;
		});

		Adicional resultado = adicionalService.crear(entrada);

		assertEquals(1, resultado.getId());
		assertEquals(BigDecimal.ZERO, resultado.getPrecio());
		verify(adicionalRepository).insertar(any(Adicional.class));
	}

	@Test
	void crearAdicionalConPrecioNegativoLanzaExcepcion() {
		Adicional entrada = new Adicional(null, 1, "Tocina", new BigDecimal("-1000"), true);

		assertThrows(IllegalArgumentException.class, () -> adicionalService.crear(entrada));
		verify(adicionalRepository, never()).insertar(any(Adicional.class));
	}

	@Test
	void crearAdicionalConCategoriaInexistenteLanzaExcepcion() {
		Adicional entrada = new Adicional(null, 99, "Tocina", new BigDecimal("3000"), true);
		when(categoriaRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> adicionalService.crear(entrada));
		verify(adicionalRepository, never()).insertar(any(Adicional.class));
	}

	@Test
	void actualizarAdicionalExistenteDevuelveActualizado() {
		Adicional existente = new Adicional(1, 1, "Queso extra", new BigDecimal("2000"), true);
		Adicional entrada = new Adicional(null, 1, "Queso Doble", new BigDecimal("3500"), true);
		Adicional salida = new Adicional(1, 1, "Queso Doble", new BigDecimal("3500"), true);
		when(adicionalRepository.buscarPorId(1)).thenReturn(Optional.of(existente));
		when(categoriaRepository.buscarPorId(1)).thenReturn(Optional.of(new Categoria(1, "Hamburguesas", 1, true)));
		when(adicionalRepository.buscarPorId(1)).thenReturn(Optional.of(salida));

		Adicional resultado = adicionalService.actualizar(1, entrada);

		assertEquals(1, resultado.getId());
		assertEquals("Queso Doble", resultado.getNombre());
		assertEquals(new BigDecimal("3500"), resultado.getPrecio());
	}

	@Test
	void actualizarAdicionalInexistenteLanzaExcepcion() {
		Adicional entrada = new Adicional(null, 1, "Nuevo", new BigDecimal("1000"), true);
		when(adicionalRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> adicionalService.actualizar(99, entrada));
	}

	@Test
	void eliminarAdicionalExistenteNoLanzaExcepcion() {
		Adicional existente = new Adicional(1, 1, "Queso extra", new BigDecimal("2000"), true);
		when(adicionalRepository.buscarPorId(1)).thenReturn(Optional.of(existente));

		adicionalService.eliminar(1); // no debe lanzar excepción
	}

	@Test
	void eliminarAdicionalInexistenteLanzaExcepcion() {
		when(adicionalRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> adicionalService.eliminar(99));
		verify(adicionalRepository, never()).eliminar(any());
	}
}
