package com.joao.gestaorisco;

import com.joao.gestaorisco.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de modelo (Model) para a entidade Risco, utilizando o padrão Active
 * Record para lidar com sua própria persistência (CRUD).
 */
public class Risco {
	// Atributos privados que mapeiam as colunas da tabela RISCO
	private int id;
	private String descricao;
	private String origem;
	private String dataIdentificacao; // Formato yyyy-MM-dd
	private String status; // Ex: Identificado, Avaliado, Em Mitigação, Encerrado
	private int tipoRiscoId;

	// Construtor usado para criar um novo Risco (o ID será gerado pelo BD)
	public Risco(String descricao, String origem, String dataIdentificacao, int tipoRiscoId) {
		this.descricao = descricao;
		this.origem = origem;
		this.dataIdentificacao = dataIdentificacao;
		this.status = "Identificado"; // Status inicial padrão para todo novo risco
		this.tipoRiscoId = tipoRiscoId;
	}

	// Construtor usado para carregar um Risco existente do BD
	// Utilizado pelos métodos de busca (como listar()) para recriar o objeto a
	// partir do banco.
	public Risco(int id, String descricao, String origem, String dataIdentificacao, String status, int tipoRiscoId) {
		this.id = id;
		this.descricao = descricao;
		this.origem = origem;
		this.dataIdentificacao = dataIdentificacao;
		this.status = status;
		this.tipoRiscoId = tipoRiscoId;
	}

	// --- PERSISTÊNCIA (Métodos Active Record) ---

	/**
	 * Salva o objeto Risco no banco de dados. Se o ID for 0 (novo), faz um INSERT.
	 * Se tiver ID, faz um UPDATE. No contexto da aplicação web atual, foca no
	 * INSERT.
	 */
	public void salvar() {
		if (this.id == 0) {
			// INSERT (Para novos riscos)
			String sql = "INSERT INTO RISCO (DESCRICAO, ORIGEM, DATA_IDENTIFICACAO, STATUS, TIPO_RISCO_ID) VALUES (?, ?, ?, ?, ?)";

			try (Connection conn = DatabaseManager.getConnection();
					PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

				stmt.setString(1, this.descricao);
				stmt.setString(2, this.origem);
				stmt.setString(3, this.dataIdentificacao);
				stmt.setString(4, this.status);
				stmt.setInt(5, this.tipoRiscoId);

				int affectedRows = stmt.executeUpdate();

				if (affectedRows > 0) {
					try (ResultSet rs = stmt.getGeneratedKeys()) {
						if (rs.next()) {
							this.id = rs.getInt(1); // Atribui o ID gerado de volta ao objeto.
						}
					}
				}
			} catch (SQLException e) {
				System.err.println("Erro ao inserir Risco: " + e.getMessage());
				e.printStackTrace();
			}
		}
		// O método de UPDATE seria implementado aqui para edição ou mudança de status.
	}

	/**
	 * Atualiza o status de um Risco específico no banco.
	 * 
	 * @param novoStatus Novo status a ser atribuído (Ex: "Avaliado").
	 */
	public void atualizarStatus(String novoStatus) {
		String sql = "UPDATE RISCO SET STATUS = ? WHERE ID = ?";

		try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, novoStatus);
			stmt.setInt(2, this.id);

