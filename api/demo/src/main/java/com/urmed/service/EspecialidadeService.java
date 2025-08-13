package com.urmed.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.urmed.model.Especialidade;
import com.urmed.repository.EspecialidadeRepository;
import com.urmed.web.dto.EspecialidadeDTO;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;

    public EspecialidadeService(EspecialidadeRepository especialidadeRepository) {
        this.especialidadeRepository = especialidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<EspecialidadeDTO> listarTodos() {
        return especialidadeRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EspecialidadeDTO buscarPorId(Long id) {
        Especialidade esp = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));
        return toDTO(esp);
    }

    @Transactional
    public EspecialidadeDTO salvar(EspecialidadeDTO dto) {
        if (especialidadeRepository.existsByNome(dto.getNome())) {
            throw new RuntimeException("Especialidade já cadastrada");
        }
        Especialidade esp = toEntity(dto);
        return toDTO(especialidadeRepository.save(esp));
    }

    @Transactional
    public EspecialidadeDTO atualizar(Long id, EspecialidadeDTO dto) {
        Especialidade esp = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

        esp.setNome(dto.getNome());
        return toDTO(especialidadeRepository.save(esp));
    }

    @Transactional
    public void deletar(Long id) {
        especialidadeRepository.deleteById(id);
    }

    private EspecialidadeDTO toDTO(Especialidade esp) {
        EspecialidadeDTO dto = new EspecialidadeDTO();
        dto.setId(esp.getId());
        dto.setNome(esp.getNome());
        return dto;
    }

    private Especialidade toEntity(EspecialidadeDTO dto) {
        Especialidade esp = new Especialidade();
        esp.setId(dto.getId());
        esp.setNome(dto.getNome());
        return esp;
    }
}
