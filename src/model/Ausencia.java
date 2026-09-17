package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Ausencia {

    private int id;
    private int funcionarioId;
    private TipoAusencia tipo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String observacao;

    public Ausencia(int id, int funcionarioId, TipoAusencia tipo, LocalDate dataInicio, LocalDate dataFim, String observacao) {
        this.id = id;
        this.funcionarioId = funcionarioId;
        this.tipo = tipo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.observacao = observacao;
    }

    public Ausencia(int funcionarioId, TipoAusencia tipo, LocalDate dataInicio, LocalDate dataFim, String observacao) {
        this(0, funcionarioId, tipo, dataInicio, dataFim, observacao);
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

    public TipoAusencia getTipo() {
        return tipo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public String getObservacao() {
        return observacao;
    }

    public long getDias() {
        return ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
    }

    @Override
    public String toString() {
        return String.format("%s: %s a %s (%d dia(s))", tipo, dataInicio, dataFim, getDias());
    }
}
