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

import com.restaurante.menu.entity.Producto;
import com.restaurante.menu.entity.VarianteProducto;
import com.restaurante.menu.repository.ProductoRepository;
import com.restaurante.menu.repository.VarianteProductoRepository;

@ExtendWith(MockitoExtension.class)
class VarianteProductoServiceTest {

	@Mock
	private VarianteProductoRepository varianteRepository;

	@Mock
	private ProductoRepository productoRepository;

	@InjectMocks
	private VarianteProductoService varianteService;

	@Test
	void listarVariantesPorProductoExistenteDevuelveLista() {
		Producto producto = new Producto(1, 1, "Hamburguesa", "Clásica", new BigDecimal("10000"), null, true);
		VarianteProducto v1 = new VarianteProducto(1, 1, "Individual", new BigDecimal("10000"), true);
		VarianteProducto v2 = new VarianteProducto(2, 1, "Familiar", new BigDecimal("15000"), true);
		when(productoRepository.buscarPorId(1)).thenReturn(Optional.of(producto));
		when(varianteRepository.listarPorProducto(1, false)).thenReturn(List.of(v1, v2));

		List<VarianteProducto> resultado = varianteService.listarPorProducto(1, false);

		assertEquals(2, resultado.size());
		assertEquals("Individual", resultado.get(0).getNombre());
	}

	@Test
	void listarVariantesPorProductoInexistenteLanzaExcepcion() {
		when(productoRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> varianteService.listarPorProducto(99, false));
	}

	@Test
	void obtenerVarianteExistenteDevuelveVariante() {
		VarianteProducto v = new VarianteProducto(1, 1, "Individual", new BigDecimal("10000"), true);
		when(varianteRepository.buscarPorId(1)).thenReturn(Optional.of(v));

		VarianteProducto resultado = varianteService.obtener(1);

		assertEquals(1, resultado.getId());
		assertEquals("Individual", resultado.getNombre());
		assertEquals(new BigDecimal("10000"), resultado.getPrecio());
	}

	@Test
	void obtenerVarianteInexistenteLanzaExcepcion() {
		when(varianteRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> varianteService.obtener(99));
	}

	@Test
	void crearVarianteParaProductoExistenteGuardaYRetorna() {
		Producto producto = new Producto(1, 1, "Hamburguesa", "Clásica", new BigDecimal("10000"), null, true);
		VarianteProducto entrada = new VarianteProducto(null, 1, "Familiar", new BigDecimal("15000"), true);
		VarianteProducto salida = new VarianteProducto(1, 1, "Familiar", new BigDecimal("15000"), true);
		when(productoRepository.buscarPorId(1)).thenReturn(Optional.of(producto));
		when(varianteRepository.insertar(any(VarianteProducto.class))).thenAnswer(inv -> {
			VarianteProducto variante = inv.getArgument(0);
			variante.setId(1);
			return variante;
		});

		VarianteProducto resultado = varianteService.crear(1, entrada);

		assertEquals(1, resultado.getId());
		assertEquals("Familiar", resultado.getNombre());
		assertEquals(1, resultado.getProductoId());
		verify(varianteRepository).insertar(any(VarianteProducto.class));
	}

	@Test
	void crearVarianteParaProductoInexistenteLanzaExcepcion() {
		VarianteProducto entrada = new VarianteProducto(null, 99, "Familiar", new BigDecimal("15000"), true);
		when(productoRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> varianteService.crear(99, entrada));
		verify(varianteRepository, never()).insertar(any(VarianteProducto.class));
	}

	@Test
	void actualizarVarianteExistenteDevuelveActualizada() {
		VarianteProducto existente = new VarianteProducto(1, 1, "Individual", new BigDecimal("10000"), true);
		VarianteProducto entrada = new VarianteProducto(null, 1, "Individual Grande", new BigDecimal("12000"), true);
		VarianteProducto salida = new VarianteProducto(1, 1, "Individual Grande", new BigDecimal("12000"), true);
		when(varianteRepository.buscarPorId(1)).thenReturn(Optional.of(existente));
		when(varianteRepository.buscarPorId(1)).thenReturn(Optional.of(salida));

		VarianteProducto resultado = varianteService.actualizar(1, entrada);

		assertEquals(1, resultado.getId());
		assertEquals("Individual Grande", resultado.getNombre());
		assertEquals(new BigDecimal("12000"), resultado.getPrecio());
	}

	@Test
	void actualizarVarianteInexistenteLanzaExcepcion() {
		VarianteProducto entrada = new VarianteProducto(null, 1, "Nueva", new BigDecimal("1000"), true);
		when(varianteRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> varianteService.actualizar(99, entrada));
	}

	@Test
	void eliminarVarianteExistenteNoLanzaExcepcion() {
		VarianteProducto existente = new VarianteProducto(1, 1, "Individual", new BigDecimal("10000"), true);
		when(varianteRepository.buscarPorId(1)).thenReturn(Optional.of(existente));

		varianteService.eliminar(1); // no debe lanzar excepción
	}

	@Test
	void eliminarVarianteInexistenteLanzaExcepcion() {
		when(varianteRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> varianteService.eliminar(99));
		verify(varianteRepository, never()).eliminar(any());
	}
}
