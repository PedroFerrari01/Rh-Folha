-- ============================================================
-- RH Folha - Script de criação do banco de dados (MySQL)
-- ============================================================

CREATE DATABASE IF NOT EXISTS rh_folha CHARACTER SET utf8mb4;
USE rh_folha;

CREATE TABLE IF NOT EXISTS departamento (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nome    VARCHAR(80) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cargo (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(80) NOT NULL,
    salario_base    DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS funcionario (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nome                VARCHAR(120) NOT NULL,
    cpf                 VARCHAR(14) NOT NULL UNIQUE,
    data_admissao       DATE NOT NULL,
    cargo_id            INT NOT NULL,
    departamento_id     INT NOT NULL,
    salario             DECIMAL(10,2) NOT NULL,
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_funcionario_cargo FOREIGN KEY (cargo_id) REFERENCES cargo(id),
    CONSTRAINT fk_funcionario_departamento FOREIGN KEY (departamento_id) REFERENCES departamento(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS folha_pagamento (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    funcionario_id      INT NOT NULL,
    mes_referencia      VARCHAR(7) NOT NULL, -- formato 'YYYY-MM'
    salario_bruto       DECIMAL(10,2) NOT NULL,
    desconto_inss       DECIMAL(10,2) NOT NULL,
    desconto_irrf       DECIMAL(10,2) NOT NULL,
    desconto_vt         DECIMAL(10,2) NOT NULL,
    beneficio_va        DECIMAL(10,2) NOT NULL,
    salario_liquido      DECIMAL(10,2) NOT NULL,
    data_geracao        DATETIME NOT NULL,
    CONSTRAINT fk_folha_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionario(id),
    CONSTRAINT uq_folha_funcionario_mes UNIQUE (funcionario_id, mes_referencia)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ausencia (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    funcionario_id      INT NOT NULL,
    tipo                ENUM('FERIAS', 'ATESTADO', 'FALTA') NOT NULL,
    data_inicio         DATE NOT NULL,
    data_fim            DATE NOT NULL,
    observacao          VARCHAR(300),
    CONSTRAINT fk_ausencia_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
) ENGINE=InnoDB;

-- Dados de exemplo
INSERT INTO departamento (nome) VALUES ('Tecnologia'), ('Marketing'), ('Financeiro'), ('Recursos Humanos');

INSERT INTO cargo (nome, salario_base) VALUES
    ('Desenvolvedor Jr', 3200.00),
    ('Desenvolvedor Pleno', 5500.00),
    ('Analista de Marketing', 3800.00),
    ('Analista Financeiro', 4200.00),
    ('Assistente de RH', 2600.00);
