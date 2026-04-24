import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Testes do padrão Observer com Inversão de Controle via callbacks.
 * Sem dependências externas — executa com:
 *     javac Main.java TestObserver.java
 *     java TestObserver
 */
public class TestObserver {

    private static int passes = 0;
    private static int fails  = 0;

    public static void main(String[] args) {
        System.out.println("Executando testes do Observer (IoC / callbacks)...\n");

        testLambdaCallback();
        testMethodReferenceCallback();
        testMultipleObservers();
        testRemoveObserver();
        testSetTemperaturaNotifica();
        testSetPhNotifica();
        testSetUmidadeNotifica();
        testSensorStateInCallback();
        testNoObservers();
        testCidadeNaoImplementaObserver();
        testCidadeCallbackImprimeDados();
        testOrdemDeRegistro();

        System.out.println();
        System.out.println("==============================");
        System.out.println("Resultado: " + passes + " passou, " + fails + " falhou");
        if (fails > 0) System.exit(1);
    }

    // ---------------- helpers ----------------

    private static void assertTrue(String desc, boolean cond) {
        if (cond) {
            passes++;
            System.out.println("  [PASS] " + desc);
        } else {
            fails++;
            System.out.println("  [FAIL] " + desc);
        }
    }

    private static void assertEquals(String desc, Object expected, Object actual) {
        boolean eq = (expected == null ? actual == null : expected.equals(actual));
        assertTrue(desc + " (esperado=" + expected + ", obtido=" + actual + ")", eq);
    }

    private static void assertEqualsD(String desc, double expected, double actual) {
        assertTrue(desc + " (esperado=" + expected + ", obtido=" + actual + ")", expected == actual);
    }

    private static String capturarStdout(Runnable acao) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(buf));
        try {
            acao.run();
        } finally {
            System.setOut(original);
        }
        return buf.toString();
    }

    // ---------------- testes ----------------

    static void testLambdaCallback() {
        System.out.println("testLambdaCallback:");
        Sensor s = new Sensor("X");
        AtomicBoolean chamado = new AtomicBoolean(false);
        s.addObserver(sensor -> chamado.set(true));
        s.setMedicoes(1, 2, 3);
        assertTrue("lambda registrada é chamada ao notificar", chamado.get());
    }

    static void testMethodReferenceCallback() {
        System.out.println("testMethodReferenceCallback:");
        class Receptor {
            boolean chamado = false;
            Sensor ultimo = null;
            void receber(Sensor s) {
                chamado = true;
                ultimo = s;
            }
        }
        Receptor r = new Receptor();
        Sensor s = new Sensor("X");
        s.addObserver(r::receber);
        s.setMedicoes(1, 2, 3);
        assertTrue("method reference funciona como callback", r.chamado);
        assertTrue("method reference recebe o sensor correto", r.ultimo == s);
    }

    static void testMultipleObservers() {
        System.out.println("testMultipleObservers:");
        Sensor s = new Sensor("X");
        AtomicInteger contador = new AtomicInteger(0);
        s.addObserver(sensor -> contador.incrementAndGet());
        s.addObserver(sensor -> contador.incrementAndGet());
        s.addObserver(sensor -> contador.incrementAndGet());
        s.setMedicoes(1, 2, 3);
        assertEquals("três callbacks registrados → três invocações", 3, contador.get());
    }

    static void testRemoveObserver() {
        System.out.println("testRemoveObserver:");
        Sensor s = new Sensor("X");
        AtomicInteger contador = new AtomicInteger(0);
        Observer<Sensor> cb = sensor -> contador.incrementAndGet();
        s.addObserver(cb);
        s.setMedicoes(1, 2, 3); // conta 1
        s.removeObserver(cb);
        s.setMedicoes(4, 5, 6); // não deve contar
        assertEquals("após remover, callback não é mais chamado", 1, contador.get());
    }

    static void testSetTemperaturaNotifica() {
        System.out.println("testSetTemperaturaNotifica:");
        Sensor s = new Sensor("X");
        AtomicBoolean c = new AtomicBoolean(false);
        s.addObserver(sensor -> c.set(true));
        s.setTemperatura(25.0);
        assertTrue("setTemperatura dispara callback", c.get());
    }

    static void testSetPhNotifica() {
        System.out.println("testSetPhNotifica:");
        Sensor s = new Sensor("X");
        AtomicBoolean c = new AtomicBoolean(false);
        s.addObserver(sensor -> c.set(true));
        s.setPh(7.0);
        assertTrue("setPh dispara callback", c.get());
    }

    static void testSetUmidadeNotifica() {
        System.out.println("testSetUmidadeNotifica:");
        Sensor s = new Sensor("X");
        AtomicBoolean c = new AtomicBoolean(false);
        s.addObserver(sensor -> c.set(true));
        s.setUmidade(80.0);
        assertTrue("setUmidade dispara callback", c.get());
    }

    static void testSensorStateInCallback() {
        System.out.println("testSensorStateInCallback:");
        Sensor s = new Sensor("Manaus");
        AtomicReference<Sensor> recebido = new AtomicReference<>();
        s.addObserver(sensor -> recebido.set(sensor));
        s.setMedicoes(29.5, 6.8, 85.0);
        Sensor r = recebido.get();
        assertTrue("callback recebe o próprio sensor (this)", r == s);
        assertEquals("localização preservada", "Manaus", r.getLocalizacao());
        assertEqualsD("temperatura atualizada", 29.5, r.getTemperatura());
        assertEqualsD("pH atualizado", 6.8, r.getPh());
        assertEqualsD("umidade atualizada", 85.0, r.getUmidade());
    }

    static void testNoObservers() {
        System.out.println("testNoObservers:");
        Sensor s = new Sensor("X");
        try {
            s.setMedicoes(1, 2, 3);
            assertTrue("setMedicoes sem observadores não quebra", true);
        } catch (Exception e) {
            assertTrue("setMedicoes sem observadores não quebra (" + e + ")", false);
        }
    }

    static void testCidadeNaoImplementaObserver() {
        System.out.println("testCidadeNaoImplementaObserver:");
        boolean implementa = Observer.class.isAssignableFrom(Cidade.class);
        assertTrue("Cidade NÃO implementa Observer (IoC por callback)", !implementa);
    }

    static void testCidadeCallbackImprimeDados() {
        System.out.println("testCidadeCallbackImprimeDados:");
        Sensor s = new Sensor("Belém");
        Cidade poa = new Cidade("POA");
        String saida = capturarStdout(() -> {
            s.addObserver(poa::receberMedicao);
            s.setMedicoes(31.2, 7.1, 78.5);
        });
        assertTrue("saída contém a cidade POA",    saida.contains("POA"));
        assertTrue("saída contém o sensor Belém",  saida.contains("Belém"));
        assertTrue("saída contém a temperatura",   saida.contains("31.2"));
        assertTrue("saída contém o pH",            saida.contains("7.1"));
        assertTrue("saída contém a umidade",       saida.contains("78.5"));
    }

    static void testOrdemDeRegistro() {
        System.out.println("testOrdemDeRegistro:");
        Sensor s = new Sensor("X");
        StringBuilder ordem = new StringBuilder();
        s.addObserver(sensor -> ordem.append("A"));
        s.addObserver(sensor -> ordem.append("B"));
        s.addObserver(sensor -> ordem.append("C"));
        s.setMedicoes(1, 2, 3);
        assertEquals("callbacks são invocados na ordem de registro", "ABC", ordem.toString());
    }
}
