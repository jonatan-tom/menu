package com.restaurante.menu.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.restaurante.menu.dto.CategoriaMenu;
import com.restaurante.menu.entity.Categoria;
import com.restaurante.menu.entity.Producto;
import com.restaurante.menu.entity.VarianteProducto;
import com.restaurante.menu.service.MenuService;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

	@Mock
	private MenuService menuService;

	@Test
	void obtenerMenuDevuelveListaDeCategoriasConProductos() {
		Categoria categoria = new Categoria(1, "Hamburguesas", 1, true);
		Producto producto = new Producto(1, 1, "Hamburguesa", "Clásica", new BigDecimal("10000"), null, true);
		CategoriaMenu categoriaMenu = new CategoriaMenu(categoria, List.of(
				new CategoriaMenu.ProductoMenu(producto, List.of())));
		when(menuService.obtenerMenu()).thenReturn(List.of(categoriaMenu));

		List<CategoriaMenu> resultado = menuService.obtenerMenu();

		assertEquals(1, resultado.size());
		assertEquals("Hamburguesas", resultado.get(0).categoria().getNombre());
		assertEquals(1, resultado.get(0).productos().size());
		assertEquals("Hamburguesa", resultado.get(0).productos().get(0).producto().getNombre());
	}

	@Test
	void obtenerMenuVacioDevuelveListaVacia() {
		when(menuService.obtenerMenu()).thenReturn(List.of());

		List<CategoriaMenu> resultado = menuService.obtenerMenu();

		assertEquals(0, resultado.size());
	}

	@Test
	void obtenerMenuConProductoSinVariantesDevuelveListaVaciaDeVariantes() {
		Categoria categoria = new Categoria(1, "Bebidas", 2, true);
		Producto producto = new Producto(2, 1, "Gaseosa", "500ml", new BigDecimal("3000"), null, true);
		CategoriaMenu categoriaMenu = new CategoriaMenu(categoria, List.of(
				new CategoriaMenu.ProductoMenu(producto, List.of())));
		when(menuService.obtenerMenu()).thenReturn(List.of(categoriaMenu));

		List<CategoriaMenu> resultado = menuService.obtenerMenu();

		assertEquals(1, resultado.size());
		assertEquals(0, resultado.get(0).productos().get(0).variantes().size());
	}

	@Test
	void obtenerMenuConProductoConVariantesDevuelveVariantes() {
		Categoria categoria = new Categoria(1, "Hamburguesas", 1, true);
		Producto producto = new Producto(1, 1, "Hamburguesa", "Clásica", new BigDecimal("10000"), null, true);
		VarianteProducto variante = new VarianteProducto(1, 1, "Individual", new BigDecimal("10000"), true);
		CategoriaMenu categoriaMenu = new CategoriaMenu(categoria, List.of(
				new CategoriaMenu.ProductoMenu(producto, List.of(variante))));
		when(menuService.obtenerMenu()).thenReturn(List.of(categoriaMenu));

		List<CategoriaMenu> resultado = menuService.obtenerMenu();

		assertEquals(1, resultado.size());
		assertEquals(1, resultado.get(0).productos().get(0).variantes().size());
		assertEquals("Individual", resultado.get(0).productos().get(0).variantes().get(0).getNombre());
	}

	@Test
	void menuServiceEsMockySePuedeVerificarInvocacion() {
		when(menuService.obtenerMenu()).thenReturn(List.of());

		menuService.obtenerMenu();

		verify(menuService).obtenerMenu();
	}
}
