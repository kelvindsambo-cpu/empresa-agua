-- =====================================================================
-- Anexo C — Optimização da base de dados (Fase 6)
-- Sistema de Gestão de Fornecimento de Água — MySQL 8, InnoDB
-- Aplica-se sobre o esquema do Anexo A. Ordem: desnormalização (D),
-- índices (I), particionamento (P). Testado em MySQL 8.0.46.
--
-- Partes:
--   0. Janela de instalação
--   D1. valor_pago na fatura
--   (D2, a tabela-resumo do relatório mensal, não é adoptada com a
--    frequência actual de T12; está pronta a aplicar no Anexo C2)
--   I.  Índices
--   P.  Particionamento (e manutenção diária de P1)
--   T.  Transacções alteradas (T3, T6, T7, T9, T11, T12)
-- =====================================================================

USE fornecimento_agua;

-- O MySQL Workbench liga por defeito o "safe update mode"
-- (sql_safe_updates = 1), que recusa UPDATE e DELETE sem uma condição
-- numa coluna-chave (erro 1175). A carga inicial de valor_pago
-- actualiza de propósito todas as faturas, por isso o modo é desligado
-- apenas nesta sessão e reposto no fim da instalação.
SET @safe_updates_antes = @@SESSION.sql_safe_updates;
SET SESSION sql_safe_updates = 0;

-- =====================================================================
-- 0. Janela de instalação
-- =====================================================================
-- A carga inicial de D1 só fica exacta se nenhuma transacção
-- escrever enquanto correm. Os canais móveis funcionam de noite, por isso
-- "fora do expediente" não basta: a instalação suspende as escritas.
-- read_only = ON recusa escritas de todas as contas sem privilégio
-- SUPER/CONNECTION_ADMIN (as contas da aplicação) e espera que as
-- transacções em curso terminem. O administrador que corre este script
-- continua a poder escrever. Durante a janela, a aplicação mostra
-- "sistema em manutenção" e os pedidos dos canais móveis são recusados
-- e repetidos depois. A duração da janela depende do volume: o ALTER
-- TABLE de FATURA e a carga de valor_pago percorrem todas as faturas;
-- deve ser medida numa cópia da base antes de marcar a janela.
-- Se o script parar a meio (por exemplo, por um erro), a base fica em
-- read_only: correr SET GLOBAL read_only = OFF; antes de repetir, e
-- recriar a base com o Anexo A (o ALTER TABLE de FATURA pode já ter
-- sido aplicado).
SET GLOBAL read_only = ON;

-- =====================================================================
-- D1 — valor_pago guardado na FATURA (coluna derivada mantida por trigger)
-- =====================================================================
-- Antes: o valor pago de uma fatura era SUM(pagamento.valor), recalculado
-- em T3, T6, T7 e T12. Agora fica guardado na própria fatura.
-- consumo_m3, valor_total e saldo_em_divida passam a colunas geradas
-- VIRTUAL: são calculadas pelo MySQL ao ler a linha, não ocupam espaço e
-- nunca ficam desactualizadas (não são redundância).

ALTER TABLE fatura
    ADD COLUMN valor_pago DECIMAL(10,2) NOT NULL DEFAULT 0.00 AFTER valor_multa,
    ADD COLUMN consumo_m3 DECIMAL(10,2)
        GENERATED ALWAYS AS (leitura_atual - leitura_anterior) VIRTUAL,
    ADD COLUMN valor_total DECIMAL(10,2)
        GENERATED ALWAYS AS (valor_base + valor_multa) VIRTUAL,
    ADD COLUMN saldo_em_divida DECIMAL(10,2)
        GENERATED ALWAYS AS (valor_base + valor_multa - valor_pago) VIRTUAL,
    ADD CONSTRAINT ck_fatura_pago
        CHECK (valor_pago >= 0 AND valor_pago <= valor_base + valor_multa);

-- Carga inicial a partir dos pagamentos já existentes (dentro da janela)
UPDATE fatura f
SET f.valor_pago = (SELECT COALESCE(SUM(p.valor), 0)
                    FROM pagamento p
                    WHERE p.id_fatura = f.id_fatura);

