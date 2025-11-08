package com.joao.gestaorisco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Modelo para Ações de Mitigação, implementando o Padrão Active Record
 * para lidar com sua própria persistência.
 */
public class AcaoMitigacao {
    
    // ATRIBUTOS (FIELDS)
    private int id; 
    private int planoId; // Chave estrangeira para a tabela PLANO_MITIGACAO
    private String descricao; 
    private String responsavel; 
    private String prazoConclusao; // Data limite (yyyy-MM-dd)
    private String progresso; // Ex: Pendente, Em Andamento, Concluído
    // Opcionais (não usados nos construtores atuais para simplificar)
    private String dataConclusao; 
    private String observacoes; 

    // CONSTRUTOR P/ NOVO OBJETO (O ID do Plano é OBRIGATÓRIO)
    public AcaoMitigacao(int planoId, String descricao, String responsavel, String prazoConclusao) {
        this.planoId = planoId;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.prazoConclusao = prazoConclusao;
        this.progresso = "Pendente"; // Status inicial
    }

    // CONSTRUTOR P/ CARREGAR DO BANCO (Completo)
    public AcaoMitigacao(int id, int planoId, String descricao, String responsavel, String prazoConclusao, String progresso) {
        this.id = id;
        this.planoId = planoId;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.prazoConclusao = prazoConclusao;
        this.progresso = progresso;
    }

    /**
     * Salva a Ação de Mitigação no banco de dados (INSERT).
     * O ID da ação é gerado automaticamente pelo banco (H2).
     */
    public void salvar() {
        // SQL para inserção. O ID é gerado, o PROGRESSO é definido no construtor.
        String sql = "INSERT INTO ACAO_MITIGACAO (PLANO_ID, DESCRICAO, RESPONSAVEL, PRAZO_CONCLUSAO, PROGRESSO) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        // Uso de try-with-resources para garantir o fechamento de Connection, PreparedStatement e ResultSet
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Mapeamento dos parâmetros para o SQL
            stmt.setInt(1, this.planoId);
            stmt.setString(2, this.descricao);
            stmt.setString(3, this.responsavel);
            stmt.setString(4, this.prazoConclusao);
            stmt.setString(5, this.progresso); 
            
            int affectedRows = stmt.executeUpdate(); // Executa o INSERT.

            if (affectedRows > 0) {
                // Tenta recuperar o ID gerado (necessário para o Active Record)
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.id = rs.getInt(1); // Atribui o ID gerado de volta ao objeto.
                    }
                }
            }

        } catch (SQLException e) { 
            System.err.println("Erro ao salvar a Ação de Mitigação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- FUNCIONALIDADE DE BUSCA (USADA PELO SERVLET) ---
    /**
     * Busca todas as Ações de Mitigação associadas a um Plano de Mitigação específico.
     * @param planoId O ID do plano de mitigação.
     * @return Lista de AcaoMitigacao.
     */
    public static List<AcaoMitigacao> buscarPorPlano(int planoId) {
        List<AcaoMitigacao> acoes = new ArrayList<>();
        // A busca é feita usando o PLANO_ID
        String sql = "SELECT * FROM ACAO_MITIGACAO WHERE PLANO_ID = ?"; 
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // O ID do Plano é usado como critério de busca
            stmt.setInt(1, planoId); 
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Mapeia o resultado do ResultSet para um novo objeto AcaoMitigacao
                    AcaoMitigacao acao = new AcaoMitigacao(
                        rs.getInt("ID"),
                        rs.getInt("PLANO_ID"), // <-- O valor é lido aqui
                        rs.getString("DESCRICAO"),
                        rs.getString("RESPONSAVEL"),
                        rs.getString("PRAZO_CONCLUSAO"),
                        rs.getString("PROGRESSO")
                    );
                    acoes.add(acao);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar ações por plano: " + e.getMessage());
            e.printStackTrace();
        }
        return acoes;
    }

    // --- Getters (Essenciais para o JSP) ---
    public int getId() { return id; }
    public int getPlanoId() { return planoId; }
    public String getDescricao() { return descricao; }
    public String getResponsavel() { return responsavel; }
    public String getPrazoConclusao() { return prazoConclusao; }
    public String getProgresso() { return progresso; }
    // ... (Setters omitidos para manter o foco na imutabilidade e persistência)
    
    // Método toString para exibição em Console (CLI)
    @Override
    public String toString() {
        return String.format("Ação ID: %d | Plano ID: %d | Descrição: %s | Responsável: %s | Prazo: %s | Progresso: %s",
                             id, planoId, descricao, responsavel, prazoConclusao, progresso);
    }
}
