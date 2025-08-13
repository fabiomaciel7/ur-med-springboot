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

import com.urmed.model.Agendamento;
import com.urmed.model.Funcionario;
import com.urmed.model.Medico;
import com.urmed.model.Paciente;
import com.urmed.repository.AgendamentoRepository;
import com.urmed.repository.FuncionarioRepository;
import com.urmed.repository.MedicoRepository;
import com.urmed.repository.PacienteRepository;
import com.urmed.web.dto.AgendamentoDTO;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                               MedicoRepository medicoRepository,
                               PacienteRepository pacienteRepository,
                               FuncionarioRepository funcionarioRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional(readOnly = true)
    public List<AgendamentoDTO> listarTodos() {
        return agendamentoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AgendamentoDTO buscarPorId(Long id) {
        return toDTO(agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado")));
    }

    @Transactional
    public AgendamentoDTO salvar(AgendamentoDTO dto) {
        return toDTO(agendamentoRepository.save(toEntity(dto)));
    }

    @Transactional
    public AgendamentoDTO atualizar(Long id, AgendamentoDTO dto) {
        Agendamento existente = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        existente.setData(dto.getData());
        existente.setHora(dto.getHora());
        existente.setTipo(dto.getTipo());
        existente.setStatus(dto.getStatus());

        existente.setMedico(medicoRepository.findById(dto.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado")));

        existente.setPaciente(pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado")));

        existente.setFuncionario(funcionarioRepository.findById(dto.getIdFuncionario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado")));

        return toDTO(agendamentoRepository.save(existente));
    }

    @Transactional
    public AgendamentoDTO atualizarParcial(Long id, Map<String, Object> campos) {
        Agendamento ag = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        for (Map.Entry<String, Object> e : campos.entrySet()) {
            String chave = e.getKey();
            Object valor = e.getValue();

            switch (chave) {
                case "idMedico" -> {
                    Long idMedico = toLong(valor);
                    Medico m = medicoRepository.findById(idMedico)
                            .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
                    ag.setMedico(m);
                }
                case "idPaciente" -> {
                    Long idPaciente = toLong(valor);
                    Paciente p = pacienteRepository.findById(idPaciente)
                            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
                    ag.setPaciente(p);
                }
                case "idFuncionario" -> {
                    Long idFunc = toLong(valor);
                    Funcionario f = funcionarioRepository.findById(idFunc)
                            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
                    ag.setFuncionario(f);
                }
                case "data" -> ag.setData(valor instanceof String ? LocalDate.parse((String) valor) : (LocalDate) valor);
                case "hora" -> ag.setHora(valor instanceof String ? LocalTime.parse((String) valor) : (LocalTime) valor);
                case "tipo", "status" -> {
                    Field field = ReflectionUtils.findField(Agendamento.class, chave);
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, ag, valor);
                }
                case "id", "nomeMedico", "nomePaciente", "nomeFuncionario" -> {
                }
                default -> throw new RuntimeException("Campo não suportado no PATCH: " + chave);
            }
        }
        return toDTO(agendamentoRepository.save(ag));
    }

    @Transactional
    public void deletar(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new RuntimeException("Agendamento não encontrado");
        }
        agendamentoRepository.deleteById(id);
    }

    // helpers

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s) return Long.valueOf(s);
        throw new RuntimeException("Valor inválido para ID: " + v);
    }

    private AgendamentoDTO toDTO(Agendamento ag) {
        AgendamentoDTO dto = new AgendamentoDTO();
        dto.setId(ag.getId());
        dto.setData(ag.getData());
        dto.setHora(ag.getHora());
        dto.setTipo(ag.getTipo());
        dto.setStatus(ag.getStatus());
        dto.setIdMedico(ag.getMedico().getId());
        dto.setIdPaciente(ag.getPaciente().getId());
        dto.setIdFuncionario(ag.getFuncionario().getId());
        dto.setNomeMedico(ag.getMedico().getNome());
        dto.setNomePaciente(ag.getPaciente().getNome());
        dto.setNomeFuncionario(ag.getFuncionario().getNome());
        return dto;
    }

    private Agendamento toEntity(AgendamentoDTO dto) {
        Agendamento ag = new Agendamento();
        ag.setId(dto.getId());
        ag.setData(dto.getData());
        ag.setHora(dto.getHora());
        ag.setTipo(dto.getTipo());
        ag.setStatus(dto.getStatus());
        ag.setMedico(medicoRepository.findById(dto.getIdMedico())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado")));
        ag.setPaciente(pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado")));
        ag.setFuncionario(funcionarioRepository.findById(dto.getIdFuncionario())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado")));
        return ag;
    }
}
