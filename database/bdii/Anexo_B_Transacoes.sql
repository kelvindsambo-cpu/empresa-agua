-- =====================================================================
-- Anexo B — Transacções T1 a T12
-- Sistema de Gestão de Fornecimento de Água — MySQL 8, InnoDB
-- Aplica-se sobre o esquema e os dados de exemplo do Anexo A.
-- Cada transacção é independente e altera os dados de exemplo: para
-- reproduzir os valores indicados nos comentários, correr o Anexo A
-- antes de cada uma.
-- =====================================================================

USE fornecimento_agua;

-- O MySQL Workbench liga por defeito o "safe update mode"
-- (sql_safe_updates = 1), que recusa UPDATE e DELETE sem uma condição
-- numa coluna-chave (erro 1175). A T7 actualiza as faturas através de
-- uma junção, por isso o modo é desligado apenas nesta sessão e reposto
-- no fim do ficheiro.
SET @safe_updates_antes = @@SESSION.sql_safe_updates;
SET SESSION sql_safe_updates = 0;

-- ==================== Transacção 1 ====================
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

INSERT INTO cliente (nome_completo, nuit,
       telefone, rua, numero_casa, bairro)
VALUES ('Joana Alberto Mabunda', '400555666',
        '847001122', 'Rua da Resistência',
        '112', 'Matola A');
-- NUIT repetido: erro de UNIQUE e ROLLBACK
SET @id_cliente = LAST_INSERT_ID();

INSERT INTO contrato (tipo_contrato,
       data_inicio, rua, numero_porta,
       quarteirao, bairro, id_cliente)
VALUES ('residencial', '2026-10-14',
        'Rua da Resistência', '112', '15',
        'Matola A', @id_cliente);
SET @id_contrato = LAST_INSERT_ID();

INSERT INTO contador (numero_contador,
       data_instalacao, leitura_inicial,
       id_contrato)
VALUES ('CNT-2026-0451', '2026-10-14', 0.00,
        @id_contrato);
-- número de contador repetido: erro de
-- UNIQUE e ROLLBACK

COMMIT;

-- ==================== Transacção 2 ====================
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT telefone, rua, numero_casa, bairro
FROM cliente
WHERE id_cliente = 3;
-- devolve 823456789, Rua da Liberdade,
-- 1450, Liberdade

UPDATE cliente
SET telefone = '847654321',
    rua = 'Rua do Jardim', numero_casa = '45',
    bairro = 'Tchumene'
WHERE id_cliente = 3
  AND telefone = '823456789'
  AND rua = 'Rua da Liberdade'
  AND numero_casa = '1450'
  AND bairro = 'Liberdade';
-- dispara trg_cliente_auditoria (guarda os
-- valores anteriores)
-- ROW_COUNT() = 0: outro utilizador alterou
-- os dados entretanto; ROLLBACK e voltar a ler

COMMIT;

-- ==================== Transacção 3 ====================
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
       f.ano_referencia,
       f.valor_base + f.valor_multa
         AS valor_total,
       COALESCE(SUM(p.valor), 0) AS valor_pago,
       f.valor_base + f.valor_multa
         - COALESCE(SUM(p.valor), 0)
         AS saldo_em_divida,
       f.estado_fatura, f.data_vencimento
FROM contrato ct
JOIN contador cd
  ON cd.id_contrato = ct.id_contrato
JOIN fatura f ON f.id_contador = cd.id_contador
LEFT JOIN pagamento p
       ON p.id_fatura = f.id_fatura
WHERE ct.id_cliente = 3
  AND f.estado_fatura <> 'paga'
GROUP BY f.id_fatura
ORDER BY f.data_vencimento;
-- devolve a 101 (saldo 625.00) e a 102
-- (saldo 675.00)

COMMIT;

-- ==================== Transacção 4 ====================
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT estado_contrato
FROM contrato
WHERE id_contrato = 7
FOR UPDATE;
-- contrato terminado: ROLLBACK

SELECT id_contador
FROM contador
WHERE id_contrato = 7
  AND estado_contador = 'activo'
FOR UPDATE;
-- devolve o contador 10

SELECT COUNT(*)
FROM fatura
WHERE id_contador = 10
  AND data_leitura > '2026-09-29';
-- se for maior que 0: ROLLBACK (troca anterior
-- a uma leitura já faturada)

UPDATE contador
SET estado_contador = 'avariado',
    data_remocao = '2026-09-29'
