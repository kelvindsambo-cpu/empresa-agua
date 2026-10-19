-- =====================================================================
-- Anexo A — Esquema da base de dados (modelo revisto para BD II)
-- Sistema de Gestão de Fornecimento de Água — MySQL 8, InnoDB
-- =====================================================================

DROP DATABASE IF EXISTS fornecimento_agua;
CREATE DATABASE fornecimento_agua CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE fornecimento_agua;

-- ---------------------------------------------------------------------
-- Tabelas
-- ---------------------------------------------------------------------

CREATE TABLE cliente (
    id_cliente      INT AUTO_INCREMENT PRIMARY KEY,
    nome_completo   VARCHAR(200) NOT NULL,
    nuit            VARCHAR(9)   NULL UNIQUE,
    telefone        VARCHAR(20)  NOT NULL,
    rua             VARCHAR(200) NOT NULL,
    numero_casa     VARCHAR(10)  NOT NULL,
    bairro          VARCHAR(150) NOT NULL,
    CONSTRAINT ck_cliente_telefone CHECK (CHAR_LENGTH(telefone) >= 8)
) ENGINE = InnoDB;

CREATE TABLE funcionario (
    id_funcionario  INT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(200) NOT NULL,
    telefone        VARCHAR(20)  NULL,
    cargo           ENUM('tecnico_leitura', 'tecnico_manutencao', 'atendimento',
                         'supervisor', 'administrador') NOT NULL
) ENGINE = InnoDB;

CREATE TABLE tarifa (
    id_tarifa       INT AUTO_INCREMENT PRIMARY KEY,
    tipo_contrato   ENUM('residencial', 'comercial') NOT NULL,
    preco_por_m3    DECIMAL(10,2) NOT NULL,
    consumo_minimo  DECIMAL(6,2)  NOT NULL DEFAULT 5.00,
    data_inicio     DATE NOT NULL,
    data_fim        DATE NULL,                       -- NULL = tarifa vigente
    CONSTRAINT ck_tarifa_preco   CHECK (preco_por_m3 > 0),
    CONSTRAINT ck_tarifa_minimo  CHECK (consumo_minimo > 0),
    CONSTRAINT ck_tarifa_datas   CHECK (data_fim IS NULL OR data_fim > data_inicio),
    INDEX ix_tarifa_vigencia (tipo_contrato, data_inicio)
) ENGINE = InnoDB;

CREATE TABLE contrato (
    id_contrato     INT AUTO_INCREMENT PRIMARY KEY,
    tipo_contrato   ENUM('residencial', 'comercial') NOT NULL,
    estado_contrato ENUM('activo', 'suspenso', 'cortado', 'terminado') NOT NULL DEFAULT 'activo',
    data_inicio     DATE NOT NULL,
    data_fim        DATE NULL,
    rua             VARCHAR(200) NOT NULL,           -- endereco_fornecimento
    numero_porta    VARCHAR(10)  NOT NULL,
    quarteirao      VARCHAR(50)  NULL,
    bairro          VARCHAR(150) NOT NULL,
    id_cliente      INT NOT NULL,
    CONSTRAINT fk_contrato_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente),
    CONSTRAINT ck_contrato_datas CHECK (data_fim IS NULL OR data_fim > data_inicio),
    CONSTRAINT ck_contrato_terminado CHECK (estado_contrato <> 'terminado'
                                            OR data_fim IS NOT NULL)          -- RN05
) ENGINE = InnoDB;

CREATE TABLE contador (
    id_contador     INT AUTO_INCREMENT PRIMARY KEY,
    numero_contador VARCHAR(50) NOT NULL UNIQUE,     -- único e não reutilizável
    data_instalacao DATE NOT NULL,
    data_remocao    DATE NULL,
    leitura_inicial DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    estado_contador ENUM('activo', 'inactivo', 'avariado') NOT NULL DEFAULT 'activo',
    -- 1 quando activo, NULL nos outros estados: com o UNIQUE abaixo,
    -- impede dois contadores activos no mesmo contrato (RN09)
    activo          TINYINT GENERATED ALWAYS AS (IF(estado_contador = 'activo', 1, NULL)) STORED,
    id_contrato     INT NOT NULL,
    CONSTRAINT fk_contador_contrato FOREIGN KEY (id_contrato) REFERENCES contrato (id_contrato),
    CONSTRAINT uq_contador_activo UNIQUE (id_contrato, activo),
    CONSTRAINT ck_contador_leitura CHECK (leitura_inicial >= 0),
    CONSTRAINT ck_contador_datas  CHECK (data_remocao IS NULL OR data_remocao >= data_instalacao)
) ENGINE = InnoDB;

