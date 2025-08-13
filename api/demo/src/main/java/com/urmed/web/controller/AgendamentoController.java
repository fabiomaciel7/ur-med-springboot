package com.urmed.web.controller;

import com.urmed.service.AgendamentoService;
import com.urmed.web.dto.AgendamentoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoDTO>> listar() {
        return ResponseEntity.ok(agendamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendamentoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AgendamentoDTO> criar(@RequestBody AgendamentoDTO dto) {
        return ResponseEntity.ok(agendamentoService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoDTO> atualizar(@PathVariable Long id, @RequestBody AgendamentoDTO dto) {
        return ResponseEntity.ok(agendamentoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendamentoDTO> atualizarParcial(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(agendamentoService.atualizarParcial(id, campos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        agendamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
