package com.urmed.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.urmed.model.Especialidade;
import com.urmed.repository.EspecialidadeRepository;
import com.urmed.repository.MedicoRepository;
import com.urmed.web.dto.EspecialidadeDTO;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository especialidadeRepository;
    private final MedicoRepository medicoRepository;

    public EspecialidadeService(EspecialidadeRepository especialidadeRepository, MedicoRepository medicoRepository) {
        this.especialidadeRepository = especialidadeRepository;
        this.medicoRepository = medicoRepository;
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
        if (!especialidadeRepository.existsById(id)) {
            throw new RuntimeException("Especialidade não encontrada");
        }

        boolean existeMedico = medicoRepository.existsByEspecialidadeId(id);
        if (existeMedico) {
            throw new RuntimeException("Não é possível excluir a especialidade, pois existem médicos associados a ela.");
        }

        especialidadeRepository.deleteById(id);
    }

    @Transactional
    public EspecialidadeDTO atualizarParcial(Long id, Map<String, Object> campos) {
        Especialidade especialidade = especialidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

        campos.forEach((chave, valor) -> {
            Field field = ReflectionUtils.findField(Especialidade.class, chave);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, especialidade, valor);
            }
        });

        return toDTO(especialidadeRepository.save(especialidade));
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
