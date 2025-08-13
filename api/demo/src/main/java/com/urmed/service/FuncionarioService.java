package com.urmed.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.urmed.model.Funcionario;
import com.urmed.repository.AgendamentoRepository;
import com.urmed.repository.FuncionarioRepository;
import com.urmed.web.dto.FuncionarioDTO;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, AgendamentoRepository agendamentoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional(readOnly = true)
    public List<FuncionarioDTO> listarTodos() {
        return funcionarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FuncionarioDTO buscarPorId(Long id) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));
        return toDTO(funcionario);
    }

    @Transactional
    public FuncionarioDTO salvar(FuncionarioDTO dto) {
        Funcionario funcionario = toEntity(dto);
        funcionario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return toDTO(funcionarioRepository.save(funcionario));
    }

    @Transactional
    public FuncionarioDTO atualizar(Long id, FuncionarioDTO dto) {
        Funcionario existente = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        existente.setNome(dto.getNome());
        existente.setCpf(dto.getCpf());
        existente.setCargo(dto.getCargo());
        existente.setTelefone(dto.getTelefone());
        existente.setEmail(dto.getEmail());

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            existente.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return toDTO(funcionarioRepository.save(existente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!funcionarioRepository.existsById(id)) {
            throw new RuntimeException("Funcionário não encontrado");
        }
        if (agendamentoRepository.existsByFuncionarioId(id)) {
            throw new RuntimeException("Não é possível excluir o funcionário: há agendamentos associados.");
        }
        funcionarioRepository.deleteById(id);
    }

    @Transactional
    public FuncionarioDTO atualizarParcial(Long id, Map<String, Object> campos) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        campos.forEach((chave, valor) -> {
            Field field = ReflectionUtils.findField(Funcionario.class, chave);
            if (field != null) {
                field.setAccessible(true);
                if (field.getName().equals("senha")) {
                    valor = passwordEncoder.encode(valor.toString());
                }
                ReflectionUtils.setField(field, funcionario, valor);
            }
        });

        return toDTO(funcionarioRepository.save(funcionario));
    }

    private FuncionarioDTO toDTO(Funcionario funcionario) {
        FuncionarioDTO dto = new FuncionarioDTO();
        dto.setId(funcionario.getId());
        dto.setNome(funcionario.getNome());
        dto.setCpf(funcionario.getCpf());
        dto.setCargo(funcionario.getCargo());
        dto.setTelefone(funcionario.getTelefone());
        dto.setEmail(funcionario.getEmail());
        dto.setSenha(null);
        return dto;
    }

    private Funcionario toEntity(FuncionarioDTO dto) {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(dto.getId());
        funcionario.setNome(dto.getNome());
        funcionario.setCpf(dto.getCpf());
        funcionario.setCargo(dto.getCargo());
        funcionario.setTelefone(dto.getTelefone());
        funcionario.setEmail(dto.getEmail());
        funcionario.setSenha(dto.getSenha());
        return funcionario;
    }
}