-- Verificação de D1 (deve devolver zero linhas)
SELECT f.id_fatura, f.valor_pago, COALESCE(SUM(p.valor), 0) AS soma_pagamentos
FROM fatura f
LEFT JOIN pagamento p ON p.id_fatura = f.id_fatura
GROUP BY f.id_fatura, f.valor_pago
HAVING f.valor_pago <> COALESCE(SUM(p.valor), 0);

DELIMITER //

-- Pagamento: soma à fatura na mesma transacção (actualização atómica).
-- Em T6 a fatura já está bloqueada (SELECT ... FOR UPDATE), por isso o
-- UPDATE não pede nenhum bloqueio novo; dois pagamentos simultâneos da
-- mesma fatura somam ambos.
CREATE TRIGGER trg_pagamento_after_insert
AFTER INSERT ON pagamento
FOR EACH ROW
BEGIN
    UPDATE fatura
    SET valor_pago = valor_pago + NEW.valor
    WHERE id_fatura = NEW.id_fatura;
END//

-- Um recibo emitido não se altera nem se apaga (nenhuma transacção o
-- faz). A regra passa a ser imposta pela base de dados, o que garante
-- que valor_pago nunca diverge dos pagamentos.
CREATE TRIGGER trg_pagamento_before_update
BEFORE UPDATE ON pagamento
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Pagamentos registados nao podem ser alterados';
END//

CREATE TRIGGER trg_pagamento_before_delete
BEFORE DELETE ON pagamento
FOR EACH ROW
BEGIN
    SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Pagamentos registados nao podem ser eliminados';
END//

DELIMITER ;

-- =====================================================================
-- I — Índices
-- =====================================================================

-- I1: faturas por pagar de um contador (T3, T8, T9). Sem ele, o
-- MySQL usa uq_fatura_mes e lê todo o histórico do contador (12 faturas
-- por ano, ~60 em 2030) para ficar com 1 ou 2.
CREATE INDEX ix_fatura_contador_estado ON fatura (id_contador, estado_fatura);

-- I2: reclamações por resolver (T11). Sem ele, o MySQL usa o índice da
-- chave estrangeira e lê todas as reclamações de cada técnico, as
-- resolvidas incluídas (~81 000 por ano, e a crescer). O estado vem
-- primeiro porque a consulta procura os dois estados raros ('aberta' e
-- 'em_tratamento', ~1% das linhas): o MySQL lê só as ~920 entradas em
-- aberto, sem ir à tabela (índice de cobertura). Exige a nova versão de
-- T11 (contagem feita antes da junção). O índice da chave estrangeira
-- (id_funcionario) mantém-se: este não serve à chave estrangeira.
CREATE INDEX ix_reclamacao_estado_tecnico ON reclamacao (estado_reclamacao, id_funcionario);

-- Mantidos do Anexo A: ix_fatura_estado (T7), ix_tarifa_vigencia (T5, T10),
-- ix_log_transacao_inicio (monitorização).

ANALYZE TABLE fatura, reclamacao;

-- =====================================================================
-- P — Particionamento
-- =====================================================================
-- Regras do MySQL: a coluna de partição tem de fazer parte da chave
-- primária e de todas as chaves únicas; tabelas particionadas não podem
-- ter nem receber chaves estrangeiras. As duas tabelas de log não têm
-- chaves estrangeiras, por isso são as únicas particionadas.

-- P1: log_transacao por dia. Retenção: o dia corrente e os 90 dias
-- completos anteriores (no máximo 91 dias). Todas as noites elimina-se
-- a partição do dia que saiu da janela (sem DELETE: não percorre as
-- linhas; a duração depende sobretudo da espera pelo bloqueio de
-- metadados da tabela) e
-- cria-se a do 7.º dia seguinte, para haver sempre partições à frente.
-- As partições iniciais são geradas a partir da data de hoje.
ALTER TABLE log_transacao
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (id_log, inicio);

