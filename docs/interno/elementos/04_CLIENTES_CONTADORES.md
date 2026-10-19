# Opção 4 — Clientes e contadores

**Nível:** médio  
**Responsável:** [NOME]  
**Branch sugerida:** `feature/clientes-contadores`

## Resultado esperado

CRUD completo de clientes, pesquisa, consulta dos contratos e gestão de contadores ligados aos contratos, com no máximo um activo.

## Classes e ficheiros

```text
model/Cliente.java
model/Contador.java
dao/ClienteDAO.java
dao/ContadorDAO.java
service/ContadorService.java
controller/ClienteController.java
controller/ContadorController.java
view/panels/ClientePanel.java
view/panels/ContadorPanel.java
view/dialogs/ClienteDialog.java
view/dialogs/ContadorDialog.java
```

## Tarefas

- [ ] Mapear os sete campos de `cliente`, sem inventar campos de morada.
- [ ] Fazer `ClienteDAO` para inserir, procurar por nome/ID, listar e actualizar.
- [ ] Criar `JTable` com pesquisa e formulários com validação.
- [ ] Validar `nome_completo`, `telefone`, rua, número e bairro obrigatórios; `nuit` opcional e único.
- [ ] Implementar `ContadorDAO`: criar contador, procurar por contrato, actualizar estado e data de remoção.
- [ ] Garantir número de série único, leitura inicial não negativa e datas consistentes.
- [ ] Fazer troca de contador com desactivação do antigo e activação do novo de forma consistente.
- [ ] Acordar com Opção 3 como escolher contratos válidos/activos.
- [ ] Entregar métodos para Opção 2 consultar contador activo e leitura inicial.
- [ ] Testar erro para segundo contador activo no mesmo contrato (`uq_contador_activo`).

## Critérios de aceitação

- [ ] Inserção/pesquisa/actualização reflectidas em MySQL e JTable.
- [ ] NUIT duplicado não cria cliente novo.
- [ ] Não é possível associar dois contadores activos a um contrato.
- [ ] Troca de contador preserva histórico e data de remoção.
- [ ] `contador.activo` (gerado pelo MySQL) **não** é preenchido por Java.
- [ ] Métodos de consulta usados pela facturação estão prontos e documentados.

**Cuidado:** a primeira contratação liga cliente+contrato; combinar com Opção 3 um fluxo coerente com o minimundo.
