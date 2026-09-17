package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

public class FolhaPagamento {

    private int id;
    private int funcionarioId;
    private YearMonth mesReferencia;
    private BigDecimal salarioBruto;
    private BigDecimal descontoInss;
    private BigDecimal descontoIrrf;
    private BigDecimal descontoVt;
    private BigDecimal beneficioVa;
    private BigDecimal salarioLiquido;
    private LocalDateTime dataGeracao;

    // Auxiliar para exibição
    private String nomeFuncionario;

    public FolhaPagamento(int id, int funcionarioId, YearMonth mesReferencia, BigDecimal salarioBruto,
                           BigDecimal descontoInss, BigDecimal descontoIrrf, BigDecimal descontoVt,
                           BigDecimal beneficioVa, BigDecimal salarioLiquido, LocalDateTime dataGeracao) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.mesReferencia = mesReferencia;
        this.salarioBruto = salarioBruto;
        this.descontoInss = descontoInss;
        this.descontoIrrf = descontoIrrf;
        this.descontoVt = descontoVt;
        this.beneficioVa = beneficioVa;
        this.salarioLiquido = salarioLiquido;
        this.dataGeracao = dataGeracao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFuncionarioId() {
        return funcionarioId;
    }

    public YearMonth getMesReferencia() {
        return mesReferencia;
    }

    public BigDecimal getSalarioBruto() {
        return salarioBruto;
    }

    public BigDecimal getDescontoInss() {
        return descontoInss;
    }

    public BigDecimal getDescontoIrrf() {
        return descontoIrrf;
    }

    public BigDecimal getDescontoVt() {
        return descontoVt;
    }

    public BigDecimal getBeneficioVa() {
        return beneficioVa;
    }

    public BigDecimal getSalarioLiquido() {
        return salarioLiquido;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public String getNomeFuncionario() {
        return nomeFuncionario;
    }

    public void setNomeFuncionario(String nomeFuncionario) {
        this.nomeFuncionario = nomeFuncionario;
    }

    public String gerarHolerite() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== HOLERITE - ").append(mesReferencia).append(" ==========\n");
        sb.append("Funcionário: ").append(nomeFuncionario).append("\n");
        sb.append("---------------------------------------------------\n");
        sb.append(String.format("Salário bruto:        R$ %10.2f%n", salarioBruto));
        sb.append(String.format("(-) INSS:             R$ %10.2f%n", descontoInss));
        sb.append(String.format("(-) IRRF:              R$ %10.2f%n", descontoIrrf));
        sb.append(String.format("(-) Vale-transporte:  R$ %10.2f%n", descontoVt));
        sb.append(String.format("(+) Vale-alimentação: R$ %10.2f%n", beneficioVa));
        sb.append("---------------------------------------------------\n");
        sb.append(String.format("SALÁRIO LÍQUIDO:      R$ %10.2f%n", salarioLiquido));
        sb.append("=====================================================");
        return sb.toString();
    }
}
