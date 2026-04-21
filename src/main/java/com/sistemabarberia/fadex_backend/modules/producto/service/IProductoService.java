package com.sistemabarberia.fadex_backend.modules.producto.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.producto.dto.ProductoFiltro;
import com.sistemabarberia.fadex_backend.modules.producto.dto.request.ProductoRequest;
import com.sistemabarberia.fadex_backend.modules.producto.dto.response.ProductoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductoService {
    PageResponse<ProductoResponse> listarProductoFiltros(ProductoFiltro filtro, Pageable pageable);
    ProductoResponse obtenerProductoPorId(Long id);
}
