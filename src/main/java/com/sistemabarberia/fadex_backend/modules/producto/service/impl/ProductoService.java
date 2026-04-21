package com.sistemabarberia.fadex_backend.modules.producto.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.commons.storage.FileStorageService;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.producto.dto.ProductoFiltro;
import com.sistemabarberia.fadex_backend.modules.producto.dto.request.ProductoRequest;
import com.sistemabarberia.fadex_backend.modules.producto.dto.response.ProductoResponse;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.producto.mapper.ProductoMapper;
import com.sistemabarberia.fadex_backend.modules.producto.repository.ProductoRepository;
import com.sistemabarberia.fadex_backend.modules.producto.service.IProductoService;
import com.sistemabarberia.fadex_backend.modules.producto.specs.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService implements IProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final FileStorageService fileStorageService;

    private static final List<String> TIPOS_IMAGEN = List.of("image/jpeg", "image/png", "image/webp");

    @Override
    public PageResponse<ProductoResponse> listarProductoFiltros(ProductoFiltro filtro, Pageable pageable) {
        Page<Producto> pagina = productoRepository.findAll(ProductoSpecification.filtrar(filtro), pageable);
        return PageResponse.of(pagina.map(productoMapper::toResponse));
    }

    @Override
    public ProductoResponse obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
        return productoMapper.toResponse(producto);
    }

}