WHERE id_contador = 10;
-- dispara trg_contador_auditoria

INSERT INTO contador (numero_contador,
       data_instalacao, leitura_inicial,
       id_contrato)
VALUES ('CNT-2026-0452', '2026-09-29', 0.00, 7);
-- UNIQUE (id_contrato, activo) impede dois
-- contadores activos

COMMIT;

-- ==================== Transacção 5 ====================
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT id_contrato, tipo_contrato,
       estado_contrato
FROM contrato
WHERE id_contrato = (SELECT id_contrato
                     FROM contador
                     WHERE id_contador = 10)
FOR SHARE;
-- devolve o contrato 7, residencial, activo;
-- não activo: ROLLBACK

SELECT estado_contador, leitura_inicial
FROM contador
WHERE id_contador = 10
FOR SHARE;
-- contador não activo: ROLLBACK
-- (ordem: contrato, depois contador, como T4)

SELECT leitura_atual
FROM fatura
WHERE id_contador = 10
ORDER BY ano_referencia DESC,
         mes_referencia DESC
LIMIT 1;
-- devolve 1231.00 (Setembro); sem faturas,
-- usa-se a leitura_inicial do contador

SELECT id_tarifa, preco_por_m3, consumo_minimo
FROM tarifa
WHERE tipo_contrato = 'residencial'
  AND data_inicio <= '2026-10-28'
  AND (data_fim IS NULL
       OR data_fim >= '2026-10-28')
FOR SHARE;
-- devolve a tarifa 1: 45.00 MT/m3, mínimo 5 m3

INSERT INTO fatura (mes_referencia,
       ano_referencia, data_leitura,
       leitura_anterior, leitura_atual,
       data_emissao, data_vencimento,
       valor_base, id_contador, id_tarifa,
       id_funcionario)
VALUES (10, 2026, '2026-10-28',
       1231.00, 1249.00,
       '2026-10-28',
       DATE_ADD(LAST_DAY('2026-10-28'),
                INTERVAL 10 DAY),
       GREATEST(1249.00 - 1231.00, 5.00)
         * 45.00,
       10, 1, 1);
-- dispara trg_fatura_before_insert
-- (cargo do técnico); UNIQUE (id_contador,
-- ano, mês) impede uma 2.ª fatura do mês

COMMIT;

-- ==================== Transacção 6 ====================
-- Versão da secção 4.2 do Projecto Preliminar. As validações estão nas
-- próprias instruções; a transacção termina sempre com COMMIT. Uma
-- recusa (@inseridos = 0) é uma transacção confirmada sem alterações.
-- O ROLLBACK fica para erros do MySQL (ex.: recibo repetido, erro 1062).
SET @valor = 300.00;

SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT estado_fatura, valor_base + valor_multa
INTO @estado, @total
FROM fatura
WHERE id_fatura = 102
FOR UPDATE;
-- bloqueia a fatura (X): é a linha-guarda dos
-- seus pagamentos; devolve pendente, 675.00

SELECT COALESCE(SUM(valor), 0) INTO @pago
FROM pagamento
WHERE id_fatura = 102;
-- soma o que já foi pago, depois de obtido o
-- bloqueio; devolve 0.00 (saldo = 675.00)

INSERT INTO pagamento (data_pagamento, valor,
       metodo_pagamento, numero_recibo,
       id_fatura, registado_por)
SELECT CURDATE(), @valor, 'dinheiro',
       'RC-2026-001873', 102, 5
FROM DUAL
WHERE @estado <> 'paga'
  AND @valor <= @total - @pago;
SET @inseridos = ROW_COUNT();
-- 1 = aceite; 0 = recusado (nada é gravado)

UPDATE fatura
SET estado_fatura = 'paga'
WHERE id_fatura = 102
  AND @inseridos = 1
  AND @total - @pago - @valor <= 0;
-- só passa a paga se o pagamento entrou e o
-- saldo chegou a zero (trg_fatura_auditoria)

COMMIT;
-- a aplicação lê @inseridos e informa

-- ==================== Transacção 7 ====================
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT id_fatura
FROM fatura
WHERE estado_fatura = 'pendente'
  AND data_vencimento < CURDATE()
ORDER BY id_fatura
FOR UPDATE;
-- bloqueia as faturas a processar, por ordem
-- de id; devolve a 102

