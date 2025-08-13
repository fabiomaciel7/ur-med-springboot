package com.urmed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urmed.model.Especialidade;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long> {
    boolean existsByNome(String nome);
}