SET SESSION group_concat_max_len = 100000;
SET SESSION cte_max_recursion_depth = 1000;
SET @sql = (
    WITH RECURSIVE dias (d) AS (
        SELECT CURDATE() - INTERVAL 90 DAY
        UNION ALL
        SELECT d + INTERVAL 1 DAY FROM dias WHERE d < CURDATE() + INTERVAL 7 DAY
    )
    SELECT CONCAT('ALTER TABLE log_transacao PARTITION BY RANGE (TO_DAYS(inicio)) (',
                  'PARTITION p_antigo VALUES LESS THAN (', TO_DAYS(MIN(d)), '), ',
                  GROUP_CONCAT(CONCAT('PARTITION p', DATE_FORMAT(d, '%Y%m%d'),
                                      ' VALUES LESS THAN (', TO_DAYS(d) + 1, ')')
                               ORDER BY d SEPARATOR ', '),
                  ', PARTITION pmax VALUES LESS THAN MAXVALUE)')
    FROM dias
);
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;
-- p_antigo recebe registos anteriores à janela (se existirem na
-- instalação) e é eliminada na primeira manutenção.

-- Manutenção diária de P1 (01:30). Sem procedimentos
-- armazenados: a instrução é montada e executada com PREPARE.
-- (a) criar a partição do dia CURDATE() + 7 a partir de pmax (vazia)
SET @dia = CURDATE() + INTERVAL 7 DAY;
SET @sql = IF(EXISTS (SELECT 1 FROM information_schema.PARTITIONS
                      WHERE TABLE_SCHEMA = 'fornecimento_agua'
                        AND TABLE_NAME = 'log_transacao'
                        AND PARTITION_NAME = CONCAT('p', DATE_FORMAT(@dia, '%Y%m%d'))),
              'DO 0',
              CONCAT('ALTER TABLE log_transacao REORGANIZE PARTITION pmax INTO (',
                     'PARTITION p', DATE_FORMAT(@dia, '%Y%m%d'),
                     ' VALUES LESS THAN (', TO_DAYS(@dia) + 1, '), ',
                     'PARTITION pmax VALUES LESS THAN MAXVALUE)'));
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- (b) eliminar as partições de dias anteriores a CURDATE() - 90
SET @lista = (SELECT GROUP_CONCAT(PARTITION_NAME)
              FROM information_schema.PARTITIONS
              WHERE TABLE_SCHEMA = 'fornecimento_agua'
                AND TABLE_NAME = 'log_transacao'
                AND PARTITION_NAME <> 'pmax'
                AND PARTITION_DESCRIPTION <= TO_DAYS(CURDATE() - INTERVAL 90 DAY));
SET @sql = IF(@lista IS NULL, 'DO 0',
              CONCAT('ALTER TABLE log_transacao DROP PARTITION ', @lista));
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- P2: log_auditoria por ano. Permanente e a crescer (~5,9 M linhas por
-- ano): consultas por período só lêem os anos pedidos, e cada ano pode
-- ser arquivado com EXCHANGE PARTITION sem DELETE. data_hora é
-- TIMESTAMP, por isso a função de partição é UNIX_TIMESTAMP (o MySQL
-- não aceita YEAR() sobre TIMESTAMP).
ALTER TABLE log_auditoria
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (id_log, data_hora);

ALTER TABLE log_auditoria
PARTITION BY RANGE (UNIX_TIMESTAMP(data_hora)) (
    PARTITION p2026 VALUES LESS THAN (UNIX_TIMESTAMP('2027-01-01 00:00:00')),
    PARTITION p2027 VALUES LESS THAN (UNIX_TIMESTAMP('2028-01-01 00:00:00')),
    PARTITION pmax  VALUES LESS THAN MAXVALUE
);

-- Manutenção anual (Dezembro): criar o ano seguinte a partir de pmax.
-- ALTER TABLE log_auditoria REORGANIZE PARTITION pmax INTO (
--     PARTITION p2028 VALUES LESS THAN (UNIX_TIMESTAMP('2029-01-01 00:00:00')),
--     PARTITION pmax  VALUES LESS THAN MAXVALUE);

-- Fim da janela de instalação: a aplicação volta a escrever.
SET GLOBAL read_only = OFF;

-- Repõe o safe update mode como estava antes deste ficheiro
SET SESSION sql_safe_updates = @safe_updates_antes;

