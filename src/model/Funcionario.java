package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Funcionario {

    private int id;
    private String nome;
    private String cpf;
    private LocalDate dataAdmissao;
    private int cargoId;
    private int departamentoId;
    private BigDecimal salario;
    private boolean ativo;

    // Campos auxiliares só para exibição em telas
    private String nomeCargo;
    private String nomeDepartamento;

    public Funcionario(int id, String nome, String cpf, LocalDate dataAdmissao, int cargoId,
                        int departamentoId, BigDecimal salario, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataAdmissao = dataAdmissao;
        this.cargoId = cargoId;
        this.departamentoId = departamentoId;
        this.salario = salario;
        this.ativo = ativo;
    }

    public Funcionario(String nome, String cpf, LocalDate dataAdmissao, int cargoId,
                        int departamentoId, BigDecimal salario) {
        this(0, nome, cpf, dataAdmissao, cargoId, departamentoId, salario, true);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }

    public int getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(int departamentoId) {
        this.departamentoId = departamentoId;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getNomeCargo() {
        return nomeCargo;
    }

    public void setNomeCargo(String nomeCargo) {
        this.nomeCargo = nomeCargo;
    }

    public String getNomeDepartamento() {
        return nomeDepartamento;
    }

    public void setNomeDepartamento(String nomeDepartamento) {
        this.nomeDepartamento = nomeDepartamento;
    }

    @Override
    public String toString() {
        return nome + " (" + cpf + ")";
    }
}
