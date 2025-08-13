package com.urmed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urmed.model.Agendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    boolean existsByMedicoId(Long medicoId);
    boolean existsByFuncionarioId(Long funcionarioId);
}