-- =====================================================================
-- T — Transacções alteradas pela optimização (as restantes ficam
-- como no Anexo B)
-- ATENÇÃO: estes exemplos executam-se sobre os dados do Anexo A e
-- alteram-nos (a T6 paga 300,00 na fatura 102 e a T7 passa-a a vencida
-- com multa de 93,75). Para testar a instalação isoladamente, correr o
-- ficheiro só até à linha "Repõe o safe update mode" acima.
-- =====================================================================

-- ==================== Transacção 3 (nova versão) ====================
-- D1: valor_pago e saldo vêm da própria fatura, sem juntar PAGAMENTO
-- I1: só lê as faturas por pagar de cada contador
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION WITH CONSISTENT SNAPSHOT,
                  READ ONLY;

SELECT ct.id_contrato, ct.estado_contrato,
       cd.numero_contador
FROM contrato ct
LEFT JOIN contador cd
       ON cd.id_contrato = ct.id_contrato
      AND cd.estado_contador = 'activo'
WHERE ct.id_cliente = 3;

SELECT f.id_fatura, f.mes_referencia,
       f.ano_referencia, f.valor_total,
       f.valor_pago, f.saldo_em_divida,
       f.estado_fatura, f.data_vencimento
FROM contrato ct
JOIN contador cd
  ON cd.id_contrato = ct.id_contrato
JOIN fatura f ON f.id_contador = cd.id_contador
WHERE ct.id_cliente = 3
  AND f.estado_fatura IN ('pendente', 'vencida')
ORDER BY f.data_vencimento;
-- devolve a 101 (saldo 625.00) e a 102
-- (saldo 675.00); IN em vez de <> 'paga'
-- para o MySQL usar o índice I1

COMMIT;

-- ==================== Transacção 6 (nova versão) ====================
-- D1: deixa de somar os pagamentos; o trigger trg_pagamento_after_insert
-- actualiza valor_pago e saldo_em_divida é uma coluna gerada.
-- Mesma abordagem da secção 4.2: as validações estão nas próprias
-- instruções e a transacção termina sempre com COMMIT (uma recusa,
-- @inseridos = 0, é uma transacção confirmada sem alterações).
SET @valor = 300.00;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT estado_fatura, saldo_em_divida
INTO @estado, @saldo
FROM fatura
WHERE id_fatura = 102
FOR UPDATE;
-- devolve pendente, 675.00

INSERT INTO pagamento (data_pagamento, valor, metodo_pagamento,
                       numero_recibo, id_fatura, registado_por)
SELECT CURDATE(), @valor, 'dinheiro', 'RC-2026-001873', 102, 5
FROM DUAL
WHERE @estado <> 'paga'
  AND @valor <= @saldo;
SET @inseridos = ROW_COUNT();    -- 1 = aceite; 0 = recusado
-- se aceite, dispara trg_pagamento_after_insert:
-- valor_pago da 102 passa a 300.00

UPDATE fatura
SET estado_fatura = 'paga'
WHERE id_fatura = 102
  AND @inseridos = 1
  AND saldo_em_divida <= 0;
-- só muda o estado se o saldo chegar a zero

COMMIT;
-- a aplicação lê @inseridos para informar o utilizador

-- ==================== Transacção 7 (nova versão, em lotes) ====================
-- Executada às 00:30. Em vez de uma transacção com 85 400
-- faturas, processa lotes de 5 000 por ordem de id. Cada lote bloqueia
-- poucas linhas e por pouco tempo (os pagamentos móveis continuam a
-- entrar de noite). A multa continua a ser aplicada uma só vez porque
-- só se processam faturas 'pendente'. A aplicação repete o lote com
-- @desde = @ate até @ate vir NULL.
SET @desde = 0;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT MAX(id_fatura) INTO @ate
FROM (SELECT id_fatura
      FROM fatura
      WHERE estado_fatura = 'pendente'
        AND data_vencimento < CURDATE()
        AND id_fatura > @desde
      ORDER BY id_fatura
      LIMIT 5000) lote;
-- @ate NULL: não há mais faturas; COMMIT e fim

