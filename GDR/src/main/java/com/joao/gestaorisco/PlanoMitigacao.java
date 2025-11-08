package com.joao.gestaorisco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Modelo para Plano de Mitigação, implementando o Padrão Active Record
 * para lidar com sua própria persistência.
 */
public class PlanoMitigacao {
    
    // Atributos privados - Mapeamento das colunas da tabela PLANO_MITIGACAO
    private int id;                 // Chave primária (gerada pelo banco).
    private int riscoId;            // Chave estrangeira, ligando ao Risco.
    private String descricao;        // Descrição do plano de ação.
    private String dataProposta;     // Data em que o plano foi criado (yyyy-MM-dd).
    private String status;           // Status atual do plano (ex: Proposto, Em Execução, Concluído).

    // Construtor completo (Para ler do banco)
    // Usado pelos métodos de busca (SELECT) para recriar o objeto a partir do ResultSet.
    public PlanoMitigacao(int id, int riscoId, String descricao, String dataProposta, String status) {
        this.id = id;
        this.riscoId = riscoId;
        this.descricao = descricao;
        this.dataProposta = dataProposta;
        this.status = status;
    }
    
    // Construtor para criar um novo Plano (o ID será gerado pelo BD)
    public PlanoMitigacao(int riscoId, String descricao, String dataProposta) {
        this.riscoId = riscoId;
        this.descricao = descricao;
        this.dataProposta = dataProposta;
        this.status = "Proposto"; // Status inicial padrão
    }
    
    // ---------- PERSISTÊNCIA (Método Salvar Active Record) ----------
    /**
     * Salva o objeto PlanoMitigacao no banco de dados (INSERT).
     */
    public void salvar() {
        // SQL para inserção. O ID é gerado automaticamente pelo banco (H2).
        String sql = "INSERT INTO PLANO_MITIGACAO (RISCO_ID, DESCRICAO, DATA_PROPOSTA, STATUS) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Mapeamento dos parâmetros
            stmt.setInt(1, this.riscoId);
            stmt.setString(2, this.descricao);
            stmt.setString(3, this.dataProposta);
            stmt.setString(4, this.status);
            
            int affectedRows = stmt.executeUpdate(); // Executa o INSERT.

            if (affectedRows > 0) {
                // Tenta recuperar o ID gerado
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1); // Atribui o ID gerado de volta ao objeto.
                    }
                }
            }

        } catch (SQLException e) { 
            System.err.println("Erro ao salvar o Plano de Mitigação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------- MÉTODOS DE BUSCA (Para consulta) ----------
    
    /**
     * Busca todos os planos de mitigação associados a um Risco específico.
     * @param riscoId O ID do Risco.
     * @return Lista de PlanoMitigacao.
     */
    public static List<PlanoMitigacao> buscarPorRisco(int riscoId) {
        List<PlanoMitigacao> planos = new ArrayList<>();
        // A busca é feita usando o RISCO_ID
        String sql = "SELECT * FROM PLANO_MITIGACAO WHERE RISCO_ID = ?"; 
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // O ID do Risco é usado como critério de busca
            stmt.setInt(1, riscoId); 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Mapeia o resultado do ResultSet para um novo objeto PlanoMitigacao
                    PlanoMitigacao plano = new PlanoMitigacao(
                        rs.getInt("ID"),
                        rs.getInt("RISCO_ID"),
                        rs.getString("DESCRICAO"),
                        rs.getString("DATA_PROPOSTA"),
                        rs.getString("STATUS")
                    );
                    planos.add(plano);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar planos por risco: " + e.getMessage());
            e.printStackTrace();
        }
        return planos;
    }
    
    /**
     * Lista ID, descrição e status de todos os planos no console (útil para a CLI).
     */
    public static void listarTodos() {
        String sql = "SELECT ID, DESCRICAO, STATUS FROM PLANO_MITIGACAO";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("--- Planos de Mitigação Disponíveis ---");
            while (rs.next()) {
                // Imprime os detalhes do plano no console
                System.out.println("ID: " + rs.getInt("ID") + 
                                   " | Descrição: " + rs.getString("DESCRICAO") + 
                                   " | Status: " + rs.getString("STATUS"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os planos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Busca um Plano de Mitigação pelo seu ID.
     * @param id O ID do plano.
     * @return O objeto PlanoMitigacao ou null.
     */
    public static PlanoMitigacao buscarPorId(int id) {
        String sql = "SELECT * FROM PLANO_MITIGACAO WHERE ID = ?"; 
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id); 
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PlanoMitigacao(
                        rs.getInt("ID"),
                        rs.getInt("RISCO_ID"),
                        rs.getString("DESCRICAO"),
                        rs.getString("DATA_PROPOSTA"),
                        rs.getString("STATUS")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar plano por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

 // Adicione este bloco dentro da classe PlanoMitigacao.java
    /**
     * Verifica se um Plano de Mitigação existe no banco de dados dado o seu ID.
     * @param id O ID do plano de mitigação.
     * @return true se o plano existe, false caso contrário.
     */
    public static boolean existe(int id) {
        // Usamos COUNT(*) para ser mais eficiente, pois não precisamos carregar todos os dados.
        String sql = "SELECT COUNT(*) FROM PLANO_MITIGACAO WHERE ID = ?"; 
        boolean existe = false;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Se o COUNT for maior que 0, o registro existe.
                    existe = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // Em caso de erro de banco, assumimos que por segurança ele não foi encontrado
            System.err.println("Erro ao verificar a existência do Plano de Mitigação: " + e.getMessage());
            // Se desejar forçar o erro, você pode lançar uma exceção de tempo de execução aqui.
            // throw new RuntimeException(e);
        }
        
        return existe;
    }

    // --- Getters (Essenciais para o JSP) ---
    public int getId() { return id; }
    public int getRiscoId() { return riscoId; }
    public String getDescricao() { return descricao; }
    public String getDataProposta() { return dataProposta; }
    public String getStatus() { return status; }
    
    // ... (Setters omitidos)

    @Override
    public String toString() {
        // Sobrescreve para fornecer uma representação de string útil do objeto
        return "ID: " + id + " | Descrição: " + descricao + " | Status: " + status + " | Risco ID: " + riscoId;
    }
}
