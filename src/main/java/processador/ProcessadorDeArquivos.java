package processador;

import dto.RelatorioNF;
import io.EscritorCSV;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class ProcessadorDeArquivos {

    private final EscritorCSV escritor = new EscritorCSV();
    private final RelatorioNFConversor conversor = new RelatorioNFConversor();
    private final List<Future<Map<String, BigDecimal>>> futures = new ArrayList<>();

    public void processaArquivosDo(String diretorio) throws ExecutionException, InterruptedException, TimeoutException {

        ExecutorService threadPool = Executors.newFixedThreadPool(2, new FabricaThreads());
        Map<String, BigDecimal> totaisPorDestinatario = new HashMap<>();

        Set<File> arquivos = listFilesFrom(diretorio);

        BarraDeProgresso barraDeProgresso = new BarraDeProgresso(arquivos.size());

        for (File arquivo : arquivos) {
            LeituraParalelaArquivos leituraParalelaArquivos = new LeituraParalelaArquivos(arquivo, barraDeProgresso);
            Future<Map<String, BigDecimal>> futureTotal = threadPool.submit(leituraParalelaArquivos);
            futures.add(futureTotal);
        }

        for (Future<Map<String, BigDecimal>> future : futures) {
            Map<String, BigDecimal> fut = future.get();
            totaisPorDestinatario.putAll(fut);
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
