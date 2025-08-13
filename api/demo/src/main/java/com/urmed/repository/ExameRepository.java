package com.urmed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.urmed.model.Exame;

public interface ExameRepository extends JpaRepository<Exame, Long> {
}