			stmt.executeUpdate();
			this.status = novoStatus; // Atualiza o status do objeto em memória
			System.out.println("Status do Risco ID " + this.id + " atualizado para: " + novoStatus);

		} catch (SQLException e) {
			System.err.println("Erro ao atualizar status do Risco: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// --- FUNCIONALIDADES DE BUSCA (Métodos Estáticos) ---

	/**
	 * Lista todos os riscos do banco de dados.
	 * 
	 * @return Uma lista de objetos Risco.
	 */
	// Conteúdo a ser adicionado ou ajustado em Risco.java (além dos métodos
	// existentes)

	// -------------------------------------------------------------
	// 1. MÉTODO LISTAR (READ - Lista Todos)
	// -------------------------------------------------------------
	public static List<Risco> listar() {
		List<Risco> riscos = new ArrayList<>();
		String sql = "SELECT * FROM RISCO";

		try (Connection conn = DatabaseManager.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				riscos.add(new Risco(rs.getInt("ID"), rs.getString("DESCRICAO"), rs.getString("ORIGEM"),
						rs.getString("DATA_IDENTIFICACAO"), rs.getString("STATUS"), rs.getInt("TIPO_RISCO_ID")));
			}
		} catch (SQLException e) {
			System.err.println("Erro ao listar riscos: " + e.getMessage());
			e.printStackTrace();
		}
		return riscos;
	}

	// -------------------------------------------------------------
	// 2. MÉTODO BUSCAR POR ID (READ - Necessário para Editar)
	// -------------------------------------------------------------
	public static Risco buscarPorId(int id) {
		Risco risco = null;
		String sql = "SELECT * FROM RISCO WHERE ID = ?";

		try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					risco = new Risco(rs.getInt("ID"), rs.getString("DESCRICAO"), rs.getString("ORIGEM"),
							rs.getString("DATA_IDENTIFICACAO"), rs.getString("STATUS"), rs.getInt("TIPO_RISCO_ID"));
				}
			}
		} catch (SQLException e) {
			System.err.println("Erro ao buscar Risco por ID: " + e.getMessage());
			e.printStackTrace();
		}
		return risco;
	}

	// -------------------------------------------------------------
	// 3. MÉTODO ATUALIZAR (UPDATE)
	// -------------------------------------------------------------
	// -------------------------------------------------------------
	// 3. MÉTODO ATUALIZAR (UPDATE - Método de INSTÂNCIA)
//	    Este método usa os atributos do próprio objeto (this)
	// -------------------------------------------------------------
	public boolean atualizar() {
	    // O SQL usa os campos do objeto em memória
	    String sql = "UPDATE RISCO SET DESCRICAO = ?, ORIGEM = ?, DATA_IDENTIFICACAO = ?, STATUS = ?, TIPO_RISCO_ID = ? WHERE ID = ?";
	    boolean sucesso = false;

	    try (Connection conn = DatabaseManager.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        // 1. Preenche os parâmetros com os atributos da instância (this)
	        stmt.setString(1, this.descricao);
	        stmt.setString(2, this.origem);
	        stmt.setString(3, this.dataIdentificacao);
	        stmt.setString(4, this.status);
	        stmt.setInt(5, this.tipoRiscoId);
	        // 2. O ID da instância é usado no WHERE para saber qual linha atualizar
	        stmt.setInt(6, this.id); 

	        int linhas = stmt.executeUpdate();
	        sucesso = (linhas > 0);

	    } catch (SQLException e) {
	        System.err.println("Erro ao atualizar risco (instância): " + e.getMessage());
	        e.printStackTrace();
	    }
	    return sucesso;
	}
	// -------------------------------------------------------------
	// 4. MÉTODO EXCLUIR (DELETE)
	// -------------------------------------------------------------
	// Este é o método estático que o Servlet deve chamar
	// -----------------------------------------------------------------
	// MÉTODO EXCLUIR (DELETE) - Deve ser VOID e lançar SQLException
	// -----------------------------------------------------------------
	// Localização: com.joao.gestaorisco.Risco.java (ou com.isabelle.gestaoproblema.Problema.java)

	public static boolean excluir(int id) throws SQLException {
	    String sql = "DELETE FROM RISCO WHERE ID = ?"; // Ou 'DELETE FROM PROBLEMA WHERE ID = ?'

	    // Assumimos falha até prova em contrário
	    boolean sucesso = false; 

	    try (Connection conn = DatabaseManager.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, id);
	        
	        // Execute a exclusão e conte quantas linhas foram afetadas
	        int linhasAfetadas = stmt.executeUpdate();
	        
	        // Se alguma linha foi excluída (linhasAfetadas > 0), o sucesso é verdadeiro
	        if (linhasAfetadas > 0) {
	            sucesso = true;
	        }
	        
	    } // O bloco catch não precisa ser alterado, a SQLException ainda é lançada se houver erro de BD.

	    // ********************************************
	    // CORREÇÃO: Esta linha é obrigatória, pois o método retorna 'boolean'
	    return sucesso; 
	    // ********************************************
	}
	// --- Getters e Setters ---
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

	// --- Método toString para exibição na CLI ---
	@Override
	public String toString() {
		// Retorna uma representação formatada do objeto Risco
		return String.format("[ID: %d] %s (Origem: %s) | Status: %s | Tipo ID: %d", id, descricao, origem, status,
				tipoRiscoId);
	}
}
