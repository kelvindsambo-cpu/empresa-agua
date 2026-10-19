# Opção 6 — MainFrame, Dashboard, testes e apoio à equipa

**Nível:** fácil (na lógica de negócio) + responsabilidade transversal de apoio  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/dashboard-interface`

## Resultado esperado

Interface principal navegável, padrões visuais consistentes, Dashboard com dados reais e contributo activo para testes e correcções de usabilidade.

## Classes e ficheiros

```text
view/MainFrame.java
view/panels/DashboardPanel.java
controller/DashboardController.java
dao/DashboardDAO.java
view/components/               # componentes reutilizáveis aprovados
# documentação de screenshots e testes, quando aplicável
```

## Tarefas

- [ ] Construir `MainFrame` com menu lateral e conteúdo central em `CardLayout`.
- [ ] Criar pontos de navegação para os nove módulos, com placeholders apenas enquanto o módulo ainda não estiver pronto.
- [ ] Garantir que ADMIN/OPERADOR vêem menus adequados à sessão, recebida da Opção 1.
- [ ] Criar `DashboardPanel` com resumos SQL reais: contagem de clientes, contratos activos, facturas pendentes, reclamações abertas (ajustar consultas ao esquema congelado).
- [ ] Definir padrões comuns de botões, filtros, tamanhos, tabelas e diálogos.
- [ ] Ajudar os colegas a ajustar GridLayout, BorderLayout, JTable, JScrollPane, formulários e feedback visual.
- [ ] Executar testes manuais dos módulos integrados e abrir Issues com resultados reproduzíveis.
- [ ] Organizar screenshots ilustrativos e verificar documentação de execução.
- [ ] Rever visualmente os painéis para consistência, sem alterar regras de negócio dos responsáveis.

## Como apoiar os outros

Combinar com cada colega pequenas subtarefas: arrumar formulário, criar modelo de `JTable`, melhorar filtros, configurar `JScrollPane`, corrigir layout ou documentar casos de teste. Fazer mudanças em PRs atribuídos e coordenados; nunca substituir a autoria dos colegas sem acordo.

## Critérios de aceitação

- [ ] Troca de painéis não abre dezenas de JFrames.
- [ ] Dashboard não contém números fictícios fixos no código.
- [ ] Menus correspondem ao perfil e actualizam após autenticação.
- [ ] `SwingWorker` (quando necessário) não bloqueia interface.
- [ ] Pelo menos um registo de teste documentado em cada módulo integrado.
- [ ] Guia de execução e lista de problemas conhecidos estão actualizados.

**Cuidado:** embora seja a opção mais fácil, o Dashboard exige consultas JDBC e coordenação com os outros módulos.
