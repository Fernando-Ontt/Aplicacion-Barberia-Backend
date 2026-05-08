package com.sistemabarberia.fadex_backend.modules.reserva.repository;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
           SELECT  r from Reserva r
            WHERE r.barbero.barberoId= :barberoId
            AND r.fecha= :fecha
            AND (r.HoraInicio< :horaFin AND r.HoraFin> :horaIncio  )                                
           """
    )
    List<Reserva> findConflictos(@Param("barberoId") Long barberoId ,
                                 @Param("fecha") LocalDate fecha,
                                 @Param("horaInicio")LocalTime horaInicio,
                                 @Param("horaFin")LocalTime horaFin);
}
