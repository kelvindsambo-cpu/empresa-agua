# Membro 6 — Janela principal, Dashboard e demonstração

**Nível:** fácil  
**Responsável:** [NOME]  
**Branch:** `interface-dashboard`

## Resultado

Uma janela principal com menu para todos os módulos, um Dashboard com números reais e uma base de dados de demonstração pronta a usar.

## Classes / ficheiros

```text
view/MainFrame.java
view/DashboardPanel.java
dao/DashboardDAO.java
database/03_dados_demo.sql
docs/guia-de-execucao.md (actualizar)
```

## Tarefas

- [ ] **Primeiro e cedo:** `MainFrame` com menu lateral (botões) + área central em `CardLayout`, e o método `adicionarModulo(String nome, JPanel painel, boolean soAdmin)`. Publicar em `main` mesmo com painéis vazios, para os colegas encaixarem os seus.
- [ ] Topo da janela: nome do funcionário e perfil (`Sessao.getUsuarioAutenticado()`) + botão "Sair" que fecha a janela e chama `Main.terminarSessaoEVoltarAoLogin()`.
- [ ] Ligar o login ao `MainFrame`: em `Main.aoAutenticar` trocar o `JOptionPane` temporário (há um `TODO`) por `new MainFrame().setVisible(true)` — combinar com o Membro 1.
- [ ] Esconder módulos `soAdmin` quando `!Sessao.isAdmin()`.
- [ ] `DashboardPanel` com 4 cartões: total de clientes, contratos activos, facturas pendentes, reclamações abertas. Botão "Actualizar". Consultas `SELECT COUNT(*)` no `DashboardDAO`.
- [ ] `03_dados_demo.sql`: alguns clientes, contratos, contadores e funcionários a mais (fictícios) para a apresentação.
- [ ] Actualizar o guia de execução com os passos realmente testados (BD → config → admin → `mvn` → login).
- [ ] Na Fase 3, correr os testes manuais T01–T13 e avisar os colegas dos problemas.

## Demonstração

Login → MainFrame; navegar pelos módulos; ADMIN vs OPERADOR; inserir um cliente e ver o Dashboard mudar; logout.

**Fora do âmbito:** gráficos, temas configuráveis, componentes Swing personalizados para os outros módulos.
