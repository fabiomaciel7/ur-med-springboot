CREATE TABLE especialidade (
    id BIGINT IDENTITY PRIMARY KEY,
    nome NVARCHAR(100) NOT NULL,
    descricao NVARCHAR(255)
);

CREATE TABLE medico (
    id BIGINT IDENTITY PRIMARY KEY,
    nome NVARCHAR(100) NOT NULL,
    crm NVARCHAR(50) NOT NULL UNIQUE,
    telefone NVARCHAR(20),
    email NVARCHAR(100),
    id_especialidade BIGINT NOT NULL,
    FOREIGN KEY (id_especialidade) REFERENCES especialidade(id)
);

CREATE TABLE paciente (
    id BIGINT IDENTITY PRIMARY KEY,
    nome NVARCHAR(100) NOT NULL,
    cpf NVARCHAR(14) NOT NULL UNIQUE,
    telefone NVARCHAR(20),
    email NVARCHAR(100),
    data_nascimento DATE
);

CREATE TABLE funcionario (
    id BIGINT IDENTITY PRIMARY KEY,
    nome NVARCHAR(100) NOT NULL,
    cpf NVARCHAR(14) NOT NULL UNIQUE,
    cargo NVARCHAR(50),
    telefone NVARCHAR(20),
    email NVARCHAR(100)
);

CREATE TABLE agendamento (
    id BIGINT IDENTITY PRIMARY KEY,
    data DATE NOT NULL,
    hora TIME NOT NULL,
    id_medico BIGINT NOT NULL,
    id_paciente BIGINT NOT NULL,
    status NVARCHAR(20) NOT NULL,
    id_funcionario BIGINT NOT NULL,
    FOREIGN KEY (id_medico) REFERENCES medico(id),
    FOREIGN KEY (id_paciente) REFERENCES paciente(id),
    FOREIGN KEY (id_funcionario) REFERENCES funcionario(id)
);

CREATE TABLE consulta (
    id BIGINT IDENTITY PRIMARY KEY,
    data DATE NOT NULL,
    hora TIME NOT NULL,
    id_medico BIGINT NOT NULL,
    id_paciente BIGINT NOT NULL,
    diagnostico NVARCHAR(MAX),
    observacoes NVARCHAR(MAX),
    FOREIGN KEY (id_medico) REFERENCES medico(id),
    FOREIGN KEY (id_paciente) REFERENCES paciente(id)
);