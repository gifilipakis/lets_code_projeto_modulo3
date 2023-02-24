package pagamentos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src\\pagamentos\\pagamentos.csv"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] splits = line.split(";");
            Pagamento pagamento = new Pagamento(splits);
            Thread thread = new Thread(pagamento);
            thread.start();
        }
        reader.close();
    }
    
}
