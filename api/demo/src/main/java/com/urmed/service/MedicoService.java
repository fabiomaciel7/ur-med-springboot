package com.urmed.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.urmed.model.Especialidade;
import com.urmed.model.Medico;
import com.urmed.repository.ConsultaRepository;
import com.urmed.repository.EspecialidadeRepository;
import com.urmed.repository.MedicoRepository;
import com.urmed.repository.AgendamentoRepository;
import com.urmed.web.dto.MedicoDTO;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final ConsultaRepository consultaRepository;
    private final AgendamentoRepository agendamentoRepository;

    public MedicoService(MedicoRepository medicoRepository, EspecialidadeRepository especialidadeRepository, ConsultaRepository consultaRepository, AgendamentoRepository agendamentoRepository) {
        this.medicoRepository = medicoRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.consultaRepository = consultaRepository;
        this.agendamentoRepository = agendamentoRepository;
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
        if (!medicoRepository.existsById(id)) {
            throw new RuntimeException("Médico não encontrado");
        }

        boolean existeConsulta = consultaRepository.existsByMedicoId(id);
        if (existeConsulta) {
            throw new RuntimeException("Não é possível excluir o médico, pois existem consultas associadas a ele.");
        }

        boolean existeAgendamento = agendamentoRepository.existsByMedicoId(id);
        if (existeAgendamento){
            throw new RuntimeException("Não é possível excluir o médico, pois existem agendamentos associadas a ele.");
        }

        medicoRepository.deleteById(id);
    }

    @Transactional
    public MedicoDTO atualizarParcial(Long id, Map<String, Object> campos) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        campos.forEach((chave, valor) -> {
            if ("idEspecialidade".equals(chave)) {
                Long idEsp = Long.valueOf(valor.toString());
                Especialidade especialidade = especialidadeRepository.findById(idEsp)
                        .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));
                medico.setEspecialidade(especialidade);
            } else {
                Field field = ReflectionUtils.findField(Medico.class, chave);
                if (field != null) {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, medico, valor);
                }
            }
        });

        return toDTO(medicoRepository.save(medico));
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
