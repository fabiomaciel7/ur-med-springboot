package com.urmed.service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.urmed.model.Exame;
import com.urmed.model.Medico;
import com.urmed.model.Paciente;
import com.urmed.repository.ExameRepository;
import com.urmed.repository.MedicoRepository;
import com.urmed.repository.PacienteRepository;
import com.urmed.web.dto.ExameDTO;

@Service
public class ExameService {

    private final ExameRepository exameRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public ExameService(ExameRepository exameRepository,
                        MedicoRepository medicoRepository,
                        PacienteRepository pacienteRepository) {
        this.exameRepository = exameRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ExameDTO> listarTodos() {
        return exameRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExameDTO buscarPorId(Long id) {
        Exame exame = exameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exame não encontrado"));
        return toDTO(exame);
    }

    @Transactional
    public ExameDTO salvar(ExameDTO dto) {
        Exame exame = toEntity(dto);
        Exame salvo = exameRepository.save(exame);
        return toDTO(salvo);
    }

    @Transactional
    public ExameDTO atualizar(Long id, ExameDTO dto) {
        Exame existente = exameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exame não encontrado"));

        existente.setData(dto.getData());
        existente.setHora(dto.getHora());
        existente.setTipo(dto.getTipo());
        existente.setResultado(dto.getResultado());
        existente.setObservacoes(dto.getObservacoes());

        Medico medico = medicoRepository.findById(dto.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        existente.setMedico(medico);

        Paciente paciente = pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        existente.setPaciente(paciente);

        return toDTO(exameRepository.save(existente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!exameRepository.existsById(id)) {
            throw new RuntimeException("Exame não encontrado");
        }
        exameRepository.deleteById(id);
    }

    @Transactional
    public ExameDTO atualizarParcial(Long id, Map<String, Object> campos) {
        Exame exame = exameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exame não encontrado"));

        campos.forEach((chave, valor) -> {
            Field field = ReflectionUtils.findField(Exame.class, chave);
            if (field != null) {
                field.setAccessible(true);
                if (field.getType().equals(LocalDate.class) && valor instanceof String) {
                    valor = LocalDate.parse((String) valor);
                }
                if (field.getType().equals(LocalTime.class) && valor instanceof String) {
                    valor = LocalTime.parse((String) valor);
                }
                ReflectionUtils.setField(field, exame, valor);
            }
        });

        return toDTO(exameRepository.save(exame));
    }

    private ExameDTO toDTO(Exame exame) {
        ExameDTO dto = new ExameDTO();
        dto.setId(exame.getId());
        dto.setData(exame.getData());
        dto.setHora(exame.getHora());
        dto.setTipo(exame.getTipo());
        dto.setResultado(exame.getResultado());
        dto.setObservacoes(exame.getObservacoes());
        dto.setIdMedico(exame.getMedico().getId());
        dto.setIdPaciente(exame.getPaciente().getId());
        dto.setNomeMedico(exame.getMedico().getNome());
        dto.setNomePaciente(exame.getPaciente().getNome());
        return dto;
    }

    private Exame toEntity(ExameDTO dto) {
        Medico medico = medicoRepository.findById(dto.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        Paciente paciente = pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Exame exame = new Exame();
        exame.setId(dto.getId());
        exame.setData(dto.getData());
        exame.setHora(dto.getHora());
        exame.setTipo(dto.getTipo());
        exame.setResultado(dto.getResultado());
        exame.setObservacoes(dto.getObservacoes());
        exame.setMedico(medico);
        exame.setPaciente(paciente);
        return exame;
    }
}
