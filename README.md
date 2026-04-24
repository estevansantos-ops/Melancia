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

- **`Subject`** — classe base com `addObserver`, `removeObserver` e `notifyObservers`.
- **`Observer`** — interface com o método `update(Subject s)`.
- **`Sensor extends Subject`** — notifica os observadores sempre que um parâmetro é alterado.
- **`Cidade implements Observer`** — recebe e imprime as medições do sensor.

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
