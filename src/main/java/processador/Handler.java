package processador;


import io.EnviadorEmail;

public class Handler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        EnviadorEmail enviador = new EnviadorEmail();
        String mensagem = "Erro na " + t.getName() + e;
        enviador.enviaEmail("destino@gmail.com", mensagem);
        System.out.println(mensagem);

    }
}
