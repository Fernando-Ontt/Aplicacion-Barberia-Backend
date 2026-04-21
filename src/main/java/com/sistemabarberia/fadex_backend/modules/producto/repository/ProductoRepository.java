package com.sistemabarberia.fadex_backend.modules.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>,JpaSpecificationExecutor<Producto> {

}