SELECT id_fatura
FROM fatura
WHERE estado_fatura = 'pendente'
  AND data_vencimento < CURDATE()
  AND id_fatura > @desde AND id_fatura <= @ate
ORDER BY id_fatura
FOR UPDATE;
-- bloqueia o lote, por ordem de id

UPDATE fatura
SET valor_multa = ROUND((valor_base - valor_pago)
                        * 0.25, 2),
    estado_fatura = 'vencida'
WHERE estado_fatura = 'pendente'
  AND data_vencimento < CURDATE()
  AND id_fatura > @desde
  AND id_fatura <= @ate
  AND valor_pago < valor_base;
-- multa de 25% sobre o saldo (valor_pago vem de
-- D1, sem juntar PAGAMENTO); dispara
-- trg_fatura_auditoria

COMMIT;

-- ==================== Transacção 9 (nova versão) ====================
-- I1: IN em vez de <> 'paga'; o resto fica igual
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT estado_contrato
FROM contrato
WHERE id_contrato = 6
FOR UPDATE;
-- tem de estar cortado; senão ROLLBACK

SELECT f.id_fatura
FROM fatura f
JOIN contador cd
  ON cd.id_contador = f.id_contador
WHERE cd.id_contrato = 6
  AND f.estado_fatura IN ('pendente', 'vencida')
FOR SHARE OF f;
-- alguma linha: ROLLBACK (há dívida)

SELECT COUNT(*)
FROM contador
WHERE id_contrato = 6
  AND estado_contador = 'activo';
-- tem de ser 1; senão ROLLBACK

UPDATE contrato
SET estado_contrato = 'activo'
WHERE id_contrato = 6;
-- dispara trg_contrato_auditoria

COMMIT;

-- ==================== Transacção 11 (nova versão) ====================
-- I2: conta primeiro as reclamações em aberto (só as entradas do índice
-- com esses estados) e depois junta aos técnicos
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT fu.id_funcionario,
       COALESCE(a.n, 0) AS por_resolver
FROM funcionario fu
LEFT JOIN (SELECT id_funcionario, COUNT(*) AS n
           FROM reclamacao
           WHERE estado_reclamacao
                 IN ('aberta', 'em_tratamento')
           GROUP BY id_funcionario) a
       ON a.id_funcionario = fu.id_funcionario
WHERE fu.cargo = 'tecnico_manutencao'
ORDER BY por_resolver, fu.id_funcionario
LIMIT 1;
-- devolve a técnica 4 (1 por resolver)

INSERT INTO reclamacao (tipo_reclamacao,
       descricao, data_abertura,
       estado_reclamacao, id_cliente,
       id_funcionario)
VALUES ('fuga',
        'Fuga no passeio junto ao contador.',
        CURDATE(), 'em_tratamento', 3, 4);

COMMIT;

-- ==================== Transacção 12 (nova versão) ====================
-- D1: o valor cobrado vem de valor_pago, sem subconsulta em PAGAMENTO;
-- uma só consulta (WITH ROLLUP dá os subtotais por bairro e o total,
-- que substitui a segunda consulta da Fase 2). O MySQL percorre os
-- contadores e encontra a fatura do mês pela chave única uq_fatura_mes.
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION WITH CONSISTENT SNAPSHOT,
                  READ ONLY;

SELECT ct.bairro, ct.tipo_contrato,
       COUNT(*)               AS faturas,
       SUM(f.valor_total)     AS faturado,
       SUM(f.valor_pago)      AS cobrado,
       SUM(f.saldo_em_divida) AS em_divida
FROM contador cd
JOIN contrato ct
  ON ct.id_contrato = cd.id_contrato
JOIN fatura f
  ON f.id_contador = cd.id_contador
 AND f.ano_referencia = 2026
 AND f.mes_referencia = 9
GROUP BY ct.bairro, ct.tipo_contrato WITH ROLLUP
ORDER BY GROUPING(ct.bairro), ct.bairro,
         GROUPING(ct.tipo_contrato), ct.tipo_contrato;
-- linhas com tipo NULL: subtotal do bairro;
-- linha com bairro NULL: total do mês

COMMIT;
