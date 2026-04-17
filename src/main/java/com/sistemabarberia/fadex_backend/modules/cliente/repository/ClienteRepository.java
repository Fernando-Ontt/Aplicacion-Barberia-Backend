package com.sistemabarberia.fadex_backend.modules.cliente.repository;

import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Integer> {

}
