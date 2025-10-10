package com.joao.gestaorisco;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Classe utilitária para gerenciar a conexão com o banco de dados H2.
 * Responsável por obter a conexão e inicializar a estrutura do banco de dados.
 */
public class DatabaseManager {

    // Constantes de conexão para o banco de dados H2
    private static final String JDBC_URL = "jdbc:h2:C:/DadosCompartilhados/gestaoderiscos_db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    /**
     * Obtém uma conexão com o banco de dados.
     * * @return uma conexão válida com o banco de dados.
     * @throws SQLException se a conexão falhar.
     */
    public static Connection getConnection() throws SQLException {
        // Em aplicações modernas (a partir do Java 6), o Driver Manager geralmente
        // encontra o driver automaticamente via ServiceLoader.
        // O bloco try-catch abaixo adiciona robustez caso o driver não seja encontrado.
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver H2 não encontrado.");
            throw new SQLException("Driver JDBC H2 indisponível.", e);
        }
     
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    /**
     * Inicializa o banco de dados, criando as tabelas se elas não existirem.
     * Adiciona um tipo de risco padrão para começar.
     */
    public static void initializeDatabase() {
        // Uso de try-with-resources garante que Connection e Statement sejam fechados.
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Criação das tabelas
            stmt.execute("CREATE TABLE IF NOT EXISTS TIPO_RISCO ("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "NOME VARCHAR(200) NOT NULL UNIQUE,"
                    + "DESCRICAO VARCHAR(255)"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS RISCO ("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "DESCRICAO VARCHAR(500) NOT NULL,"
                    + "ORIGEM VARCHAR(255),"
                    + "DATA_IDENTIFICACAO DATE NOT NULL,"
                    + "STATUS VARCHAR(50) NOT NULL,"
                    + "TIPO_RISCO_ID INT NOT NULL,"
                    + "CONSTRAINT FK_RISCO_TIPO FOREIGN KEY (TIPO_RISCO_ID) REFERENCES TIPO_RISCO(ID)"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS AVALIACAO("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "RISCO_ID INT NOT NULL,"
                    + "IMPACTO INT NOT NULL,"
                    + "PROBABILIDADE INT NOT NULL,"
                    + "URGENCIA INT NOT NULL,"
                    + "PONTUACAO_GERAL INT NOT NULL,"
                    + "DATA_AVALIACAO DATE NOT NULL,"
                    + "RESPONSAVEL VARCHAR(255) NOT NULL,"
                    + "JUSTIFICATIVA VARCHAR(500),"
                    + "CONSTRAINT FK_AVALIACAO_RISCOS FOREIGN KEY (RISCO_ID) REFERENCES RISCO(ID)"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS PLANO_MITIGACAO("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "RISCO_ID INT NOT NULL,"
                    + "DESCRICAO VARCHAR(500) NOT NULL,"
                    + "DATA_PROPOSTA DATE NOT NULL,"
                    + "STATUS VARCHAR(50) NOT NULL,"
                    + "CONSTRAINT FK_PLANO_RISCO FOREIGN KEY (RISCO_ID) REFERENCES RISCO(ID)"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS ACAO_MITIGACAO("
                    + "ID INT PRIMARY KEY AUTO_INCREMENT,"
                    + "PLANO_ID INT NOT NULL,"
                    + "DESCRICAO VARCHAR(500) NOT NULL,"
                    + "RESPONSAVEL VARCHAR(255) NOT NULL,"
                    + "PRAZO_CONCLUSAO DATE,"
                    + "DATA_CONCLUSAO DATE,"
                    + "PROGRESSO VARCHAR(255),"
                    + "OBSERVACOES VARCHAR(500),"
                    + "CONSTRAINT FK_ACAO_PLANO FOREIGN KEY (PLANO_ID) REFERENCES PLANO_MITIGACAO(ID)"
                    + ");");

            // Verifica e insere um tipo de risco padrão se a tabela estiver vazia
            String checkSql = "SELECT COUNT(*) FROM TIPO_RISCO";
            // O uso de try-with-resources garante o fechamento do ResultSet
            try (ResultSet rs = stmt.executeQuery(checkSql)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertSql = "INSERT INTO TIPO_RISCO (NOME, DESCRICAO) VALUES ('Risco Operacional', 'Riscos de falhas internas.')";
                    stmt.executeUpdate(insertSql);
                    System.out.println("Tipo de risco padrão 'Risco Operacional' adicionado.");
                }
            }
            System.out.println("Banco de dados inicializado com sucesso.");
        } catch (SQLException e) {
            // Em caso de erro na inicialização (excluindo falha de conexão, que é tratada acima)
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
        }
    }
}
