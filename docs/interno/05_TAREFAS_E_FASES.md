# Tarefas, fases e dependências

## Etapa 0 — decisões comuns (toda a equipa)

- [ ] Preencher nomes/responsabilidades.
- [ ] Fixar JDK, Maven, MySQL e política de configuração local.
- [ ] Decidir se a aplicação usa **Anexo A puro** (recomendação inicial) ou A+C.
- [ ] Congelar nomes, tipos e assinaturas dos métodos entre módulos.
- [ ] Definir `utilizador`/perfis e acesso inicial de teste.
- [ ] Estabelecer padrões de Swing (look & feel, dimensões, formulários).

## Etapa 1 — base compilável (Opção 1 + apoio 6)

- [ ] Criar repositório, `pom.xml`, `.gitignore` e packages.
- [ ] Implementar `Main`, `DatabaseConnection` e teste básico JDBC.
- [ ] Criar Models/assinaturas acordadas, sem multiplicar lógica duplicada.
- [ ] Preparar `MainFrame`/navegação com painéis vazios temporários.

## Etapa 2 — primeiros casos de uso em paralelo

- [ ] Opção 1: login/logout, perfis e sessão.
- [ ] Opção 4: CRUD cliente com JTable e validações.
- [ ] Opção 5: CRUD funcionário e reclamações simples.
- [ ] Opção 6: Dashboard com consultas reais disponíveis e auxílio aos formulários.
- [ ] Opção 2: `Tarifa`, `TarifaDAO`, gestão e testes de vigência.
- [ ] Opção 3: `Contrato`, `ContratoDAO`, consultas e alterações de estado.

## Etapa 3 — relações e operações complexas

- [ ] Opção 4: contador ligado ao contrato; troca de contador.
- [ ] Opção 2: `FaturaService`/`FaturaDAO`, cálculo de consumo e emissão.
- [ ] Opção 3: pagamento transaccional e saldo consistente.
- [ ] Opção 5: atribuir e resolver reclamações.
- [ ] Opção 1: autorização real em Controller/Service e suporte técnico à integração.

## Etapa 4 — integração e testes

- [ ] Fazer login → seleccionar cliente → consultar contrato → contador → factura → pagamento.
- [ ] Verificar restrições de autenticação, tarifa, estados, leituras e pagamentos.
- [ ] Testar erros SQL sem bloquear Swing.
- [ ] Testar concorrência de pagamentos, com duas sessões/threads se o grupo tiver ambiente adequado.
- [ ] Rever permissões do Dashboard e menus.

## Etapa 5 — entrega

- [ ] Confirmar `mvn clean test`, arranque e execução em máquina limpa.
- [ ] Actualizar README e documentação de execução.
- [ ] Preparar dados fictícios reproduzíveis de demonstração.
- [ ] Remover documentação interna temporária **se essa for a decisão da equipa**.
- [ ] Rever histórico para evitar segredos; remover ficheiros não apaga commits antigos.
- [ ] Tag/release estável em `main` e ensaio de apresentação por cada elemento.

## Prioridade, não duração fixa

Uma etapa pode começar antes de outra terminar se os contratos estiverem acordados. Nenhum elemento deve esperar pelos outros para construir a sua própria View ou escrever testes unitários. Integrações pequenas e frequentes são preferíveis a uma junção final de seis branches enormes.
