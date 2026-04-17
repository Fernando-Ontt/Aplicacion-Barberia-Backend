package com.sistemabarberia.fadex_backend.modules.barbero.repository;

import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BarberoRepository extends JpaRepository<Barbero,Integer>{

}