CREATE TABLE fatura (
    id_fatura        INT AUTO_INCREMENT PRIMARY KEY,
    mes_referencia   TINYINT  NOT NULL,              -- periodo
    ano_referencia   SMALLINT NOT NULL,
    data_leitura     DATE NOT NULL,                  -- leitura
    leitura_anterior DECIMAL(10,2) NOT NULL,
    leitura_atual    DECIMAL(10,2) NOT NULL,
    data_emissao     DATE NOT NULL,
    data_vencimento  DATE NOT NULL,
    valor_base       DECIMAL(10,2) NOT NULL,
    valor_multa      DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    estado_fatura    ENUM('pendente', 'paga', 'vencida') NOT NULL DEFAULT 'pendente',
    id_contador      INT NOT NULL,
    id_tarifa        INT NOT NULL,
    id_funcionario   INT NOT NULL,                   -- técnico que fez a leitura
    -- consumo_m3, valor_total, valor_pago e saldo_em_divida são derivados
    -- e calculados nas consultas (ver documento de optimização)
    CONSTRAINT fk_fatura_contador    FOREIGN KEY (id_contador)    REFERENCES contador (id_contador),
    CONSTRAINT fk_fatura_tarifa      FOREIGN KEY (id_tarifa)      REFERENCES tarifa (id_tarifa),
    CONSTRAINT fk_fatura_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario),
    CONSTRAINT uq_fatura_mes UNIQUE (id_contador, ano_referencia, mes_referencia),   -- RN16
    CONSTRAINT ck_fatura_mes     CHECK (mes_referencia BETWEEN 1 AND 12),
    CONSTRAINT ck_fatura_ano     CHECK (ano_referencia BETWEEN 2000 AND 2100),
    CONSTRAINT ck_fatura_leitura CHECK (leitura_atual >= leitura_anterior),           -- RN18
    CONSTRAINT ck_fatura_valores CHECK (valor_base >= 0 AND valor_multa >= 0),
    CONSTRAINT ck_fatura_datas   CHECK (data_vencimento > data_emissao),
    INDEX ix_fatura_estado (estado_fatura, data_vencimento)
) ENGINE = InnoDB;

CREATE TABLE pagamento (
    id_pagamento     INT AUTO_INCREMENT PRIMARY KEY,
    data_pagamento   DATE NOT NULL,
    valor            DECIMAL(10,2) NOT NULL,
    metodo_pagamento ENUM('dinheiro', 'transferencia', 'mpesa', 'emola', 'cartao') NOT NULL,
    numero_recibo    VARCHAR(50) NOT NULL UNIQUE,
    id_fatura        INT NOT NULL,
    registado_por    INT NULL,                       -- funcionário que recebeu ao balcão;
                                                     -- nulo nos canais móveis
    CONSTRAINT fk_pagamento_fatura      FOREIGN KEY (id_fatura)     REFERENCES fatura (id_fatura),
    CONSTRAINT fk_pagamento_funcionario FOREIGN KEY (registado_por) REFERENCES funcionario (id_funcionario),
    CONSTRAINT ck_pagamento_valor CHECK (valor > 0),
    CONSTRAINT ck_pagamento_registo CHECK ((metodo_pagamento IN ('mpesa', 'emola'))
                                           = (registado_por IS NULL))   -- RN38
) ENGINE = InnoDB;

CREATE TABLE reclamacao (
    id_reclamacao     INT AUTO_INCREMENT PRIMARY KEY,
    tipo_reclamacao   ENUM('fuga', 'avaria_contador', 'falta_agua', 'qualidade_agua',
                           'faturacao', 'atendimento', 'outro') NOT NULL,
    descricao         TEXT NOT NULL,
    data_abertura     DATE NOT NULL,
    data_fecho        DATE NULL,
    estado_reclamacao ENUM('aberta', 'em_tratamento', 'resolvida') NOT NULL DEFAULT 'aberta',
    id_cliente        INT NOT NULL,
    id_funcionario    INT NULL,                      -- responsável (pode estar por atribuir)
    CONSTRAINT fk_reclamacao_cliente     FOREIGN KEY (id_cliente)     REFERENCES cliente (id_cliente),
    CONSTRAINT fk_reclamacao_funcionario FOREIGN KEY (id_funcionario) REFERENCES funcionario (id_funcionario),
    CONSTRAINT ck_reclamacao_datas CHECK (data_fecho IS NULL OR data_fecho >= data_abertura),  -- RN34
    CONSTRAINT ck_reclamacao_fecho CHECK ((estado_reclamacao = 'resolvida')
                                          = (data_fecho IS NOT NULL)),       -- RN34
    CONSTRAINT ck_reclamacao_responsavel CHECK (estado_reclamacao = 'aberta'
                                                OR id_funcionario IS NOT NULL)  -- RN35
) ENGINE = InnoDB;

