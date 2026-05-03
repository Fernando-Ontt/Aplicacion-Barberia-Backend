package com.sistemabarberia.fadex_backend.modules.producto.repository;

import com.sistemabarberia.fadex_backend.modules.producto.entity.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {
}
