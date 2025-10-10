package com.joao.gestaorisco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet; // Importação necessária para buscar dados

// Esta classe agora é responsável tanto pelos dados quanto pela sua persistência (Persistência Anêmica)
public class Avaliacao { 

    // ---------- ATRIBUTOS ----------
    private int id;                  
    private int riscoId;              
    private int impacto;              
    private int probabilidade;        
    private int urgencia;             
    private int pontuacaoGeral;       
    private String dataAvaliacao;     
    private String responsavel;       
    private String justificativa;     

    // ---------- CONSTRUTOR P/ NOVAS AVALIAÇÕES ----------
    // Inicializa uma avaliação com os dados fornecidos (id é gerado pelo banco)
    public Avaliacao(int riscoId, int impacto, int probabilidade, int urgencia, int pontuacaoGeral, String dataAvaliacao, String responsavel, String justificativa) {
        // O ID é 0/não-definido neste ponto, pois será gerado no banco.
        this.riscoId = riscoId;
        this.impacto = impacto;
        this.probabilidade = probabilidade;
        this.urgencia = urgencia;
        this.pontuacaoGeral = pontuacaoGeral;
        this.dataAvaliacao = dataAvaliacao;
        this.responsavel = responsavel;
        this.justificativa = justificativa;
    }
    
    // ---------- CONSTRUTOR P/ CARREGAR DO BANCO (COM ID) ----------
    // Este construtor é útil para instanciar o objeto quando ele é lido do banco de dados.
    public Avaliacao(int id, int riscoId, int impacto, int probabilidade, int urgencia, int pontuacaoGeral, String dataAvaliacao, String responsavel, String justificativa) {
        this(riscoId, impacto, probabilidade, urgencia, pontuacaoGeral, dataAvaliacao, responsavel, justificativa);
        this.id = id;
    }


    // ---------- GETTERS e SETTER para ID (Necessário para a persistência) ----------
    public int getId() { return id; }  
    // Adicionado setter para ID, pois o banco de dados atribui o valor após o INSERT
    public void setId(int id) { this.id = id; } 
    
    // Os outros getters permanecem os mesmos:
    public int getRiscoId() { return riscoId; }
    public int getImpacto() { return impacto; }
    public int getProbabilidade() { return probabilidade; }
    public int getUrgencia() { return urgencia; }
    public int getPontuacaoGeral() { return pontuacaoGeral; }
    public String getDataAvaliacao() { return dataAvaliacao; }
    public String getResponsavel() { return responsavel; }
    public String getJustificativa() { return justificativa; }

    
    // ------------------------------------------------------------------
    // ---------- MÉTODOS DE PERSISTÊNCIA (Substituindo o DAO) ----------
    // ------------------------------------------------------------------
    
    /**
     * Insere a instância atual de Avaliacao no banco de dados.
     * Tenta retornar o ID gerado pelo banco e atribuí-lo ao objeto.
     */
    public void salvar() { 
        // Comando SQL com opção para retornar chaves geradas (Statement.RETURN_GENERATED_KEYS)
        String sql = "INSERT INTO AVALIACAO (RISCO_ID, IMPACTO, PROBABILIDADE, URGENCIA, PONTUACAO_GERAL, DATA_AVALIACAO, RESPONSAVEL, JUSTIFICATIVA) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(); 
             // Prepara a query e indica que queremos receber as chaves geradas
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) { 

            stmt.setInt(1, this.getRiscoId());        
            stmt.setInt(2, this.getImpacto());        
            stmt.setInt(3, this.getProbabilidade());  
            stmt.setInt(4, this.getUrgencia());       
            stmt.setInt(5, this.getPontuacaoGeral()); 
            stmt.setString(6, this.getDataAvaliacao()); 
            stmt.setString(7, this.getResponsavel());   
            stmt.setString(8, this.getJustificativa()); 

            int affectedRows = stmt.executeUpdate(); 

            if (affectedRows > 0) {
                // Tenta recuperar o ID gerado pelo banco e atualizar o objeto
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.setId(rs.getInt(1)); // Atribui o ID gerado ao objeto
                    }
                }
            }

        } catch (SQLException e) { 
            System.err.println("Erro ao salvar Avaliação: " + e.getMessage());
            e.printStackTrace();   
        }
    }
    
    // ---------- MÉTODO toString ----------
    @Override
    public String toString() {
        return "ID: " + id + 
               " | Impacto: " + impacto +
               " | Probabilidade: " + probabilidade +
               " | Pontuacao: " + pontuacaoGeral +
               " | Responsável: " + responsavel;
    }
}