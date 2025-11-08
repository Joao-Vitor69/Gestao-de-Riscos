package com.joao.gestaorisco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet; 
import java.sql.Statement;

/**
 * Classe de Modelo para Avaliação de Risco, implementando o Padrão Active Record
 * para lidar com sua própria persistência.
 */
public class Avaliacao { 

    // ---------- ATRIBUTOS (Mapeamento das Colunas da Tabela AVALIACAO) ----------
    private int id;                  // Chave primária (PK), gerada pelo banco.
    private int riscoId;             // Chave estrangeira (FK) para a tabela RISCO.
    private int impacto;             // Métrica 1 da avaliação de risco.
    private int probabilidade;       // Métrica 2 da avaliação de risco.
    private int urgencia;            // Métrica 3 da avaliação de risco.
    private int pontuacaoGeral;      // Resultado do cálculo: Impacto * Probabilidade * Urgência.
    private String dataAvaliacao;     // Data em que a avaliação foi realizada (yyyy-MM-dd).
    private String responsavel;       // Pessoa que realizou a avaliação.
    private String justificativa;     // Racional para as notas atribuídas.

    // ---------- CONSTRUTOR P/ NOVAS AVALIAÇÕES ----------
    /**
     * Construtor para criar uma nova avaliação antes de salvá-la no banco.
     * @param riscoId ID do Risco que está sendo avaliado.
     * @param impacto Valor de 1 a 5.
     * @param probabilidade Valor de 1 a 5.
     * @param urgencia Valor de 1 a 5.
     * @param dataAvaliacao Data da avaliação.
     * @param responsavel Responsável.
     * @param justificativa Racional.
     */
    public Avaliacao(int riscoId, int impacto, int probabilidade, int urgencia, 
                     String dataAvaliacao, String responsavel, String justificativa) {
        this.riscoId = riscoId;
        this.impacto = impacto;
        this.probabilidade = probabilidade;
        this.urgencia = urgencia;
        this.dataAvaliacao = dataAvaliacao;
        this.responsavel = responsavel;
        this.justificativa = justificativa;
        // O cálculo da pontuação geral é feito no construtor
        this.pontuacaoGeral = calcularPontuacao(impacto, probabilidade, urgencia);
    }
    
    // ---------- CONSTRUTOR P/ CARREGAR AVALIAÇÕES DO BANCO ----------
    /**
     * Construtor completo usado pelos métodos de busca para recriar o objeto a partir do ResultSet.
     */
    public Avaliacao(int id, int riscoId, int impacto, int probabilidade, int urgencia, 
                     int pontuacaoGeral, String dataAvaliacao, String responsavel, String justificativa) {
        this.id = id;
        this.riscoId = riscoId;
        this.impacto = impacto;
        this.probabilidade = probabilidade;
        this.urgencia = urgencia;
        this.pontuacaoGeral = pontuacaoGeral;
        this.dataAvaliacao = dataAvaliacao;
        this.responsavel = responsavel;
        this.justificativa = justificativa;
    }
    
    // ---------- LÓGICA DE NEGÓCIO ----------
    /**
     * Calcula a pontuação geral do risco (Impacto * Probabilidade * Urgência).
     */
    private int calcularPontuacao(int impacto, int probabilidade, int urgencia) {
        return impacto * probabilidade * urgencia;
    }

    // ---------- PERSISTÊNCIA (Método Salvar Active Record) ----------
    /**
     * Salva o objeto Avaliacao no banco de dados (INSERT).
     */
    public void salvar() {
        // SQL para inserção. O ID é gerado automaticamente pelo banco (H2).
        String sql = "INSERT INTO AVALIACAO (RISCO_ID, IMPACTO, PROBABILIDADE, URGENCIA, PONTUACAO_GERAL, DATA_AVALIACAO, RESPONSAVEL, JUSTIFICATIVA) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        // O Statement.RETURN_GENERATED_KEYS é crucial para obter o ID gerado.
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Mapeamento dos parâmetros
            stmt.setInt(1, this.getRiscoId());        
            stmt.setInt(2, this.getImpacto());        
            stmt.setInt(3, this.getProbabilidade());  
            stmt.setInt(4, this.getUrgencia());       
            stmt.setInt(5, this.getPontuacaoGeral()); 
            stmt.setString(6, this.getDataAvaliacao()); 
            stmt.setString(7, this.getResponsavel());   
            stmt.setString(8, this.getJustificativa()); 

            int affectedRows = stmt.executeUpdate(); // Executa o INSERT.

            if (affectedRows > 0) {
                // Se a inserção foi bem-sucedida, tenta recuperar o ID gerado pelo banco (H2).
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.setId(rs.getInt(1)); // Atribui o ID gerado de volta ao objeto.
                    }
                }
            }

        } catch (SQLException e) { 
            // Tratamento de erros de banco de dados durante a operação.
            System.err.println("Erro ao salvar Avaliação: " + e.getMessage());
            e.printStackTrace();   
        }
    }
    
    // ---------- MÉTODOS DE BUSCA (Para consulta) ----------

    /**
     * Busca a avaliação mais recente para um Risco específico (pela Pontuação Geral).
     * @param riscoId O ID do Risco a ser consultado.
     * @return O objeto Avaliacao mais recente ou null.
     */
    public static Avaliacao buscarMaisRecentePorRisco(int riscoId) {
        // Seleciona a avaliação com a maior pontuação (ou a mais recente, dependendo do critério)
        // Usaremos DATA_AVALIACAO para simular a mais recente.
        String sql = "SELECT * FROM AVALIACAO WHERE RISCO_ID = ? ORDER BY DATA_AVALIACAO DESC, ID DESC LIMIT 1";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, riscoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapeia o ResultSet para um novo objeto Avaliacao (usando o construtor completo)
                    return new Avaliacao(
                        rs.getInt("ID"),
                        rs.getInt("RISCO_ID"),
                        rs.getInt("IMPACTO"),
                        rs.getInt("PROBABILIDADE"),
                        rs.getInt("URGENCIA"),
                        rs.getInt("PONTUACAO_GERAL"),
                        rs.getString("DATA_AVALIACAO"),
                        rs.getString("RESPONSAVEL"),
                        rs.getString("JUSTIFICATIVA")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar avaliação mais recente: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Retorna nulo se não encontrar.
    }
    
    // ---------- Getters e Setters (Essenciais para o JSP) ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRiscoId() { return riscoId; }
    public int getImpacto() { return impacto; }
    public int getProbabilidade() { return probabilidade; }
    public int getUrgencia() { return urgencia; }
    public int getPontuacaoGeral() { return pontuacaoGeral; }
    public String getDataAvaliacao() { return dataAvaliacao; }
    public String getResponsavel() { return responsavel; }
    public String getJustificativa() { return justificativa; }

    // ---------- MÉTODO toString (Para exibição amigável na CLI) ----------
    @Override
    public String toString() {
        return "ID: " + id + 
               " | Impacto: " + impacto +
               " | Probabilidade: " + probabilidade +
               " | Urgência: " + urgencia +
               " | Pontuação Geral: " + pontuacaoGeral +
               " | Data: " + dataAvaliacao;
    }
}
