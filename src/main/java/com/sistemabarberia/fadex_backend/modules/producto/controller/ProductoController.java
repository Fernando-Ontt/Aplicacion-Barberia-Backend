package com.sistemabarberia.fadex_backend.modules.producto.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.producto.dto.ProductoFiltro;
import com.sistemabarberia.fadex_backend.modules.producto.dto.request.ProductoRequest;
import com.sistemabarberia.fadex_backend.modules.producto.dto.response.ProductoResponse;
import com.sistemabarberia.fadex_backend.modules.producto.service.IProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final IProductoService productoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductoResponse>>> obtenerProductos(@Valid @ModelAttribute ProductoFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        PageResponse<ProductoResponse> productos = productoService.listarProductoFiltros(filtro, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Productos  obtenidos correctamente", productos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtenerPorId(@PathVariable Long id) {
        ProductoResponse producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto obtenido correctamente", producto));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@RequestPart("producto") ProductoRequest request, @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        ProductoResponse producto = productoService.crearProducto(request, archivos);
        return ResponseEntity.ok(ApiResponse.ok("Producto creado correctamente", producto));
    }
}
