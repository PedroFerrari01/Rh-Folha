# RH Folha | Gestão de Recursos Humanos e Folha de Pagamento

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%234479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)

## 📖 Sobre o Projeto

O **RH Folha** é um sistema corporativo desenvolvido para solucionar problemas reais de departamento pessoal: a complexidade matemática e o risco de erros manuais no fechamento da folha de pagamento. 

Em vez de depender de planilhas descentralizadas, o sistema centraliza o cadastro de funcionários, departamentos e cargos em um banco de dados relacional (MySQL), e atua como um motor de cálculo automatizado. O grande diferencial desta aplicação é a **fidelidade às regras de negócio e leis trabalhistas**, implementando lógicas exatas para:
*   **Tributação Progressiva (INSS):** O algoritmo particiona o salário base e aplica as diferentes alíquotas exatamente como a tabela oficial do governo, somando as parcelas para o desconto final.
*   **Imposto de Renda (IRRF):** Cálculo automatizado considerando a base pós-INSS e aplicando a dedução legal de forma exata.
*   **Benefícios:** Parametrização e desconto automático do teto de 6% para Vale-Transporte e adição do Vale-Alimentação.

O foco da arquitetura foi garantir a exatidão financeira (utilizando `BigDecimal` para evitar erros de arredondamento) e a integridade dos dados (bloqueando a geração duplicada de holerites no mesmo mês de referência).

---

## ⚙️ Arquitetura e Funcionalidades

- **Motor de Folha de Pagamento:** Geração em lote com cálculos automatizados de INSS progressivo e IRRF.
- **Precisão Financeira:** Uso exclusivo da classe `BigDecimal` para todas as operações monetárias.
- **Integridade de Dados:** Implementação de restrições (`UNIQUE constraints`) para impedir a duplicidade de folhas para o mesmo funcionário no mesmo período.
- **Gestão de Pessoal:** CRUD completo de Departamentos, Cargos e Funcionários. Controle de estado de atividade (admissão/desligamento) preservando o histórico.
- **Controle de Ponto:** Registro estruturado de férias, atestados e faltas, com reflexo no cálculo final do holerite.
- **Relatórios Gerenciais:** Extração de métricas de custo total por departamento.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 17+
- **Interface Gráfica:** Swing (`GridBagLayout` e `JTabbedPane`)
- **Banco de Dados:** MySQL 8 
- **Integração e Arquitetura:** JDBC (`java.sql`, `PreparedStatement`), Padrão DAO
- **Configuração:** Externalização de credenciais via `config.properties`

---

## 📁 Estrutura do Projeto

A arquitetura do código foi organizada visando a separação de responsabilidades (Clean Code):

```text
rh-folha/
├── sql/
│   └── schema.sql             # Scripts DDL/DML para criação e carga inicial do banco
├── src/
│   ├── Main.java              # Entry point da aplicação
│   ├── config.properties      # Credenciais de conexão (JDBC)
│   ├── model/                 # Entidades do domínio
│   ├── dao/                   # Camada de persistência (Padrão DAO)
│   ├── ui/                    # Telas e componentes gráficos Swing
│   └── util/
│       ├── ConexaoFactory.java    # Fábrica de conexões Singleton
│       └── CalculadoraFolha.java  # Lógica matemática de tributos isolada
└── README.md
```

---

## 🚀 Como Executar

### Pré-requisitos
*   **Java Development Kit (JDK) 17** ou superior.
*   **MySQL Server 8.0** ou superior em execução.
*   **Driver JDBC (mysql-connector-j):** Necessário para a conexão com o banco.

### Passo 1: Preparando o Banco de Dados
1. Abra o seu cliente MySQL (MySQL Workbench, DBeaver ou Terminal).
2. Execute o script `schema.sql` localizado na pasta `sql/`. Ele criará o banco de dados `rh_folha`, as tabelas necessárias e fará a inserção de alguns dados de teste (departamentos e cargos).

### Passo 2: Configurando as Credenciais
1. Navegue até a pasta `src/` e abra o arquivo `config.properties`.
2. Edite as informações com o usuário e a senha do seu banco de dados local:
```properties
db.url=jdbc:mysql://localhost:3306/rh_folha
db.user=seu_usuario_aqui
db.password=sua_senha_aqui
```

### Passo 3: Executando o Projeto

**Opção A: Rodando via IDE (Eclipse, IntelliJ ou VS Code) - *Recomendado***
1. Importe a pasta do projeto na sua IDE.
2. Adicione o arquivo `.jar` do `mysql-connector-j` ao *Build Path* (Eclipse) ou *Dependencies* (IntelliJ) do projeto.
3. Localize a classe `Main.java` na pasta `src/` e execute o projeto.

**Opção B: Rodando via Terminal**
Certifique-se de que o `.jar` do conector do MySQL está em uma pasta `lib/` na raiz do projeto.
```bash
cd src
# Compila os arquivos Java
javac -d ../out -cp ".:../lib/mysql-connector-j-x.x.x.jar" $(find . -name "*.java")
# Copia as credenciais para a pasta de saída
cp config.properties ../out/
cd ../out
# Executa a aplicação
java -cp ".:../lib/mysql-connector-j-x.x.x.jar" Main
```

---

## 📈 Roadmap de Evolução

- [ ] Implementação de cálculo de 13º salário e férias proporcionais.
- [ ] Exportação de holerites para o formato PDF.
- [ ] Módulo de autenticação com controle de acesso (Gestor de RH vs. Funcionário).
- [ ] Registro em banco do histórico de reajustes salariais.
