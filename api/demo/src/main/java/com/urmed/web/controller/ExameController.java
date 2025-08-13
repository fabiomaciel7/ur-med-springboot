package com.urmed.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urmed.service.ExameService;
import com.urmed.web.dto.ExameDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exames")
public class ExameController {

    private final ExameService exameService;

    public ExameController(ExameService exameService) {
        this.exameService = exameService;
    }

    @GetMapping
    public ResponseEntity<List<ExameDTO>> listar() {
        return ResponseEntity.ok(exameService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExameDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(exameService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ExameDTO> criar(@Valid @RequestBody ExameDTO dto) {
        return ResponseEntity.ok(exameService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExameDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ExameDTO dto) {
        return ResponseEntity.ok(exameService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        exameService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ExameDTO> atualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(exameService.atualizarParcial(id, campos));
    }
}
