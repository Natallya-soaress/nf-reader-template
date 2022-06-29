package processador;

import dto.NotaFiscalItem;
import io.LeitorCSV;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

public class LeituraParalelaArquivos implements Callable<Map<String, BigDecimal>> {

    private File arquivo;
    private BarraDeProgresso barraDeProgresso;
    private Map<String, BigDecimal> totaisPorDestinatario;
    private final LeitorCSV<NotaFiscalItem> leitorCSV = new LeitorCSV<>();

    public LeituraParalelaArquivos(File arquivo, BarraDeProgresso barraDeProgresso) {
        this.arquivo = arquivo;
        this.barraDeProgresso = barraDeProgresso;
    }

    @Override
    public Map<String, BigDecimal> call() {

        Map<String, BigDecimal> totaisPorDestinatario = new HashMap<>();

        checaSeEhCSV(arquivo);

        List<NotaFiscalItem> notaFiscalItems = leitorCSV.leia(arquivo, NotaFiscalItem.class);

        agrupaTotal(notaFiscalItems, totaisPorDestinatario);

        barraDeProgresso.incrementa();

        return totaisPorDestinatario;
    }

    private void agrupaTotal(List<NotaFiscalItem> notaFiscalItems, Map<String, BigDecimal> totaisPorDestinatario) {

        notaFiscalItems.forEach(nf -> {

            BigDecimal valorAnterior = totaisPorDestinatario.putIfAbsent(nf.getNomeDestinatario(), nf.getValorTotal());

            if (Objects.nonNull(valorAnterior)) {
                totaisPorDestinatario.put(nf.getNomeDestinatario(), valorAnterior.add(nf.getValorTotal()));
            }
        });
    }

    private void checaSeEhCSV(File arquivo) {

        var nomeDoArquivo = arquivo.getName();
        if (!nomeDoArquivo.endsWith(".csv")) {
            throw new IllegalArgumentException("Formato inválido do arquivo: " + nomeDoArquivo);
        }
    }
}
