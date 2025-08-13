package com.clinica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinica.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}