/**
 * Projeto Orientado a Objetos - Padrão de Projeto Observer
 * (versão com Inversão de Controle via callbacks)
 *
 * Sistema de coleta de dados ambientais da Amazônia
 *
 * Sujeitos (Subjects): Sensores localizados na Amazônia
 *   - Coletam temperatura, pH e umidade do ar
 *
 * Observadores (Observers): Cidades que monitoram os sensores
 *   - BSB (Brasília), RJ (Rio de Janeiro), SJC (São José dos Campos),
 *     SP (São Paulo) e POA (Porto Alegre)
 *
 * Baseado no exemplo do Cap. 6 - Engenharia de Software Moderna
 * Prof. Marco Tulio Valente
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Interface funcional Observer — representa o "contrato do callback".
 *
 * Por ser @FunctionalInterface (um único método abstrato), pode ser
 * satisfeita por uma expressão lambda ou por uma referência a método
 * (ex.: cidade::receberMedicao). Assim, a classe observadora NÃO
 * precisa declarar "implements Observer" — basta fornecer uma função
 * compatível com a assinatura update(T).
 */
@FunctionalInterface
interface Observer<T> {
    void update(T subject);
}

/**
 * Classe Subject — guarda a lista de callbacks registrados e
 * decide QUANDO invocá-los. Essa é a essência da Inversão de
 * Controle (IoC): o observador não fica perguntando se algo mudou;
 * ele registra uma função e é o sujeito quem a chama de volta.
 *
 * "Don't call us, we'll call you." (Princípio de Hollywood)
 */
class Subject<T> {

    private List<Observer<T>> observers = new ArrayList<Observer<T>>();

    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(T self) {
        for (Observer<T> obs : observers) {
            obs.update(self); // callback: o Subject invoca a função registrada
        }
    }
}

/**
 * Sensor é um sujeito (objeto que pode ser observado).
 * Representa um sensor instalado na Amazônia que coleta
 * temperatura, pH e umidade do ar.
 */
class Sensor extends Subject<Sensor> {

    private String localizacao; // ex: "Manaus", "Belém", "Rio Branco"
    private double temperatura;
    private double ph;
    private double umidade;

    public Sensor(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public double getPh() {
        return ph;
    }

    public double getUmidade() {
        return umidade;
    }

    public void setMedicoes(double temperatura, double ph, double umidade) {
        this.temperatura = temperatura;
        this.ph = ph;
        this.umidade = umidade;
        notifyObservers(this);
    }

    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
        notifyObservers(this);
    }

    public void setPh(double ph) {
        this.ph = ph;
        notifyObservers(this);
    }

    public void setUmidade(double umidade) {
        this.umidade = umidade;
        notifyObservers(this);
    }
}

/**
 * Cidade NÃO implementa Observer. Ela apenas expõe um método
 * (receberMedicao) que pode ser usado como callback por qualquer
 * sensor. Em Main o registro é feito via method reference:
 *
 *     sensor.addObserver(cidade::receberMedicao);
 *
 * A Cidade entrega uma função ao Sensor e fica passiva — quem
 * decide quando ela será executada é o Sensor. Isso é a IoC.
 */
class Cidade {

    private String nome;

    public Cidade(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void receberMedicao(Sensor sensor) {
        System.out.println("---------------------------------------------");
        System.out.println("Cidade " + nome + " recebeu dados do sensor de "
                + sensor.getLocalizacao() + ":");
        System.out.println("  Temperatura: " + sensor.getTemperatura() + " °C");
        System.out.println("  pH:          " + sensor.getPh());
        System.out.println("  Umidade:     " + sensor.getUmidade() + " %");
    }
}

/**
 * Classe principal que demonstra o funcionamento do sistema.
 */
public class Main {

