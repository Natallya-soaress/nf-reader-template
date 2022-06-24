package processador;

import dto.NotaFiscalItem;
import dto.RelatorioNF;
import io.EscritorCSV;
import io.LeitorCSV;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class ProcessadorDeArquivos {

    private final LeitorCSV<NotaFiscalItem> leitor = new LeitorCSV<>();
    private final EscritorCSV escritor = new EscritorCSV();
    private final RelatorioNFConversor conversor = new RelatorioNFConversor();

    public void processaArquivosDo(String diretorio) {

        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        Map<String, BigDecimal> totaisPorDestinatario = new ConcurrentHashMap<>();

        Set<File> arquivos = listFilesFrom(diretorio);

        BarraDeProgresso barraDeProgresso = new BarraDeProgresso(arquivos.size());

        for (File arquivo : arquivos) {
            LeituraParalelaArquivos leituraParalelaArquivos = new LeituraParalelaArquivos(arquivo, barraDeProgresso, totaisPorDestinatario);
            threadPool.execute(leituraParalelaArquivos);
        }

        List<RelatorioNF> relatorioNFs = conversor.converte(totaisPorDestinatario);

        escritor.escreve(relatorioNFs, Path.of("src/main/resources/relatorio/relatorio.csv"));
    }

    private Set<File> listFilesFrom(String diretorio) {
        return Stream.of(requireNonNull(new File(diretorio).listFiles()))
                .filter(file -> !file.isDirectory())
                .collect(Collectors.toSet());
    }
}
