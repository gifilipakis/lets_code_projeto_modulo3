package pagamentos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Pagamento implements Runnable {

    String clienteNome;
    LocalDate dataVencimento;
    double valor;
    double classificacao;
    
    public Pagamento(String[] splits) {
        this.clienteNome = splits[0];
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataVencimento = LocalDate.parse(splits[1], df);
        this.valor = Double.parseDouble(splits[2]);
        this.classificacao = Double.parseDouble(splits[2]);
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public double getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(double classificacao) {
        this.classificacao = classificacao;
    }

    @Override
    public void run() {
        // System.out.println("Pagamento "+this.getClienteNome()+" | valor inicial: "+this.getValor()+" | prazo: "+this.getDataVencimento());
        long periodoDias = LocalDate.now().until(dataVencimento, ChronoUnit.DAYS);
        long periodoMes = LocalDate.now().until(dataVencimento, ChronoUnit.MONTHS);
        
        // System.out.println(periodoDias+" | "+periodoMes);
        if(periodoDias < 0) {
            periodoDias = -periodoDias;
            periodoMes = -periodoMes;
            // System.out.println("Está atrasado");
            if(periodoDias >= 7) {
                Double multa = ((periodoDias%7)*this.valor*0.01);
                // System.out.println("Multa: "+multa);
                this.valor += multa;
                if(periodoMes > 0) {
                    double novaClassificacao = this.classificacao - periodoMes;
                    this.classificacao = Math.max(0, (int) novaClassificacao);
                }
                this.valor += 50;
            }
        } else {
            // System.out.println("Está no prazo");
            Double desconto = Math.min(500, this.valor*(this.classificacao/100));
            // System.out.println("Desconto: "+desconto);
            Double novoValor = this.valor - desconto;
            this.valor = Math.max(this.valor*0.05, novoValor);
        }

        escreveArquivo();
        
    }

    public void escreveArquivo() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-yyyy");
        String str = "src/pagamentos/atualizacoes/pagamentos_"+this.dataVencimento.format(dateTimeFormatter)+".txt";
        Path path = Paths.get(str);
        try {
            Files.createFile(path);
        } catch (IOException e) {
            System.out.println("Arquivo "+str+" já existe");
        }
        
        String registro = "Pagamento "+this.clienteNome+" | Prazo: "+this.dataVencimento+" | novo valor: "+this.valor+"\n";
        try {
            Files.writeString(path, registro, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
