package com.urmed.web.dto;

import jakarta.validation.constraints.*;

public class MedicoDTO {

    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "O CRM é obrigatório")
    @Size(max = 50, message = "O CRM deve ter no máximo 50 caracteres")
    private String crm;

    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
    private String telefone;

    @Email(message = "O email deve ser válido")
    @Size(max = 100, message = "O email deve ter no máximo 100 caracteres")
    private String email;

    @NotNull(message = "O ID da especialidade é obrigatório")
    private Long idEspecialidade;

    private String especialidadeNome;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCrm() { return crm; }
    public void setCrm(String crm) { this.crm = crm; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getIdEspecialidade() { return idEspecialidade; }
    public void setIdEspecialidade(Long idEspecialidade) { this.idEspecialidade = idEspecialidade; }

    public String getEspecialidadeNome() { return especialidadeNome; }
    public void setEspecialidadeNome(String especialidadeNome) { this.especialidadeNome = especialidadeNome; }
}
