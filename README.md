# Calculadora de Saúde e IMC - Android (Jetpack Compose)

Este projeto é uma aplicação Android desenvolvida para a disciplina de Programação para Dispositivos Móveis. O objetivo é vai além de uma calculadora de IMC simples, oferecendo uma suíte completa de monitoramento de métricas de saúde com persistência de dados local.

## 📱 Funcionalidades

O aplicativo realiza o cálculo e monitoramento das seguintes métricas:

*   **Cálculo de IMC (Índice de Massa Corporal):** Com classificação detalhada (Abaixo do peso, Normal, Obesidade, etc.).
*   **TMB (Taxa Metabólica Basal):** Cálculo da energia gasta em repouso (Fórmula Mifflin-St Jeor).
*   **Peso Ideal:** Estimativa baseada na fórmula de Devine.
*   **Necessidade Calórica Diária:** Baseada no nível de atividade física selecionado.

### Funcionalidades Extras Implementadas
*   ✅ **Histórico Completo:** Persistência de todas as medições em banco de dados local (Room).
*   ✅ **Gráficos de Evolução:** Visualização gráfica da progressão do IMC, Peso e TMB ao longo do tempo (Biblioteca YCharts).
*   ✅ **Exportação de Dados:** Funcionalidade para exportar e compartilhar o histórico em formato **.CSV** (compatível com Excel).
*   ✅ **Sistema de Métricas:** Validação robusta de entradas (peso, altura, idade).

## 🛠 Tecnologias e Arquitetura

O projeto foi refatorado seguindo as boas práticas de desenvolvimento Android moderno:

*   **Linguagem:** Kotlin
*   **UI Toolkit:** Jetpack Compose (Material Design 3)
*   **Arquitetura:** MVVM (Model-View-ViewModel)
    *   **View:** Composables (`Home`, `HistoryScreen`, `GraphScreen`, `TMBScreen`).
    *   **ViewModel:** Gerenciamento de estado e comunicação com repositório (`StateFlow`).
    *   **Model:** Camada de dados com Room e Repository Pattern.
*   **Persistência:** Room Database (SQLite).
*   **Assincronismo:** Kotlin Coroutines & Flow.
*   **Injeção de Dependências:** Manual (via `ViewModelFactory`).
*   **Gráficos:** YCharts.

## 📸 Telas do Aplicativo

1.  **Home:** Formulário de entrada de dados (Idade, Peso, Altura, Sexo).
2.  **Detalhes:** Exibição detalhada das métricas calculadas e ajuste de atividade física.
3.  **Histórico:** Lista cronológica das medições salvas.
4.  **Gráficos:** Visualização da evolução do usuário.

## 🚀 Como Executar

1.  Clone este repositório.
2.  Abra o projeto no Android Studio (Ladybug ou superior recomendado).
3.  Sincronize o Gradle.
4.  Execute em um emulador ou dispositivo físico (Android 8.0+).

## 👥 Autores

*   **André Noro Crivellenti**
*   **Leonardo Rodrigues Oliveira Saraiva**

---
*Trabalho desenvolvido para a disciplina de PDM - UFU 2025.*
