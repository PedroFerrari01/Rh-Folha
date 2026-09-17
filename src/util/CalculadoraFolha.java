package util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula os descontos de INSS e IRRF usando tabelas progressivas
 * (valores de referência simplificados, mesmo princípio da tabela oficial:
 * cada faixa de salário paga sua própria alíquota, não a alíquota total
 * sobre o valor inteiro).
 */
public class CalculadoraFolha {

    private static final BigDecimal[] FAIXAS_INSS = {
            new BigDecimal("1412.00"), new BigDecimal("2666.68"),
            new BigDecimal("4000.03"), new BigDecimal("7786.02")
    };
    private static final BigDecimal[] ALIQUOTAS_INSS = {
            new BigDecimal("0.075"), new BigDecimal("0.09"),
            new BigDecimal("0.12"), new BigDecimal("0.14")
    };

    private static final BigDecimal[] FAIXAS_IRRF = {
            new BigDecimal("2259.20"), new BigDecimal("2826.65"),
            new BigDecimal("3751.05"), new BigDecimal("4664.68")
    };
    private static final BigDecimal[] ALIQUOTAS_IRRF = {
            BigDecimal.ZERO, new BigDecimal("0.075"),
            new BigDecimal("0.15"), new BigDecimal("0.225")
    };
    private static final BigDecimal ALIQUOTA_IRRF_TOPO = new BigDecimal("0.275");
    private static final BigDecimal[] DEDUCOES_IRRF = {
            BigDecimal.ZERO, new BigDecimal("169.44"),
            new BigDecimal("381.44"), new BigDecimal("662.77")
    };
    private static final BigDecimal DEDUCAO_IRRF_TOPO = new BigDecimal("896.00");

    /**
     * INSS progressivo: cada faixa contribui só com sua alíquota sobre a
     * parte do salário que cai dentro dela (igual à tabela oficial do INSS).
     */
    public static BigDecimal calcularInss(BigDecimal salarioBruto) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal faixaAnterior = BigDecimal.ZERO;

        for (int i = 0; i < FAIXAS_INSS.length; i++) {
            BigDecimal tetoFaixa = FAIXAS_INSS[i];
            if (salarioBruto.compareTo(faixaAnterior) <= 0) {
                break;
            }
            BigDecimal baseNaFaixa = salarioBruto.min(tetoFaixa).subtract(faixaAnterior);
            total = total.add(baseNaFaixa.multiply(ALIQUOTAS_INSS[i]));
            faixaAnterior = tetoFaixa;
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * IRRF simplificado: alíquota efetiva por faixa com parcela a deduzir,
     * calculado sobre a base já descontado o INSS.
     */
    public static BigDecimal calcularIrrf(BigDecimal baseCalculo) {
        if (baseCalculo.compareTo(FAIXAS_IRRF[0]) <= 0) {
            return BigDecimal.ZERO;
        }
        for (int i = 1; i < FAIXAS_IRRF.length; i++) {
            if (baseCalculo.compareTo(FAIXAS_IRRF[i]) <= 0) {
                BigDecimal imposto = baseCalculo.multiply(ALIQUOTAS_IRRF[i]).subtract(DEDUCOES_IRRF[i]);
                return imposto.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            }
        }
        BigDecimal imposto = baseCalculo.multiply(ALIQUOTA_IRRF_TOPO).subtract(DEDUCAO_IRRF_TOPO);
        return imposto.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    /** Desconto de vale-transporte: 6% do salário bruto (regra padrão da CLT), limitado ao próprio salário. */
    public static BigDecimal calcularDescontoVt(BigDecimal salarioBruto) {
        return salarioBruto.multiply(new BigDecimal("0.06")).setScale(2, RoundingMode.HALF_UP);
    }
}
