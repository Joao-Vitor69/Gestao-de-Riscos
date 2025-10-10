package com.joao.gestaorisco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Necessário para gerar chaves
import java.util.ArrayList;
import java.util.List;

public class AcaoMitigacao {
    // ATRIBUTOS (FIELDS)
    private int id; 
    private int planoId; 
    private String descricao; 
    private String responsavel; 
    private String prazoConclusao; 
    private String dataConclusao; 
    private String progresso; 
    private String observacoes; 

    // CONSTRUTOR P/ NOVO OBJETO (O ID será gerado pelo banco)
    public AcaoMitigacao(int planoId, String descricao, String responsavel, String prazoConclusao) {
        this.planoId = planoId;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.prazoConclusao = prazoConclusao;
        this.progresso = "Pendente"; // Status inicial
    }

    // CONSTRUTOR P/ CARREGAR DO BANCO (Completo, com ID e Progresso)
    public AcaoMitigacao(int id, int planoId, String descricao, String responsavel, String prazoConclusao, String progresso) {
        this.id = id;
        this.planoId = planoId;
        this.descricao = descricao;
        this.responsavel = responsavel;
        this.prazoConclusao = prazoConclusao;
        this.progresso = progresso;
    }

    // GETTERS
    public int getId() { return id; }
    public int getPlanoId() { return planoId; }
    public String getDescricao() { return descricao; }
    public String getResponsavel() { return responsavel; }
    public String getPrazoConclusao() { return prazoConclusao; }
    public String getDataConclusao() { return dataConclusao; }
    public String getProgresso() { return progresso; }
    public String getObservacoes() { return observacoes; }
    
    // SETTERS
    // Se o erro estava aqui, era porque o código anterior estava faltando!
    public void setId(int id) { this.id = id; }
    public void setPlanoId(int planoId) { this.planoId = planoId; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public void setPrazoConclusao(String prazoConclusao) { this.prazoConclusao = prazoConclusao; }
    public void setDataConclusao(String dataConclusao) { this.dataConclusao = dataConclusao; }
    public void setProgresso(String progresso) { this.progresso = progresso; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public String toString() {
        return "ID: " + id + " | Ação: " + descricao + " | Responsável: " + responsavel + " | Progresso: " + progresso;
    }

    // --- FUNCIONALIDADE DE INSERÇÃO (MIGREI A LÓGICA DE RECUPERAR O ID) ---
    public void salvar() {
        // Incluí a opção para recuperar o ID gerado automaticamente.
        String sql = "INSERT INTO ACAO_MITIGACAO (PLANO_ID, DESCRICAO, RESPONSAVEL, PRAZO_CONCLUSAO, PROGRESSO) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             // Indica que queremos recuperar o ID gerado.
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { 
            
            stmt.setInt(1, this.getPlanoId());
            stmt.setString(2, this.getDescricao());
            stmt.setString(3, this.getResponsavel());
            stmt.setString(4, this.getPrazoConclusao());
            stmt.setString(5, this.getProgresso());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Tenta recuperar o ID gerado e atualizar o objeto
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.setId(generatedKeys.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao salvar a Ação de Mitigação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- FUNCIONALIDADE DE BUSCA (MIGRADA DO DAO) ---
    public static List<AcaoMitigacao> buscarPorPlano(int planoId) {
        List<AcaoMitigacao> acoes = new ArrayList<>();
        String sql = "SELECT * FROM ACAO_MITIGACAO WHERE PLANO_ID = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, planoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Mapeamento do ResultSet para o Objeto
                    AcaoMitigacao acao = new AcaoMitigacao(
                        rs.getInt("ID"),
                        rs.getInt("PLANO_ID"),
                        rs.getString("DESCRICAO"),
                        rs.getString("RESPONSAVEL"),
                        rs.getString("PRAZO_CONCLUSAO"),
                        rs.getString("PROGRESSO")
                        // Os campos dataConclusao e observacoes estão sendo ignorados neste construtor.
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
}