UPDATE fatura f
JOIN (SELECT f2.id_fatura,
             f2.valor_base
               - COALESCE(SUM(p.valor), 0)
               AS saldo
      FROM fatura f2
      LEFT JOIN pagamento p
             ON p.id_fatura = f2.id_fatura
      WHERE f2.estado_fatura = 'pendente'
        AND f2.data_vencimento < CURDATE()
      GROUP BY f2.id_fatura) s
  ON s.id_fatura = f.id_fatura
SET f.valor_multa = ROUND(s.saldo * 0.25, 2),
    f.estado_fatura = 'vencida'
WHERE s.saldo > 0;
-- multa de 25% sobre o saldo: 168.75 na
-- fatura 102; dispara trg_fatura_auditoria
-- (uma linha por fatura)

COMMIT;

-- ==================== Transacção 8 ====================
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT estado_contrato
FROM contrato
WHERE id_contrato = 7
FOR UPDATE;
-- tem de estar activo; senão ROLLBACK

SELECT f.id_fatura
FROM fatura f
JOIN contador cd
  ON cd.id_contador = f.id_contador
WHERE cd.id_contrato = 7
  AND f.estado_fatura = 'vencida'
FOR SHARE OF f;
-- devolve a 101; nenhuma linha: ROLLBACK

UPDATE contrato
SET estado_contrato = 'cortado'
WHERE id_contrato = 7;
-- dispara trg_contrato_auditoria

COMMIT;

-- ==================== Transacção 9 ====================
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
  AND f.estado_fatura <> 'paga'
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

-- ==================== Transacção 10 ====================
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT id_tarifa, data_inicio
FROM tarifa
WHERE tipo_contrato = 'residencial'
  AND data_fim IS NULL
FOR UPDATE;
-- devolve a tarifa 1 (desde 2026-01-01)
-- a nova tarifa tem de começar depois de hoje
-- e depois do início da actual; senão
-- ROLLBACK

UPDATE tarifa
SET data_fim = '2026-10-31'
WHERE id_tarifa = 1;
-- dispara trg_tarifa_auditoria_update

INSERT INTO tarifa (tipo_contrato, preco_por_m3,
                    consumo_minimo, data_inicio)
VALUES ('residencial', 50.00, 5.00,
        '2026-11-01');
-- dispara trg_tarifa_before_insert (períodos
-- sobrepostos: erro e ROLLBACK) e
-- trg_tarifa_auditoria_insert

COMMIT;

-- ==================== Transacção 11 ====================
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;

SELECT fu.id_funcionario,
       COUNT(r.id_reclamacao) AS por_resolver
FROM funcionario fu
LEFT JOIN reclamacao r
       ON r.id_funcionario = fu.id_funcionario
      AND r.estado_reclamacao <> 'resolvida'
WHERE fu.cargo = 'tecnico_manutencao'
GROUP BY fu.id_funcionario
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

-- ==================== Transacção 12 ====================
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
START TRANSACTION WITH CONSISTENT SNAPSHOT,
                  READ ONLY;

SELECT ct.bairro, ct.tipo_contrato,
       COUNT(*) AS faturas,
       SUM(f.valor_base + f.valor_multa)
         AS faturado,
       SUM((SELECT COALESCE(SUM(p.valor), 0)
            FROM pagamento p
            WHERE p.id_fatura = f.id_fatura))
         AS cobrado
FROM fatura f
JOIN contador cd
  ON cd.id_contador = f.id_contador
JOIN contrato ct
  ON ct.id_contrato = cd.id_contrato
WHERE f.ano_referencia = 2026
  AND f.mes_referencia = 9
GROUP BY ct.bairro, ct.tipo_contrato
ORDER BY ct.bairro, ct.tipo_contrato;

SELECT SUM(f.valor_base + f.valor_multa)
       - COALESCE(
           (SELECT SUM(p.valor)
            FROM pagamento p
            JOIN fatura f2
              ON f2.id_fatura = p.id_fatura
            WHERE f2.ano_referencia = 2026
              AND f2.mes_referencia = 9), 0)
       AS total_em_divida
FROM fatura f
WHERE f.ano_referencia = 2026
  AND f.mes_referencia = 9;
-- tem de bater com faturado - cobrado da
-- primeira consulta (675.00)

COMMIT;

-- Repõe o safe update mode como estava antes deste ficheiro
SET SESSION sql_safe_updates = @safe_updates_antes;
