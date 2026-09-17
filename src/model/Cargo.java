package model;

import java.math.BigDecimal;

public class Cargo {

    private int id;
    private String nome;
    private BigDecimal salarioBase;

    public Cargo(int id, String nome, BigDecimal salarioBase) {
        this.id = id;
        this.nome = nome;
        this.salarioBase = salarioBase;
    }

    public Cargo(String nome, BigDecimal salarioBase) {
        this(0, nome, salarioBase);
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

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    @Override
    public String toString() {
        return nome + " (R$ " + salarioBase + ")";
    }
}
