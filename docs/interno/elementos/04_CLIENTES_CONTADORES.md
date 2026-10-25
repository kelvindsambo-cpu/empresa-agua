# Membro 4 — Clientes e contadores

**Nível:** fácil/médio  
**Responsável:** [NOME]  
**Branch:** `clientes-contadores`

## Resultado

Registar, pesquisar e editar clientes; registar contadores nos contratos e mudar o seu estado.

## Classes

```text
model/Cliente.java, model/Contador.java
dao/ClienteDAO.java, dao/ContadorDAO.java
controller/ClienteController.java, controller/ContadorController.java
view/ClientePanel.java, view/ClienteDialog.java
view/ContadorPanel.java, view/ContadorDialog.java
```

## Tarefas

### Clientes

- [ ] `ClientePanel`: `JTable` + campo de pesquisa por nome (ou NUIT) + botões Novo/Editar.
- [ ] `ClienteDialog`: nome completo, NUIT (opcional, 9 dígitos), telefone (≥ 8 caracteres), rua, nº casa, bairro.
- [ ] Validar obrigatórios no Java. NUIT duplicado: a BD recusa (`UNIQUE`); mostrar a mensagem.
- [ ] `ClienteDAO`: `listar()`, `pesquisar(termo)`, `buscarPorId(int)`, `inserir`, `actualizar`.

### Contadores

- [ ] `ContadorPanel`: escolher contrato (combo) → `JTable` com os contadores desse contrato (nº série, instalação, leitura inicial, estado).
- [ ] `ContadorDialog` "Novo": nº de série, data de instalação, leitura inicial (≥ 0).
- [ ] Botão "Mudar estado": `activo`/`inactivo`/`avariado`; ao sair de `activo`, pedir data de remoção.
- [ ] `ContadorDAO.buscarActivoPorContrato(int idContrato)` para o Membro 2.
- [ ] Nunca escrever a coluna `activo` (é gerada pela BD).

A BD já recusa: nº de série repetido, segundo contador activo no mesmo contrato, leitura inicial negativa.

## Precisa de

Membro 3: `ContratoDAO.listarActivos()` (até lá, consulta temporária).

## Demonstração

Registar e pesquisar cliente; tentar NUIT repetido; registar contador; tentar um segundo contador activo no mesmo contrato; trocar de contador (pôr o antigo `inactivo` e registar o novo).

**Fora do âmbito:** apagar clientes, troca automática de contador numa transacção, histórico de moradas.
