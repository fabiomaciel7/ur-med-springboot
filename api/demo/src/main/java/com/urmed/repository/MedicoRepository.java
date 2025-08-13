package com.urmed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.urmed.model.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {
    boolean existsByCrm(String crm);
}
