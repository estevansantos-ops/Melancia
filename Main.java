/**
 * Projeto Orientado a Objetos - Padrão de Projeto Observer
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
import java.util.Iterator;
import java.util.Scanner;

/**
 * Classe Subject (implementação interna do padrão)
 * Todo sujeito deve herdar dessa classe.
 * Inclui métodos para adicionar, remover e notificar os observadores.
 */
class Subject {

    private List<Observer> observers = new ArrayList<Observer>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        Iterator<Observer> it = observers.iterator();
        while (it.hasNext()) {
            Observer obs = it.next();
            obs.update(this);
        }
    }
}

/**
 * Interface Observer (implementação interna do padrão)
 * Todo observador deve implementar essa interface.
 */
interface Observer {
    public void update(Subject s);
}

/**
 * Sensor é um sujeito (objeto que pode ser observado).
 * Representa um sensor instalado na Amazônia que coleta
 * temperatura, pH e umidade do ar.
 */
class Sensor extends Subject {

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

    /**
     * Atualiza as medições do sensor e notifica os observadores interessados.
     */
    public void setMedicoes(double temperatura, double ph, double umidade) {
        this.temperatura = temperatura;
        this.ph = ph;
        this.umidade = umidade;
        notifyObservers();
    }

    /**
     * Atualiza apenas a temperatura e notifica os observadores.
     */
    public void setTemperatura(double temperatura) {
        this.temperatura = temperatura;
        notifyObservers();
    }

    /**
     * Atualiza apenas o pH e notifica os observadores.
     */
    public void setPh(double ph) {
        this.ph = ph;
        notifyObservers();
    }

    /**
     * Atualiza apenas a umidade e notifica os observadores.
     */
    public void setUmidade(double umidade) {
        this.umidade = umidade;
        notifyObservers();
    }
}

/**
 * Cidade é um observador dos sensores da Amazônia.
 * Cada cidade recebe atualizações dos sensores que ela está
 * interessada em monitorar e imprime as informações coletadas.
 */
class Cidade implements Observer {

    private String nome;

    public Cidade(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /**
     * Método chamado quando um sensor observado atualiza suas medições.
     */
    public void update(Subject s) {
        Sensor sensor = (Sensor) s;
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

        // Cada cidade se registra nos sensores que deseja monitorar

        // BSB monitora todos os sensores
        sensorManaus.addObserver(bsb);
        sensorBelem.addObserver(bsb);
        sensorRioBranco.addObserver(bsb);

        // RJ monitora Manaus e Belém
        sensorManaus.addObserver(rj);
        sensorBelem.addObserver(rj);

        // SJC monitora apenas Rio Branco
        sensorRioBranco.addObserver(sjc);

        // SP monitora Manaus e Rio Branco
        sensorManaus.addObserver(sp);
        sensorRioBranco.addObserver(sp);

        // POA monitora apenas Belém
        sensorBelem.addObserver(poa);

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

            // Aplica o novo valor e dispara update (notifyObservers)
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