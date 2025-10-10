package com.joao.gestaorisco;


import com.joao.gestaorisco.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de modelo (Model) para a entidade Risco, utilizando o padrão Active Record
 * para lidar com sua própria persistência (CRUD).
 */
public class Risco {
    private int id;
    private String descricao;
    private String origem;
    private String dataIdentificacao; // Formato yyyy-MM-dd
    private String status;
    private int tipoRiscoId;

    // Construtor usado para criar um novo Risco (o ID será gerado pelo BD)
    public Risco(String descricao, String origem, String dataIdentificacao, int tipoRiscoId) {
        this.descricao = descricao;
        this.origem = origem;
        this.dataIdentificacao = dataIdentificacao;
        this.status = "Identificado"; // Status inicial padrão
        this.tipoRiscoId = tipoRiscoId;
    }

    // Construtor usado para carregar um Risco existente do BD
    public Risco(int id, String descricao, String origem, String dataIdentificacao, String status, int tipoRiscoId) {
        this.id = id;
        this.descricao = descricao;
        this.origem = origem;
        this.dataIdentificacao = dataIdentificacao;
        this.status = status;
        this.tipoRiscoId = tipoRiscoId;
    }

    // --- Métodos de Persistência (Active Record) ---

    /**
     * Insere um novo Risco no banco de dados.
     */
    public void salvar() {
        String sql = "INSERT INTO RISCO (DESCRICAO, ORIGEM, DATA_IDENTIFICACAO, STATUS, TIPO_RISCO_ID) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, this.descricao);
            stmt.setString(2, this.origem);
            stmt.setString(3, this.dataIdentificacao);
            stmt.setString(4, this.status);
            stmt.setInt(5, this.tipoRiscoId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Tenta recuperar a chave gerada e atualizar o ID do objeto
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar o risco: " + e.getMessage());
        }
    }

    /**
     * Lista todos os riscos cadastrados no banco de dados.
     * @return Uma lista de objetos Risco.
     */
    public static List<Risco> listar() {
        List<Risco> riscos = new ArrayList<>();
        String sql = "SELECT ID, DESCRICAO, ORIGEM, DATA_IDENTIFICACAO, STATUS, TIPO_RISCO_ID FROM RISCO";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Risco risco = new Risco(
                    rs.getInt("ID"),
                    rs.getString("DESCRICAO"),
                    rs.getString("ORIGEM"),
                    rs.getString("DATA_IDENTIFICACAO"),
                    rs.getString("STATUS"),
                    rs.getInt("TIPO_RISCO_ID")
                );
                riscos.add(risco);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar riscos: " + e.getMessage());
        }
        return riscos;
    }

    /**
     * Verifica se um risco com o ID especificado existe no banco de dados.
     * @param id O ID do risco a ser verificado.
     * @return true se o risco existir, false caso contrário.
     */
    public static boolean existe(int id) {
        String sql = "SELECT COUNT(*) FROM RISCO WHERE ID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar existência do risco: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lista todos os tipos de risco cadastrados.
     */
    public static void listarTiposRisco() {
        String sql = "SELECT ID, NOME, DESCRICAO FROM TIPO_RISCO";
        System.out.println("\n--- Tipos de Risco Disponíveis ---");
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf("ID: %d | Nome: %s | Descrição: %s%n", 
                                  rs.getInt("ID"), 
                                  rs.getString("NOME"), 
                                  rs.getString("DESCRICAO"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar tipos de risco: " + e.getMessage());
        }
    }

    /**
     * Atualiza o campo STATUS do risco no banco de dados.
     * @param id O ID do risco a ser atualizado.
     * @param novoStatus O novo status a ser definido.
     */
    public static void atualizarStatus(int id, String novoStatus) {
        String sql = "UPDATE RISCO SET STATUS = ? WHERE ID = ?";

        // Usando try-with-resources para garantir o fechamento da conexão e do statement
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Aviso: Nenhuma linha afetada ao atualizar o status do Risco ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o status do risco: " + e.getMessage());
        }
    }
    
    // --- Getters e Setters (Omissos para brevidade, mas devem ser incluídos em código real) ---
    // Você pode adicioná-los se precisar de acesso externo aos campos.
    
    // --- Método toString para exibição na CLI ---

    @Override
    public String toString() {
        return String.format("[ID: %d] %s (Origem: %s) | Status: %s | Tipo ID: %d",
                             id, descricao, origem, status, tipoRiscoId);
    }

    // Getters para a classe Avaliacao, PlanoMitigacao e AcaoMitigacao
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDataIdentificacao() {
        return dataIdentificacao;
    }

    public void setDataIdentificacao(String dataIdentificacao) {
        this.dataIdentificacao = dataIdentificacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTipoRiscoId() {
        return tipoRiscoId;
    }

    public void setTipoRiscoId(int tipoRiscoId) {
        this.tipoRiscoId = tipoRiscoId;
    }
}