    public static void main(String[] args) {

        // Cria os sensores (sujeitos) localizados na Amazônia
        Sensor sensorManaus    = new Sensor("Manaus");
        Sensor sensorBelem     = new Sensor("Belém");
        Sensor sensorRioBranco = new Sensor("Rio Branco");

        // Array de sensores para uso no menu
        Sensor[] sensores = { sensorManaus, sensorBelem, sensorRioBranco };

        // Cria as cidades (observadores)
        Cidade bsb = new Cidade("BSB");
        Cidade rj  = new Cidade("RJ");
        Cidade sjc = new Cidade("SJC");
        Cidade sp  = new Cidade("SP");
        Cidade poa = new Cidade("POA");

        // ========= REGISTRO DOS CALLBACKS (Inversão de Controle) =========
        // Cada cidade registra o método receberMedicao como callback
        // nos sensores que deseja monitorar. Nenhuma cidade implementa
        // Observer — o acoplamento é feito apenas pela função.

        // BSB monitora todos os sensores
        sensorManaus.addObserver(bsb::receberMedicao);
        sensorBelem.addObserver(bsb::receberMedicao);
        sensorRioBranco.addObserver(bsb::receberMedicao);

        // RJ monitora Manaus e Belém
        sensorManaus.addObserver(rj::receberMedicao);
        sensorBelem.addObserver(rj::receberMedicao);

        // SJC monitora apenas Rio Branco
        sensorRioBranco.addObserver(sjc::receberMedicao);

        // SP monitora Manaus e Rio Branco
        sensorManaus.addObserver(sp::receberMedicao);
        sensorRioBranco.addObserver(sp::receberMedicao);

        // POA monitora apenas Belém
        sensorBelem.addObserver(poa::receberMedicao);

        // ========= EXECUÇÃO INICIAL =========

        System.out.println("=============================================");
        System.out.println("Sensor de Manaus realizando nova medição...");
        System.out.println("=============================================");
        sensorManaus.setMedicoes(29.5, 6.8, 85.0);

        System.out.println("\n=============================================");
        System.out.println("Sensor de Belém realizando nova medição...");
        System.out.println("=============================================");
        sensorBelem.setMedicoes(31.2, 7.1, 78.5);

        System.out.println("\n=============================================");
        System.out.println("Sensor de Rio Branco realizando nova medição...");
        System.out.println("=============================================");
        sensorRioBranco.setMedicoes(28.0, 6.5, 90.3);

        // ========= MENU INTERATIVO =========

        Scanner scanner = new Scanner(System.in);
        boolean executando = true;

        while (executando) {
            System.out.println("\n=============================================");
            System.out.println("Acesso a Sensor:");
            System.out.println("  1 - Manaus");
            System.out.println("  2 - Belém");
            System.out.println("  3 - Rio Branco");
            System.out.println("  E - Exit");
            System.out.print("Escolha uma opção: ");

            String escolhaSensor = scanner.nextLine().trim();

            if (escolhaSensor.equalsIgnoreCase("E")) {
                System.out.println("\nEncerrando o sistema. Até logo!");
                executando = false;
                continue;
            }

            Sensor sensorSelecionado = null;
            try {
                int idx = Integer.parseInt(escolhaSensor) - 1;
                if (idx >= 0 && idx < sensores.length) {
                    sensorSelecionado = sensores[idx];
                }
            } catch (NumberFormatException e) {
                // entrada inválida
            }

            if (sensorSelecionado == null) {
                System.out.println("Opção inválida! Tente novamente.");
                continue;
            }

            // Menu de parâmetros
            System.out.println("\nSensor de " + sensorSelecionado.getLocalizacao() + " selecionado.");
            System.out.println("Qual parâmetro deseja alterar?");
            System.out.println("  1 - Temperatura (atual: " + sensorSelecionado.getTemperatura() + " °C)");
            System.out.println("  2 - pH          (atual: " + sensorSelecionado.getPh() + ")");
            System.out.println("  3 - Umidade     (atual: " + sensorSelecionado.getUmidade() + " %)");
            System.out.println("  V - Voltar");
            System.out.print("Escolha uma opção: ");

            String escolhaParam = scanner.nextLine().trim();

            if (escolhaParam.equalsIgnoreCase("V")) {
                continue;
            }

            if (!escolhaParam.equals("1") && !escolhaParam.equals("2") && !escolhaParam.equals("3")) {
                System.out.println("Opção inválida! Voltando ao menu principal.");
                continue;
            }

            // Leitura do novo valor
            System.out.print("Digite o novo valor: ");
            String entradaValor = scanner.nextLine().trim().replace(",", ".");
            double novoValor;
            try {
                novoValor = Double.parseDouble(entradaValor);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido! Voltando ao menu principal.");
                continue;
            }

            // Aplica o novo valor e dispara os callbacks (notifyObservers)
            System.out.println("\n=============================================");
            System.out.println("Sensor de " + sensorSelecionado.getLocalizacao()
                    + " atualizando parâmetro...");
            System.out.println("=============================================");

            switch (escolhaParam) {
                case "1":
                    sensorSelecionado.setTemperatura(novoValor);
                    break;
                case "2":
                    sensorSelecionado.setPh(novoValor);
                    break;
                case "3":
                    sensorSelecionado.setUmidade(novoValor);
                    break;
            }
        }

        scanner.close();
    }
}
