package com.joao.gestaorisco;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet principal para a aplicação de Gestão de Riscos. Este servlet atua
 * como o controlador central, roteando as requisições e utilizando diretamente
 * os métodos de persistência incorporados nas classes de modelo (sem a camada
 * DAO explícita).
 */
@WebServlet("/app")
public class GestaodeRiscoServlet extends HttpServlet {

	// Adiciona o método init() para inicializar o banco de dados
	@Override
	public void init() throws ServletException {
		super.init();
		// ESTE É O CÓDIGO ADICIONADO:
		// Inicializa o banco de dados (cria tabelas se não existirem).
		DatabaseManager.initializeDatabase();
		System.out.println("Banco de Dados inicializado para o Servlet.");
	}

	// As instâncias dos DAOs foram removidas, pois a lógica de persistência
	// agora reside nas classes de modelo (Risco, PlanoMitigacao, etc.).
	// private final RiscoDAO riscoDAO = new RiscoDAO();
	// private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	// private final PlanoMitigacaoDAO planoDAO = new PlanoMitigacaoDAO();
	// private final AcaoMitigacaoDAO acaoDAO = new AcaoMitigacaoDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String acao = request.getParameter("acao");

		if (acao == null) {
			acao = "listarRiscos";
		}

		try {
			switch (acao) {
			case "listarRiscos":
				listarRiscos(request, response);
				break;
			case "formNovoRisco":
				formNovoRisco(request, response);
				break;
			case "formAvaliacao":
				formAvaliacao(request, response);
				break;
			case "formPlano":
				formPlano(request, response);
				break;
			case "formAcao":
				formAcao(request, response); // Novo método adicionado
				break;
			case "listarPlanosPorRisco":
				listarPlanosPorRisco(request, response);
				break;
			case "listarAcoesPorPlano":
				listarAcoesPorPlano(request, response);
				break;
			default:
				listarRiscos(request, response);
				break;
			}
		} catch (Exception e) {
			// Tratamento genérico de exceção
			request.setAttribute("erro", "Ocorreu um erro interno: " + e.getMessage());
			listarRiscos(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String acao = request.getParameter("acao");

		if (acao == null) {
			acao = "listarRiscos";
		}

		try {
			switch (acao) {
			case "inserirRisco":
				inserirRisco(request, response);
				break;
			case "avaliarRisco":
				avaliarRisco(request, response);
				break;
			case "inserirPlano":
				inserirPlano(request, response);
				break;
			case "inserirAcao":
				inserirAcao(request, response);
				break;
			default:
				listarRiscos(request, response);
				break;
			}
		} catch (Exception e) {
			// Tratamento genérico de exceção
			request.setAttribute("erro", "Ocorreu um erro interno: " + e.getMessage());
			listarRiscos(request, response);
		}
	}

	/**
	 * Lista todos os riscos do banco de dados e os exibe em listaRiscos.jsp.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void listarRiscos(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// O método estático listarTodos() da classe Risco lida com a busca no BD.
		List<Risco> listaRiscos = Risco.listar();
		// Coloca a lista de riscos na requisição para ser acessada pelo JSP.
		request.setAttribute("listaRiscos", listaRiscos);
		// Encaminha a requisição para a página JSP.
		request.getRequestDispatcher("/listaRiscos.jsp").forward(request, response);
	}

	/**
	 * Encaminha para o formulário de novo risco.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void formNovoRisco(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/formNovoRisco.jsp").forward(request, response);
	}

	/**
	 * Insere um novo risco no banco. Agora chama {@code risco.salvar()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void inserirRisco(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Extrai os parâmetros
			String descricao = request.getParameter("descricao");
			String origem = request.getParameter("origem");
			// A data é gerada automaticamente no formato yyyy-MM-dd
			String dataIdentificacao = LocalDate.now().toString();
			int tipoRiscoId = Integer.parseInt(request.getParameter("tipoRiscoId"));

			// Cria e salva o objeto Risco, usando o método de instância 'salvar()'
			Risco risco = new Risco(descricao, origem, dataIdentificacao, tipoRiscoId);
			risco.salvar();

			// Redireciona para a lista principal após a inserção
			response.sendRedirect("app?acao=listarRiscos");
		} catch (NumberFormatException e) {
			request.setAttribute("erro", "ID do Tipo de Risco inválido ou ausente.");
			request.getRequestDispatcher("/formNovoRisco.jsp").forward(request, response);
		} catch (Exception e) {
			request.setAttribute("erro", "Erro ao inserir risco: " + e.getMessage());
			request.getRequestDispatcher("/formNovoRisco.jsp").forward(request, response);
		}
	}

	/**
	 * Prepara o formulário de avaliação de risco.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void formAvaliacao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Código Correto no Servlet (Exemplo: GestaodeRiscoServlet.java ou
		// Deletar.java)

		// CÓDIGO CORRETO NO SERVLET (Exemplo: deletar.java)

		try {
			int id = Integer.parseInt(request.getParameter("id"));

			// Simplesmente CHAME O MÉTODO e ignore o retorno
			Risco.excluir(id);

			// Se chegou até aqui, é sucesso
			response.sendRedirect("listarRiscos");

		} catch (Exception e) {
			// Trata erro (SQL, NumberFormat, etc.)
			request.setAttribute("erro", "Erro ao excluir: " + e.getMessage());
			request.getRequestDispatcher("listarRiscos").forward(request, response);
		}
	}

	/**
	 * Processa a avaliação de um risco. Agora chama {@code avaliacao.salvar()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	// Este método deve estar dentro da classe GestaodeRiscoServlet.java

	private void avaliarRisco(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    
	    // Para simplificar o código, você pode criar uma função auxiliar (veja abaixo)
	    // ou usar um tratamento inline como este:

	    String impactoStr = request.getParameter("impacto");
	    String probabilidadeStr = request.getParameter("probabilidade");
	    String urgenciaStr = request.getParameter("urgencia");
	    
	    try {
	        // --- 🔑 CORREÇÃO CHAVE ---
	        // Se a string for nula ou vazia, lançamos um erro de formato claro 
	        // ou usamos um valor padrão (mas o erro é melhor, pois o campo é 'required').
	        
	        int impacto = (impactoStr != null && !impactoStr.trim().isEmpty()) 
	                      ? Integer.parseInt(impactoStr.trim()) 
	                      : 0; // Se for 0, o cálculo da pontuação falha (bom indicador de erro)
	                      
	        int probabilidade = (probabilidadeStr != null && !probabilidadeStr.trim().isEmpty()) 
	                            ? Integer.parseInt(probabilidadeStr.trim()) 
	                            : 0;
	                            
	        int urgencia = (urgenciaStr != null && !urgenciaStr.trim().isEmpty()) 
	                       ? Integer.parseInt(urgenciaStr.trim()) 
	                       : 0;

	        // O idRisco também deve ser tratado
	        int riscoId = Integer.parseInt(request.getParameter("idRisco")); 
	        
	        // 1. CÁLCULO AGORA FUNCIONA SEM ERRO
	        int pontuacaoGeral = impacto * probabilidade * urgencia; 
	        
	        // 2. Outros parâmetros
	        String responsavel = request.getParameter("responsavel");
	        String justificativa = request.getParameter("justificativa");
	        String dataAvaliacao = request.getParameter("dataAvaliacao"); // Garanta que este campo seja passado ou use a data atual (LocalDate.now())
	        
	        // 3. Criação e Persistência do Objeto Avaliacao
	        // (Assumindo que Avaliacao.java já foi migrada corretamente)
	        Avaliacao avaliacao = new Avaliacao(riscoId, impacto, probabilidade, urgencia, dataAvaliacao, responsavel, justificativa);
	        avaliacao.salvar();

	        // 4. Redireciona para a listagem principal de riscos
	        response.sendRedirect("app?acao=listarRiscos"); 

	    } catch (NumberFormatException e) {
	        // Trata qualquer erro de conversão
	        request.setAttribute("erro", "Erro de formato nos campos de Impacto, Probabilidade ou Urgência. Certifique-se de que todos foram preenchidos com números.");
	        // Redireciona de volta ao formulário, mantendo o ID do Risco na requisição
	        request.setAttribute("idRisco", request.getParameter("idRisco")); 
	        request.getRequestDispatcher("/formAvaliacao.jsp").forward(request, response);
	    } catch (Exception e) {
	        // Trata outros erros (ex: erro de banco de dados)
	        request.setAttribute("erro", "Erro ao registrar a Avaliação de Risco: " + e.getMessage());
	        request.setAttribute("idRisco", request.getParameter("idRisco"));
	        request.getRequestDispatcher("/formAvaliacao.jsp").forward(request, response);
	    }
	}

	/**
	 * Prepara o formulário de registro de Plano de Mitigação, garantindo que o ID
	 * do Risco esteja presente e seja válido.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void formPlano(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Obtém o ID do Risco da requisição (do parâmetro 'idRisco' da URL/Form)
			int idRisco = Integer.parseInt(request.getParameter("idRisco"));

			// Armazena o ID do Risco na requisição para que o JSP possa usá-lo
			request.setAttribute("idRisco", idRisco);

			// Encaminha para o formulário
			request.getRequestDispatcher("/formPlano.jsp").forward(request, response);
		} catch (NumberFormatException e) {
			request.setAttribute("erro", "ID do Risco inválido.");
			request.getRequestDispatcher("/listaRiscos.jsp").forward(request, response);
		}
	}

	/**
	 * Insere um novo plano de mitigação para um risco. Agora chama
	 * {@code plano.salvar()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void inserirPlano(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Exemplo de como deve ficar seu Servlet de exclusão:

		try {
			// 1. Obtém o ID do parâmetro
			int riscoId = Integer.parseInt(request.getParameter("id"));

			// ***************************************************************
			// CORREÇÃO: A linha que estava dando erro deve ser substituída por:
			Risco.excluir(riscoId);
			// ***************************************************************

			// 2. Se a chamada acima não lançou exceção, a exclusão foi bem-sucedida.
			response.sendRedirect("listarRiscos"); // Redireciona para a lista

		} catch (NumberFormatException e) {
			// Se o ID for inválido
			request.setAttribute("erro", "ID de Risco inválido para exclusão.");
			request.getRequestDispatcher("/listarRiscos").forward(request, response);

		} catch (SQLException e) {
			// 3. Se deu erro no banco (ex: chave estrangeira), capturamos aqui.
			request.setAttribute("erro",
					"Erro ao excluir Risco. Verifique se existem Avaliações ou Planos vinculados a ele.");
			request.getRequestDispatcher("/listarRiscos").forward(request, response);

		} catch (Exception e) {
			// Trata outras exceções
			// ...
		}
	}

	/**
	 * Lista todos os planos de mitigação para um risco específico.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void listarPlanosPorRisco(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			int idRisco = Integer.parseInt(request.getParameter("idRisco"));

			// Busca os planos e coloca na requisição
			List<PlanoMitigacao> listaPlanos = PlanoMitigacao.buscarPorRisco(idRisco);
			request.setAttribute("listaPlanos", listaPlanos);
			request.setAttribute("riscoId", idRisco); // Para referência no JSP

			// Encaminha para o JSP que lista os planos e ações
			request.getRequestDispatcher("/listaAcoes.jsp").forward(request, response);
		} catch (NumberFormatException e) {
			request.setAttribute("erro", "ID do Risco inválido.");
			listarRiscos(request, response);
		}
	}

	/**
	 * Prepara o formulário de registro de Ação de Mitigação, garantindo que o ID do
	 * Plano esteja presente e seja válido.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void formAcao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Obtém o ID do Plano da requisição (do parâmetro 'planoId' da URL/Form)
			int planoId = Integer.parseInt(request.getParameter("planoId"));

			// Armazena o ID do Plano na requisição para que o JSP possa usá-lo
			request.setAttribute("planoId", planoId);

			// Encaminha para o formulário
			request.getRequestDispatcher("/formAcao.jsp").forward(request, response);
		} catch (NumberFormatException e) {
			request.setAttribute("erro", "ID do Plano de Mitigação inválido ou ausente.");
			request.getRequestDispatcher("/listaRiscos.jsp").forward(request, response);
		}
	}

	/**
	 * Insere uma nova ação de mitigação para um plano. Agora chama
	 * {@code acao.salvar()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void inserirAcao(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Extrai o parâmetro como string primeiro
			String planoIdStr = request.getParameter("planoId");

			// Validação robusta para evitar NumberFormatException na string "null"
			if (planoIdStr == null || planoIdStr.trim().isEmpty() || planoIdStr.trim().equalsIgnoreCase("null")) {
				throw new NumberFormatException("planoId é nulo ou inválido.");
			}

			// Agora que validamos, podemos converter para int
			int planoId = Integer.parseInt(planoIdStr);

			String descricao = request.getParameter("descricao");
			String responsavel = request.getParameter("responsavel");
			String prazoConclusao = request.getParameter("prazoConclusao");

			// O progresso é definido internamente como 'Pendente' no modelo AcaoMitigacao
			AcaoMitigacao acao = new AcaoMitigacao(planoId, descricao, responsavel, prazoConclusao);
			// Salva a ação no banco
			acao.salvar();

			// Redireciona para a lista principal após a inserção
			response.sendRedirect("app?acao=listarRiscos");
		} catch (NumberFormatException e) {
			request.setAttribute("erro", "ID do Plano de Mitigação inválido ou ausente.");
			// Em caso de erro, redireciona de volta ao formulário
			request.getRequestDispatcher("/formAcao.jsp").forward(request, response);
		} catch (Exception e) {
			request.setAttribute("erro", "Erro ao inserir ação de mitigação: " + e.getMessage());
			// Em caso de erro, redireciona de volta ao formulário
			request.getRequestDispatcher("/formAcao.jsp").forward(request, response);
		}
	}

	/**
	 * Lista todas as ações de mitigação para um plano específico.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void listarAcoesPorPlano(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			int planoId = Integer.parseInt(request.getParameter("planoId"));

			// Busca as ações e coloca na requisição
			List<AcaoMitigacao> listaAcoes = AcaoMitigacao.buscarPorPlano(planoId);
			request.setAttribute("listaAcoes", listaAcoes);
			request.setAttribute("planoId", planoId); // Para referência no JSP

			// Encaminha para o JSP que lista as ações
			request.getRequestDispatcher("/detalheAcoes.jsp").forward(request, response);
		} catch (NumberFormatException e) {
			request.setAttribute("erro", "ID do Plano inválido.");
			// Se o ID for inválido, volta para a lista de planos/riscos
			response.sendRedirect("app?acao=listarRiscos");
		}
	}

}
