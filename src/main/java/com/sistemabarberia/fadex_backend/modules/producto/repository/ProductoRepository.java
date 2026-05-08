package com.sistemabarberia.fadex_backend.modules.producto.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>,JpaSpecificationExecutor<Producto> {
    boolean existsByCategoriaId(Long categoriaId);
    @EntityGraph(attributePaths = {"imagenes","categoria"})
    Page<Producto> findAll (Specification<Producto> filtrar, Pageable pageable);

}
