package com.joao.gestaorisco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// A classe PlanoMitigacao agora armazena os dados E lida com a persistência,
// eliminando a necessidade da classe PlanoMitigacaoDAO.
public class PlanoMitigacao {
    
    // Atributos privados
    private int id; 
    private int riscoId; 
    private String descricao; 
    private String dataProposta; 
    private String status; 

    // Construtor completo (Para ler do banco)
    public PlanoMitigacao(int id, int riscoId, String descricao, String dataProposta, String status) {
        this.id = id;
        this.riscoId = riscoId;
        this.descricao = descricao;
        this.dataProposta = dataProposta;
        this.status = status;
    }

    // Construtor simplificado (Para criar um novo objeto)
    public PlanoMitigacao(int riscoId, String descricao, String dataProposta, String status) {
        this.riscoId = riscoId;
        this.descricao = descricao;
        this.dataProposta = dataProposta;
        this.status = status;
    }

    // Getters
    public int getId() { return id; }
    public int getRiscoId() { return riscoId; }
    public String getDescricao() { return descricao; }
    public String getDataProposta() { return dataProposta; }
    public String getStatus() { return status; }
    
    // Setter para o ID: necessário para atualizar o objeto com o ID gerado pelo banco.
    public void setId(int id) { this.id = id; }


    // ------------------------------------------------------------------
    // ---------- MÉTODOS DE PERSISTÊNCIA (MIGRADOS DO DAO) -------------
    // ------------------------------------------------------------------

    /**
     * Insere a instância atual (this) no banco de dados e atualiza seu ID.
     */
    public void salvar() {
        String sql = "INSERT INTO PLANO_MITIGACAO (RISCO_ID, DESCRICAO, DATA_PROPOSTA, STATUS) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             // Indica que queremos recuperar as chaves geradas
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Atribui os valores da própria instância aos placeholders
            stmt.setInt(1, this.getRiscoId());
            stmt.setString(2, this.getDescricao());
            stmt.setString(3, this.getDataProposta());
            stmt.setString(4, this.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            // Tenta recuperar a chave gerada e atualizar o ID do objeto
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.setId(generatedKeys.getInt(1)); // Define o ID gerado
                        System.out.println("Plano de mitigação registrado com ID: " + this.id);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar Plano de Mitigação: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Busca todos os planos de mitigação associados a um risco específico.
     * Método estático para que possa ser chamado sem uma instância do objeto.
     * @param riscoId O ID do risco a ser buscado.
     * @return Uma lista de objetos PlanoMitigacao.
     */
    public static List<PlanoMitigacao> buscarPorRisco(int riscoId) {
        List<PlanoMitigacao> planos = new ArrayList<>();
        String sql = "SELECT * FROM PLANO_MITIGACAO WHERE RISCO_ID = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, riscoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Mapeia o ResultSet para um novo objeto PlanoMitigacao usando o construtor completo
                    planos.add(new PlanoMitigacao(
                        rs.getInt("ID"),
                        rs.getInt("RISCO_ID"),
                        rs.getString("DESCRICAO"),
                        rs.getString("DATA_PROPOSTA"),
                        rs.getString("STATUS")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar planos por risco: " + e.getMessage());
            e.printStackTrace();
        }
        return planos;
    }
    
    /**
     * Lista ID, descrição e status de todos os planos no console.
     * Método estático de utilidade.
     */
    public static void listarTodos() {
        String sql = "SELECT ID, DESCRICAO, STATUS FROM PLANO_MITIGACAO";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("--- Planos de Mitigação Disponíveis ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("ID") + 
                                   " | Descrição: " + rs.getString("DESCRICAO") + 
                                   " | Status: " + rs.getString("STATUS"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os planos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Descrição: " + descricao + " | Status: " + status + " | Risco ID: " + riscoId;
    }
}