-- Auditoria: quem alterou o quê e quando, com o valor anterior. A aplicação
-- identifica o funcionário na sessão (SET @app_utilizador = '<login>'); os
-- triggers guardam esse valor ou, se não existir, o utilizador da base de dados.
CREATE TABLE log_auditoria (
    id_log          INT AUTO_INCREMENT PRIMARY KEY,
    usuario         VARCHAR(100) NOT NULL,
    acao            ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    tabela_afetada  VARCHAR(50)  NOT NULL,
    registro_id     INT NOT NULL,
    detalhe         VARCHAR(255) NULL,
    data_hora       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

-- Registo de execução das transacções (monitorização). A aplicação insere
-- uma linha depois de cada COMMIT ou ROLLBACK, em autocommit, fora da
-- transacção: assim as transacções anuladas também ficam registadas.
-- É a base para medir transacções por minuto, picos e latência.
CREATE TABLE log_transacao (
    id_log          BIGINT AUTO_INCREMENT PRIMARY KEY,
    transacao       VARCHAR(5)   NOT NULL,           -- 'T1' a 'T12'
    utilizador      VARCHAR(100) NOT NULL,
    tipo_operacao   ENUM('LEITURA', 'ESCRITA') NOT NULL,
    inicio          DATETIME(3)  NOT NULL,
    fim             DATETIME(3)  NOT NULL,
    estado          ENUM('CONCLUIDA', 'ERRO') NOT NULL,
    codigo_erro     INT NULL,                        -- ex.: 1062, 1205, 1213
    INDEX ix_log_transacao_inicio (inicio)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Dados de exemplo (estado inicial usado nos exemplos das transacções).
-- Carregados antes dos triggers, para não ficarem na auditoria.
-- ---------------------------------------------------------------------

INSERT INTO funcionario (id_funcionario, nome, telefone, cargo) VALUES
 (1, 'Ana Mucavele',   '841110001', 'tecnico_leitura'),
 (2, 'Bento Nhaca',    '841110002', 'tecnico_leitura'),
 (3, 'Carlos Tembe',   '841110003', 'tecnico_manutencao'),
 (4, 'Dina Matsinhe',  '841110004', 'tecnico_manutencao'),
 (5, 'Elsa Mabjaia',   '841110005', 'atendimento'),
 (6, 'Fábio Cumbe',    '841110006', 'administrador');

INSERT INTO tarifa (id_tarifa, tipo_contrato, preco_por_m3, consumo_minimo, data_inicio, data_fim) VALUES
 (1, 'residencial', 45.00, 5.00, '2026-01-01', NULL),
 (2, 'comercial',   70.00, 5.00, '2026-01-01', NULL);

INSERT INTO cliente (id_cliente, nome_completo, nuit, telefone, rua, numero_casa, bairro) VALUES
 (1, 'Paulo Ernesto Macuácua', '400100200', '842000101', 'Av. União Africana',   '310',  'Fomento'),
 (2, 'Lurdes Amélia Sitoe',    NULL,        '843000202', 'Av. das Indústrias',   '18',   'Machava'),
 (3, 'Maria José Cossa',       '400300400', '823456789', 'Rua da Liberdade',     '1450', 'Liberdade');

INSERT INTO contrato (id_contrato, tipo_contrato, estado_contrato, data_inicio,
                      rua, numero_porta, quarteirao, bairro, id_cliente) VALUES
 (5, 'comercial',   'activo',  '2024-06-01', 'Av. União Africana',   '312',  '4',  'Fomento',      1),
 (6, 'residencial', 'cortado', '2025-01-15', 'Av. das Indústrias',   '18',   '22', 'Machava',      2),
 (7, 'residencial', 'activo',  '2025-03-14', 'Rua da Liberdade',     '1450', '9',  'Liberdade',    3);

INSERT INTO contador (id_contador, numero_contador, data_instalacao, leitura_inicial, id_contrato) VALUES
 (8,  'CNT-2024-0108', '2024-06-01', 0.00, 5),
 (9,  'CNT-2025-0233', '2025-01-15', 0.00, 6),
 (10, 'CNT-2025-0310', '2025-03-14', 0.00, 7);

INSERT INTO fatura (id_fatura, mes_referencia, ano_referencia, data_leitura, leitura_anterior, leitura_atual,
                    data_emissao, data_vencimento, valor_base, valor_multa, estado_fatura,
                    id_contador, id_tarifa, id_funcionario) VALUES
 (95,  9, 2026, '2026-09-27', 2410.00, 2440.00, '2026-09-27', '2026-10-10', 2100.00,   0.00, 'paga',    8,  2, 2),
 (96,  8, 2026, '2026-08-27',  640.00,  652.00, '2026-08-27', '2026-09-10',  540.00,   0.00, 'paga',    9,  1, 1),
 (97,  9, 2026, '2026-09-27',  652.00,  656.00, '2026-09-27', '2026-10-10',  225.00,  56.25, 'paga',    9,  1, 1),
 (100, 7, 2026, '2026-07-28', 1180.00, 1196.00, '2026-07-28', '2026-08-10',  720.00,   0.00, 'paga',    10, 1, 1),
 (101, 8, 2026, '2026-08-28', 1196.00, 1216.00, '2026-08-28', '2026-09-10',  900.00, 125.00, 'vencida', 10, 1, 1),
 (102, 9, 2026, '2026-09-28', 1216.00, 1231.00, '2026-09-28', '2026-10-10',  675.00,   0.00, 'pendente',10, 1, 1);

INSERT INTO pagamento (id_pagamento, data_pagamento, valor, metodo_pagamento, numero_recibo,
                       id_fatura, registado_por) VALUES
 (1, '2026-10-05', 2100.00, 'transferencia', 'RC-2026-001701',  95, 5),
 (2, '2026-09-08',  540.00, 'dinheiro',      'RC-2026-001522',  96, 5),
 (3, '2026-10-13',  281.25, 'mpesa',         'RC-2026-001860',  97, NULL),
 (4, '2026-08-06',  720.00, 'mpesa',         'RC-2026-001390', 100, NULL),
 (5, '2026-09-02',  400.00, 'emola',         'RC-2026-001488', 101, NULL);

INSERT INTO reclamacao (id_reclamacao, tipo_reclamacao, descricao, data_abertura, data_fecho,
                        estado_reclamacao, id_cliente, id_funcionario) VALUES
 (1, 'fuga',            'Fuga na conduta principal da rua.',  '2026-10-08', NULL,         'em_tratamento', 1, 3),
 (2, 'avaria_contador', 'Contador sem movimento.',            '2026-10-10', NULL,         'em_tratamento', 2, 3),
 (3, 'falta_agua',      'Sem água desde a manhã.',            '2026-10-12', NULL,         'em_tratamento', 1, 4),
 (4, 'qualidade_agua',  'Água com cor castanha.',             '2026-09-20', '2026-09-22', 'resolvida',     3, 4);

-- ---------------------------------------------------------------------
-- Triggers
-- ---------------------------------------------------------------------
DELIMITER //

-- RN19: a leitura só pode ser feita por um técnico de leitura
CREATE TRIGGER trg_fatura_before_insert
BEFORE INSERT ON fatura
FOR EACH ROW
BEGIN
    DECLARE v_cargo VARCHAR(30);
    SELECT cargo INTO v_cargo FROM funcionario WHERE id_funcionario = NEW.id_funcionario;
    IF v_cargo IS NULL OR v_cargo <> 'tecnico_leitura' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A leitura tem de ser feita por um tecnico de leitura';
    END IF;
END//

-- ----- Auditoria: regista o que de outra forma se perderia -----

-- Mudanças de estado do contrato (corte, religação, suspensão, fim)
CREATE TRIGGER trg_contrato_auditoria
AFTER UPDATE ON contrato
FOR EACH ROW
BEGIN
    IF OLD.estado_contrato <> NEW.estado_contrato THEN
        INSERT INTO log_auditoria (usuario, acao, tabela_afetada, registro_id, detalhe)
        VALUES (COALESCE(@app_utilizador, CURRENT_USER()), 'UPDATE', 'contrato', NEW.id_contrato,
                CONCAT(OLD.estado_contrato, ' -> ', NEW.estado_contrato));
    END IF;
END//

-- Mudanças de estado da fatura e aplicação de multas
CREATE TRIGGER trg_fatura_auditoria
AFTER UPDATE ON fatura
FOR EACH ROW
BEGIN
    IF OLD.estado_fatura <> NEW.estado_fatura OR OLD.valor_multa <> NEW.valor_multa THEN
        INSERT INTO log_auditoria (usuario, acao, tabela_afetada, registro_id, detalhe)
        VALUES (COALESCE(@app_utilizador, CURRENT_USER()), 'UPDATE', 'fatura', NEW.id_fatura,
                CONCAT(OLD.estado_fatura, ' -> ', NEW.estado_fatura,
                       IF(OLD.valor_multa <> NEW.valor_multa,
                          CONCAT('; multa ', OLD.valor_multa, ' -> ', NEW.valor_multa), '')));
    END IF;
END//

-- Dados do cliente: guarda os valores anteriores, que o UPDATE apaga
CREATE TRIGGER trg_cliente_auditoria
AFTER UPDATE ON cliente
FOR EACH ROW
BEGIN
    DECLARE v_antes VARCHAR(255);
    SET v_antes = CONCAT_WS('; ',
        IF(OLD.nome_completo <> NEW.nome_completo, CONCAT('nome: ', OLD.nome_completo), NULL),
        IF(NOT (OLD.nuit <=> NEW.nuit), CONCAT('nuit: ', IFNULL(OLD.nuit, '-')), NULL),
        IF(OLD.telefone <> NEW.telefone, CONCAT('telefone: ', OLD.telefone), NULL),
        IF(OLD.rua <> NEW.rua OR OLD.numero_casa <> NEW.numero_casa OR OLD.bairro <> NEW.bairro,
           CONCAT('endereço: ', OLD.rua, ', ', OLD.numero_casa, ', ', OLD.bairro), NULL));
    IF v_antes <> '' THEN
        INSERT INTO log_auditoria (usuario, acao, tabela_afetada, registro_id, detalhe)
        VALUES (COALESCE(@app_utilizador, CURRENT_USER()), 'UPDATE', 'cliente', NEW.id_cliente,
                LEFT(CONCAT('antes: ', v_antes), 255));
    END IF;
END//

-- Troca de contador (o contador antigo muda de estado)
CREATE TRIGGER trg_contador_auditoria
AFTER UPDATE ON contador
FOR EACH ROW
BEGIN
    IF OLD.estado_contador <> NEW.estado_contador THEN
        INSERT INTO log_auditoria (usuario, acao, tabela_afetada, registro_id, detalhe)
        VALUES (COALESCE(@app_utilizador, CURRENT_USER()), 'UPDATE', 'contador', NEW.id_contador,
                CONCAT(OLD.estado_contador, ' -> ', NEW.estado_contador));
    END IF;
END//

-- Tarifas: criação e fecho de vigência
CREATE TRIGGER trg_tarifa_auditoria_insert
AFTER INSERT ON tarifa
FOR EACH ROW
BEGIN
    INSERT INTO log_auditoria (usuario, acao, tabela_afetada, registro_id, detalhe)
    VALUES (COALESCE(@app_utilizador, CURRENT_USER()), 'INSERT', 'tarifa', NEW.id_tarifa,
            CONCAT(NEW.tipo_contrato, ' ', NEW.preco_por_m3, ' MT/m3 desde ', NEW.data_inicio));
END//

CREATE TRIGGER trg_tarifa_auditoria_update
AFTER UPDATE ON tarifa
FOR EACH ROW
BEGIN
    IF NOT (OLD.data_fim <=> NEW.data_fim) OR OLD.preco_por_m3 <> NEW.preco_por_m3 THEN
        INSERT INTO log_auditoria (usuario, acao, tabela_afetada, registro_id, detalhe)
        VALUES (COALESCE(@app_utilizador, CURRENT_USER()), 'UPDATE', 'tarifa', NEW.id_tarifa,
                CONCAT_WS('; ',
                    IF(NOT (OLD.data_fim <=> NEW.data_fim),
                       CONCAT('fim: ', IFNULL(OLD.data_fim, '-'), ' -> ', IFNULL(NEW.data_fim, '-')), NULL),
                    IF(OLD.preco_por_m3 <> NEW.preco_por_m3,
                       CONCAT('preço: ', OLD.preco_por_m3, ' -> ', NEW.preco_por_m3), NULL)));
    END IF;
END//

-- RN13: os períodos de vigência das tarifas do mesmo tipo não se sobrepõem
CREATE TRIGGER trg_tarifa_before_insert
BEFORE INSERT ON tarifa
FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1 FROM tarifa
               WHERE tipo_contrato = NEW.tipo_contrato
                 AND (data_fim IS NULL OR data_fim >= NEW.data_inicio)
                 AND (NEW.data_fim IS NULL OR data_inicio <= NEW.data_fim)) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Periodo de vigencia sobreposto a outra tarifa do mesmo tipo';
    END IF;
END//

DELIMITER ;
