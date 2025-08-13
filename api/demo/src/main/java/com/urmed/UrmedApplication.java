package com.urmed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.urmed.model.Funcionario;
import com.urmed.repository.FuncionarioRepository;

@SpringBootApplication
public class UrmedApplication {

    public static void main(String[] args) {
        SpringApplication.run(UrmedApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(FuncionarioRepository repo, BCryptPasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                Funcionario admin = new Funcionario();
                admin.setNome("Administrador");
                admin.setCpf("000.000.000-00");
                admin.setCargo("ADMIN");
                admin.setTelefone("11999999999");
                admin.setEmail("admin@urmed.com");
                admin.setSenha(encoder.encode("123456")); // senha criptografada
                repo.save(admin);
                System.out.println("Usuário admin criado: admin@urmed.com / 123456");
            }
        };
    }
}
