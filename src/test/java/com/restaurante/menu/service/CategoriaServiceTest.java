package com.restaurante.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.restaurante.menu.entity.Categoria;
import com.restaurante.menu.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

	@Mock
	private CategoriaRepository categoriaRepository;

	@InjectMocks
	private CategoriaService categoriaService;

	@Test
	void listarCategoriasDevuelveLista() {
		Categoria c1 = new Categoria(1, "Hamburguesas", 1, true);
		Categoria c2 = new Categoria(2, "Bebidas", 2, true);
		when(categoriaRepository.listar(false)).thenReturn(List.of(c1, c2));

		List<Categoria> resultado = categoriaService.listar(false);

		assertEquals(2, resultado.size());
		assertEquals("Hamburguesas", resultado.get(0).getNombre());
	}

	@Test
	void obtenerCategoriaExistenteDevuelveCategoria() {
		Categoria c = new Categoria(1, "Hamburguesas", 1, true);
		when(categoriaRepository.buscarPorId(1)).thenReturn(Optional.of(c));

		Categoria resultado = categoriaService.obtener(1);

		assertEquals(1, resultado.getId());
		assertEquals("Hamburguesas", resultado.getNombre());
	}

	@Test
	void obtenerCategoriaInexistenteLanzaExcepcion() {
		when(categoriaRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> categoriaService.obtener(99));
	}

	@Test
	void crearCategoriaGuardaYRetorna() {
		Categoria entrada = new Categoria(null, "Bebidas", 2, true);
		Categoria salida = new Categoria(1, "Bebidas", 2, true);
		when(categoriaRepository.insertar(any(Categoria.class))).thenAnswer(inv -> {
			Categoria cat = inv.getArgument(0);
			cat.setId(1);
			return cat;
		});

		Categoria resultado = categoriaService.crear(entrada);

		assertEquals(1, resultado.getId());
		assertEquals("Bebidas", resultado.getNombre());
		verify(categoriaRepository).insertar(any(Categoria.class));
	}

	@Test
	void crearCategoriaConNombreVacioLanzaExcepcion() {
		Categoria entrada = new Categoria(null, "", 1, true);

		assertThrows(IllegalArgumentException.class, () -> categoriaService.crear(entrada));
		verify(categoriaRepository, never()).insertar(any(Categoria.class));
	}

	@Test
	void actualizarCategoriaExistenteDevuelveActualizada() {
		Categoria existente = new Categoria(1, "Hamburguesas", 1, true);
		Categoria entrada = new Categoria(null, "Comida Rápida", 1, true);
		Categoria salida = new Categoria(1, "Comida Rápida", 1, true);
		when(categoriaRepository.buscarPorId(1)).thenReturn(Optional.of(existente));
		when(categoriaRepository.buscarPorId(1)).thenReturn(Optional.of(salida));

		Categoria resultado = categoriaService.actualizar(1, entrada);

		assertEquals(1, resultado.getId());
		assertEquals("Comida Rápida", resultado.getNombre());
	}

	@Test
	void actualizarCategoriaInexistenteLanzaExcepcion() {
		Categoria entrada = new Categoria(null, "Nueva", 1, true);
		when(categoriaRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> categoriaService.actualizar(99, entrada));
	}

	@Test
	void eliminarCategoriaExistenteNoLanzaExcepcion() {
		Categoria existente = new Categoria(1, "Hamburguesas", 1, true);
		when(categoriaRepository.buscarPorId(1)).thenReturn(Optional.of(existente));

		categoriaService.eliminar(1); // no debe lanzar excepción
	}

	@Test
	void eliminarCategoriaInexistenteLanzaExcepcion() {
		when(categoriaRepository.buscarPorId(99)).thenReturn(Optional.empty());

		assertThrows(NoSuchElementException.class, () -> categoriaService.eliminar(99));
		verify(categoriaRepository, never()).eliminar(any());
	}
}
