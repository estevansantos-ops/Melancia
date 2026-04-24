# PCD-ProjOO — Plataforma de Coleta de Dados

Atividade da disciplina de **Projeto Orientado a Objetos (ProjOO)** que implementa
uma **Plataforma de Coleta de Dados Ambientais da Amazônia** utilizando o padrão
de projeto **Observer**.

Baseado no exemplo do Cap. 6 do livro *Engenharia de Software Moderna* do
Prof. Marco Tulio Valente.

## Descrição

O sistema simula sensores instalados em cidades da Amazônia que coletam dados
de **temperatura**, **pH** e **umidade do ar**. Cada cidade observadora se
inscreve nos sensores de interesse e recebe atualizações automaticamente
sempre que uma nova medição é realizada.

### Sujeitos (Subjects) — Sensores na Amazônia
- Manaus
- Belém
- Rio Branco

### Observadores (Observers) — Cidades monitoras
- **BSB** — Brasília
- **RJ** — Rio de Janeiro
- **SJC** — São José dos Campos
- **SP** — São Paulo
- **POA** — Porto Alegre

### Relação entre sensores e observadores

| Cidade | Manaus | Belém | Rio Branco |
|--------|:------:|:-----:|:----------:|
| BSB    |   X    |   X   |     X      |
| RJ     |   X    |   X   |            |
| SJC    |        |       |     X      |
| SP     |   X    |       |     X      |
| POA    |        |   X   |            |

## Estrutura do projeto

- [Main.java](Main.java) — código-fonte completo (classes `Subject`, `Observer`, `Sensor`, `Cidade` e `Main`).
- `*.class` — arquivos compilados.

## Padrão Observer

- **`Subject<T>`** — classe base genérica com `addObserver`, `removeObserver` e `notifyObservers`. Mantém a lista de callbacks registrados.
- **`Observer<T>`** — **interface funcional** (`@FunctionalInterface`) com o método `update(T subject)`. Pode ser satisfeita por uma lambda ou por uma referência a método.
- **`Sensor extends Subject<Sensor>`** — notifica os observadores sempre que um parâmetro é alterado.
- **`Cidade`** — **não implementa** a interface `Observer`. Ela apenas expõe um método (`receberMedicao(Sensor)`) que é registrado como callback nos sensores desejados.

## O que é um *callback*?

Um **callback** é uma função (ou referência a um método) que um objeto entrega a outro para que seja **executada mais tarde**, quando um determinado evento acontecer. Em vez de quem recebeu o callback perguntar "algo mudou?", é ele quem **chama de volta** a função original no momento certo.

No nosso projeto:

- A classe `Cidade` define o método `receberMedicao(Sensor sensor)`.
- Esse método é registrado no sensor usando uma **referência a método** (`bsb::receberMedicao`).
- Quando o sensor realiza uma nova medição, ele percorre sua lista de callbacks e **chama cada um deles**, passando a si mesmo como argumento.

```java
sensorManaus.addObserver(bsb::receberMedicao); // registra o callback
sensorManaus.setTemperatura(30.0);             // sensor chama o callback de BSB
```

A Cidade não sabe *quando* será chamada — apenas oferece uma função ao Sensor e espera. Essa é a ideia de "**não nos chame, nós te chamamos**" (Princípio de Hollywood).

## Como foi implementada a Inversão de Controle no Observer?

Na versão "clássica" do Observer, a Cidade precisaria declarar `implements Observer` e sobrescrever `update(Subject s)`. Isso cria um acoplamento forte: a Cidade fica amarrada à interface do padrão.

Nesta versão aplicamos **Inversão de Controle (IoC)** usando **interface funcional + callback**:

1. **`Observer<T>` virou interface funcional** — tem um único método abstrato, o que permite usá-la com lambdas e method references:
   ```java
   @FunctionalInterface
   interface Observer<T> {
       void update(T subject);
   }
   ```

2. **`Cidade` deixou de implementar `Observer`** — agora é apenas uma classe comum que oferece um método compatível com a assinatura esperada:
   ```java
   public void receberMedicao(Sensor sensor) { ... }
   ```

3. **O registro é feito por *method reference*** no `main`, entregando a função ao sensor:
   ```java
   sensorManaus.addObserver(bsb::receberMedicao);
   ```

4. **O fluxo de controle se inverte**: quem decide *quando* a `Cidade` é chamada não é a própria Cidade, e sim o `Sensor`, dentro de `notifyObservers(this)`:
   ```java
   protected void notifyObservers(T self) {
       for (Observer<T> obs : observers) {
           obs.update(self); // callback disparado pelo sujeito
       }
   }
   ```

### Resumindo a inversão

| Sem IoC (polling)                                   | Com IoC (callback / Observer) |
|-----------------------------------------------------|--------------------------------|
| A Cidade perguntaria ao Sensor se mudou algo.       | A Cidade registra uma função e espera. |
| A Cidade controla *quando* lê os dados.             | O Sensor controla *quando* a Cidade é notificada. |
| Forte acoplamento e código sincrono repetitivo.     | Acoplamento fraco — só pela assinatura do callback. |

## Como compilar e executar

Requer **JDK 8+** instalado.

```bash
javac Main.java
java Main
```

## Funcionamento

Ao iniciar, o programa:

1. Cria os sensores e as cidades observadoras.
2. Registra cada cidade nos sensores que ela deseja monitorar.
3. Executa uma rodada inicial de medições em Manaus, Belém e Rio Branco.
4. Exibe um **menu interativo** que permite escolher um sensor e alterar
   individualmente temperatura, pH ou umidade. A cada alteração, todas as
   cidades registradas naquele sensor recebem a nova medição.

### Menu

```
Acesso a Sensor:
  1 - Manaus
  2 - Belém
  3 - Rio Branco
  E - Exit
```

Após escolher o sensor:

```
  1 - Temperatura
  2 - pH
  3 - Umidade
  V - Voltar
```

## Autor

Atividade desenvolvida para a disciplina de Projeto Orientado a Objetos.
