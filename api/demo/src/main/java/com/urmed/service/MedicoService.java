package com.urmed.service;

import com.urmed.model.Medico;
import com.urmed.model.Especialidade;
import com.urmed.repository.MedicoRepository;
import com.urmed.repository.EspecialidadeRepository;
import com.urmed.web.dto.MedicoDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadeRepository especialidadeRepository;

    public MedicoService(MedicoRepository medicoRepository, EspecialidadeRepository especialidadeRepository) {
        this.medicoRepository = medicoRepository;
        this.especialidadeRepository = especialidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicoDTO> listarTodos() {
        return medicoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MedicoDTO buscarPorId(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        return toDTO(medico);
    }

    @Transactional
    public MedicoDTO salvar(MedicoDTO dto) {
        if (medicoRepository.existsByCrm(dto.getCrm())) {
            throw new RuntimeException("CRM já cadastrado");
        }
        Especialidade especialidade = especialidadeRepository.findById(dto.getIdEspecialidade())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

        Medico medico = toEntity(dto, especialidade);
        return toDTO(medicoRepository.save(medico));
    }

    @Transactional
    public MedicoDTO atualizar(Long id, MedicoDTO dto) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Especialidade especialidade = especialidadeRepository.findById(dto.getIdEspecialidade())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

        medico.setNome(dto.getNome());
        medico.setCrm(dto.getCrm());
        medico.setTelefone(dto.getTelefone());
        medico.setEmail(dto.getEmail());
        medico.setEspecialidade(especialidade);

        return toDTO(medicoRepository.save(medico));
    }

    @Transactional
    public void deletar(Long id) {
        medicoRepository.deleteById(id);
    }

    private MedicoDTO toDTO(Medico medico) {
        MedicoDTO dto = new MedicoDTO();
        dto.setId(medico.getId());
        dto.setNome(medico.getNome());
        dto.setCrm(medico.getCrm());
        dto.setTelefone(medico.getTelefone());
        dto.setEmail(medico.getEmail());
        dto.setIdEspecialidade(medico.getEspecialidade().getId());
        dto.setEspecialidadeNome(medico.getEspecialidade().getNome());
        return dto;
    }

    private Medico toEntity(MedicoDTO dto, Especialidade especialidade) {
        Medico medico = new Medico();
        medico.setId(dto.getId());
        medico.setNome(dto.getNome());
        medico.setCrm(dto.getCrm());
        medico.setTelefone(dto.getTelefone());
        medico.setEmail(dto.getEmail());
        medico.setEspecialidade(especialidade);
        return medico;
    }
}
