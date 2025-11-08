package com.joao.gestaorisco;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Classe utilitária para gerenciar a conexão com o banco de dados H2.
 * Responsável por obter a conexão e inicializar a estrutura do banco de dados (schema).
 * Esta classe isola a lógica de infraestrutura da lógica de negócio.
 */
public class DatabaseManager {

    // ***************************************************************
    // 1. CONSTANTES DE CONEXÃO
    // ***************************************************************
    // Constantes de conexão para o banco de dados H2
    // A URL especifica o driver (jdbc:h2:), o modo (arquivo) e o caminho do banco.
    private static final String JDBC_URL = "jdbc:h2:C:/DadosCompartilhados/gestaoderiscos_db";
    private static final String USER = "sa"; // Usuário padrão (System Administrator)
    private static final String PASSWORD = ""; // Senha padrão (vazia)

    /**
     * Obtém uma conexão com o banco de dados.
     * @return uma conexão válida com o banco de dados.
     * @throws SQLException se a conexão falhar (ex: credenciais erradas, banco indisponível).
     */
    public static Connection getConnection() throws SQLException {
        // Em aplicações modernas (a partir do Java 6), o Driver Manager geralmente
        // encontra o driver automaticamente via ServiceLoader.
        // O bloco try-catch abaixo adiciona robustez caso o driver não seja encontrado
        // no classpath, garantindo que a dependência seja validada.
        try {
            // Tenta carregar a classe do driver JDBC H2 na memória.
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver H2 não encontrado.");
            // Lança uma exceção SQL, pois a falha do driver impede qualquer operação de BD.
            throw new SQLException("Driver JDBC H2 indisponível.", e);
        }
     
        // Retorna a conexão, utilizando os parâmetros definidos nas constantes.
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * Inicializa o banco de dados, criando as tabelas se elas não existirem.
     * Adiciona um tipo de risco padrão para começar (Seeding/Povoamento).
     */
    public static void initializeDatabase() {
        // ***************************************************************
        // 2. INICIALIZAÇÃO E GARANTIA DE FECHAMENTO DE RECURSOS (Try-with-resources)
        // ***************************************************************
        // Uso de try-with-resources garante que Connection e Statement sejam fechados
        // automaticamente após a execução, evitando vazamento de recursos.
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // Criação das tabelas - o 'IF NOT EXISTS' é fundamental para a idempotência
            // (pode ser executado múltiplas vezes sem erro).

            // Tabela 1: TIPO_RISCO (Tabela de Referência/Lookup)
            stmt.execute("CREATE TABLE IF NOT EXISTS TIPO_RISCO ("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT," // Chave primária auto-gerada
                    + "NOME VARCHAR(200) NOT NULL UNIQUE,"
                    + "DESCRICAO VARCHAR(255)"
                    + ");");

            // Tabela 2: RISCO (Entidade Principal)
            stmt.execute("CREATE TABLE IF NOT EXISTS RISCO ("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "DESCRICAO VARCHAR(500) NOT NULL,"
                    + "ORIGEM VARCHAR(255),"
                    + "DATA_IDENTIFICACAO DATE NOT NULL,"
                    + "STATUS VARCHAR(50) NOT NULL," // Ex: Identificado, Avaliado, Mitigado
                    + "TIPO_RISCO_ID INT NOT NULL,"
                    // Definição da Chave Estrangeira
                    + "CONSTRAINT FK_RISCO_TIPO FOREIGN KEY (TIPO_RISCO_ID) REFERENCES TIPO_RISCO(ID)"
                    + ");");

            // Tabela 3: AVALIACAO (Análise do Risco)
            stmt.execute("CREATE TABLE IF NOT EXISTS AVALIACAO("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "RISCO_ID INT NOT NULL," // Relaciona 1:1 com RISCO
                    + "IMPACTO INT NOT NULL,"
                    + "PROBABILIDADE INT NOT NULL,"
                    + "URGENCIA INT NOT NULL,"
                    + "PONTUACAO_GERAL INT NOT NULL,"
                    + "DATA_AVALIACAO DATE NOT NULL,"
                    + "RESPONSAVEL VARCHAR(255) NOT NULL,"
                    + "JUSTIFICATIVA VARCHAR(500),"
                    + "CONSTRAINT FK_AVALIACAO_RISCOS FOREIGN KEY (RISCO_ID) REFERENCES RISCO(ID)"
                    + ");");

            // Tabela 4: PLANO_MITIGACAO (Resposta Estratégica)
            stmt.execute("CREATE TABLE IF NOT EXISTS PLANO_MITIGACAO("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "RISCO_ID INT NOT NULL," // Relaciona 1:1 com RISCO
                    + "DESCRICAO VARCHAR(500) NOT NULL,"
                    + "DATA_PROPOSTA DATE NOT NULL,"
                    + "STATUS VARCHAR(50) NOT NULL,"
                    + "CONSTRAINT FK_PLANO_RISCO FOREIGN KEY (RISCO_ID) REFERENCES RISCO(ID)"
                    + ");");

            // Tabela 5: ACAO_MITIGACAO (Resposta Tática - Tarefas)
            stmt.execute("CREATE TABLE IF NOT EXISTS ACAO_MITIGACAO("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "PLANO_ID INT NOT NULL," // Relaciona N:1 com PLANO_MITIGACAO
                    + "DESCRICAO VARCHAR(500) NOT NULL,"
                    + "RESPONSAVEL VARCHAR(255) NOT NULL,"
                    + "PRAZO_CONCLUSAO DATE,"
                    + "DATA_CONCLUSAO DATE,"
                    + "PROGRESSO VARCHAR(255),"
                    + "OBSERVACOES VARCHAR(500),"
                    + "CONSTRAINT FK_ACAO_PLANO FOREIGN KEY (PLANO_ID) REFERENCES PLANO_MITIGACAO(ID)"
                    + ");");

            // ***************************************************************
            // 3. SEEDING (POVOAMENTO INICIAL)
            // ***************************************************************
            
            // Verifica se a tabela TIPO_RISCO está vazia para evitar duplicidade na inicialização.
            String checkSql = "SELECT COUNT(*) FROM TIPO_RISCO";
            // O uso de try-with-resources garante o fechamento do ResultSet
            try (ResultSet rs = stmt.executeQuery(checkSql)) {
                if (rs.next() && rs.getInt(1) == 0) { // Se o count for 0, insere.
                    String insertSql = "INSERT INTO TIPO_RISCO (NOME, DESCRICAO) VALUES ('Risco Operacional', 'Riscos de falhas internas.')";
                    stmt.executeUpdate(insertSql);
                    System.out.println("Tipo de risco padrão 'Risco Operacional' adicionado.");
                }
            }
            System.out.println("Banco de dados inicializado com sucesso.");
        } catch (SQLException e) {
            // ***************************************************************
            // 4. TRATAMENTO DE ERROS NA CRIAÇÃO DO ESQUEMA
            // ***************************************************************
            // Captura erros que ocorram *após* a conexão ser estabelecida, mas durante
            // a execução dos comandos DDL (CREATE TABLE).
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }
}