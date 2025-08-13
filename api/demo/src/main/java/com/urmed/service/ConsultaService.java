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

import com.urmed.model.Consulta;
import com.urmed.model.Medico;
import com.urmed.model.Paciente;
import com.urmed.repository.ConsultaRepository;
import com.urmed.repository.MedicoRepository;
import com.urmed.repository.PacienteRepository;
import com.urmed.web.dto.ConsultaDTO;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaService(ConsultaRepository consultaRepository,
                           MedicoRepository medicoRepository,
                           PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ConsultaDTO> listarTodos() {
        return consultaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConsultaDTO buscarPorId(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        return toDTO(consulta);
    }

    @Transactional
    public ConsultaDTO salvar(ConsultaDTO dto) {
        Consulta consulta = toEntity(dto);
        Consulta salvo = consultaRepository.save(consulta);
        return toDTO(salvo);
    }

    @Transactional
    public ConsultaDTO atualizar(Long id, ConsultaDTO dto) {
        Consulta existente = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        existente.setData(dto.getData());
        existente.setHora(dto.getHora());
        existente.setDiagnostico(dto.getDiagnostico());
        existente.setObservacoes(dto.getObservacoes());

        Medico medico = medicoRepository.findById(dto.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        existente.setMedico(medico);

        Paciente paciente = pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        existente.setPaciente(paciente);

        Consulta atualizado = consultaRepository.save(existente);
        return toDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!consultaRepository.existsById(id)) {
            throw new RuntimeException("Consulta não encontrada");
        }
        consultaRepository.deleteById(id);
    }

    @Transactional
    public ConsultaDTO atualizarParcial(Long id, Map<String, Object> campos) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        campos.forEach((chave, valor) -> {
            switch (chave) {
                case "idMedico" -> {
                    Long idMedico = Long.valueOf(valor.toString());
                    Medico medico = medicoRepository.findById(idMedico)
                            .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
                    consulta.setMedico(medico);
                }

                case "idPaciente" -> {
                    Long idPaciente = Long.valueOf(valor.toString());
                    Paciente paciente = pacienteRepository.findById(idPaciente)
                            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
                    consulta.setPaciente(paciente);
                }

                default -> {
                    Field field = ReflectionUtils.findField(Consulta.class, chave);
                    if (field != null) {
                        field.setAccessible(true);
                        if (field.getType().equals(LocalDate.class) && valor instanceof String) {
                            valor = LocalDate.parse((String) valor);
                        }
                        if (field.getType().equals(LocalTime.class) && valor instanceof String) {
                            valor = LocalTime.parse((String) valor);
                        }
                        ReflectionUtils.setField(field, consulta, valor);
                    }
                }
            }
        });

        return toDTO(consultaRepository.save(consulta));
    }



    private ConsultaDTO toDTO(Consulta consulta) {
        ConsultaDTO dto = new ConsultaDTO();
        dto.setId(consulta.getId());
        dto.setData(consulta.getData());
        dto.setHora(consulta.getHora());
        dto.setIdMedico(consulta.getMedico().getId());
        dto.setIdPaciente(consulta.getPaciente().getId());
        dto.setDiagnostico(consulta.getDiagnostico());
        dto.setObservacoes(consulta.getObservacoes());
        dto.setNomeMedico(consulta.getMedico().getNome());
        dto.setNomePaciente(consulta.getPaciente().getNome());
        return dto;
    }

    private Consulta toEntity(ConsultaDTO dto) {
        Medico medico = medicoRepository.findById(dto.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        Paciente paciente = pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Consulta consulta = new Consulta();
        consulta.setId(dto.getId());
        consulta.setData(dto.getData());
        consulta.setHora(dto.getHora());
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setDiagnostico(dto.getDiagnostico());
        consulta.setObservacoes(dto.getObservacoes());
        return consulta;
    }
}
