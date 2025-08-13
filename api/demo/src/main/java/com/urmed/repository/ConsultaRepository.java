package com.urmed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urmed.model.Consulta;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    void deleteByPacienteId(Long pacienteId);
    boolean existsByMedicoId(Long medicoId);
}
