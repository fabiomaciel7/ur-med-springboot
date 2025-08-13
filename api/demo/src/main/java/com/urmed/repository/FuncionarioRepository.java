package com.urmed.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urmed.model.Funcionario;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByCpf(String cpf);
    Optional<Funcionario> findByEmail(String email);
}
