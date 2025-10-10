package com.joao.gestaorisco;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

	/**
	 * Lida com as requisições GET. Roteia a requisição para a página JSP apropriada
	 * com base no parâmetro "acao".
	 *
	 * @param request  o objeto HttpServletRequest que contém a requisição do
	 *                 cliente
	 * @param response o objeto HttpServletResponse que envia a resposta ao cliente
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String acao = request.getParameter("acao");

		// Usa um switch para um controle de fluxo mais limpo
		switch (acao != null ? acao : "listarRiscos") {
		case "formRisco":
			// Redireciona para o formulário de cadastro de novo risco
			request.getRequestDispatcher("/formNovoRisco.jsp").forward(request, response);
			break;
		case "formAvaliacao":
			// Obtém o ID do risco para a avaliação
			int idRiscoAvaliacao = Integer.parseInt(request.getParameter("idRisco"));
			// Armazena o ID na requisição para ser usado no formulário JSP
			request.setAttribute("idRisco", idRiscoAvaliacao);
			request.getRequestDispatcher("/formAvaliacao.jsp").forward(request, response);
			break;
		case "formPlano":
			// Obtém o ID do risco para a criação do plano
			int idRiscoPlano = Integer.parseInt(request.getParameter("idRisco"));
			// Armazena o ID na requisição para ser usado no formulário JSP
			request.setAttribute("idRisco", idRiscoPlano);
			request.getRequestDispatcher("/formPlano.jsp").forward(request, response);
			break;
			// Código sugerido para GestaodeRiscoServlet.java (doGet)

		case "formAcao":
		    // Tenta obter o ID do plano da URL. Se não vier, causará um erro 400 ou 500.
		    String planoIdParam = request.getParameter("planoId");

		    if (planoIdParam == null || planoIdParam.isEmpty()) {
		        // Redireciona para um local seguro (Lista de Riscos) se o ID estiver faltando
		        // O servidor não sabe para qual plano registrar a ação!
		        response.sendRedirect("app?acao=listarRiscos&erro=ID_Plano_Ausente");
		        return;
		    }
		    
		    // Converte e armazena o ID na requisição (para ser lido pelo JSP)
		    try {
		        int planoId = Integer.parseInt(planoIdParam); 
		        request.setAttribute("planoId", planoId);
		        request.getRequestDispatcher("/formAcao.jsp").forward(request, response);
		    } catch (NumberFormatException e) {
		        // Se a string não for um número válido (ex: "abc")
		        response.sendRedirect("app?acao=listarRiscos&erro=ID_Plano_Invalido");
		    }
		    break;
		case "listarRiscos":
			listarRiscos(request, response);
			break;
		case "listarPlanosPorRisco":
			int i = Integer.parseInt(request.getParameter("idRisco"));
			listarPlanosPorRisco(request, response, i);
			break;
		case "listarAcoesPorPlano":
			int planoIdListar = Integer.parseInt(request.getParameter("planoId"));
			listarAcoesPorPlano(request, response, planoIdListar);
			break;
		default:
			// Ação padrão: lista todos os riscos
			response.sendRedirect("app?acao=listarRiscos");
			break;
			
		}
	}

	/**
	 * Lida com as requisições POST. Roteia a requisição para o método de inserção
	 * apropriado com base no parâmetro "acao".
	 *
	 * @param request  o objeto HttpServletRequest que contém a requisição do
	 *                 cliente
	 * @param response o objeto HttpServletResponse que envia a resposta ao cliente
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String acao = request.getParameter("acao");

		// Usa um switch para um controle de fluxo mais limpo no POST
		switch (acao != null ? acao : "") {
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
			// Redireciona para a lista de riscos se a ação não for reconhecida
			response.sendRedirect("app?acao=listarRiscos");
			break;
		}
	}

	/**
	 * Obtém e exibe a lista de todos os riscos. Agora chama o método estático
	 * {@code Risco.listar()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void listarRiscos(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Usa o método estático 'listar' do modelo Risco
		List<Risco> riscos = Risco.listar();
		// Adiciona a lista de riscos como um atributo da requisição
		request.setAttribute("listaRiscos", riscos);
		// Encaminha a requisição para a página JSP que exibirá a lista
		request.getRequestDispatcher("/listaRiscos.jsp").forward(request, response);
	}

	/**
	 * Obtém e exibe a lista de planos para um risco específico. Agora chama o
	 * método estático {@code PlanoMitigacao.buscarPorRisco()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @param idRisco  o ID do risco
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void listarPlanosPorRisco(HttpServletRequest request, HttpServletResponse response, int idRisco)
			throws ServletException, IOException {
		// Busca os planos de mitigação associados a um risco específico
		List<PlanoMitigacao> planos = PlanoMitigacao.buscarPorRisco(idRisco);
		// Adiciona a lista de planos à requisição
		request.setAttribute("listaPlanos", planos);
		// Encaminha a requisição para a página JSP que exibirá a lista
		request.getRequestDispatcher("/listaPlanos.jsp").forward(request, response);
	}

	/**
	 * Obtém e exibe a lista de ações para um plano específico. Agora chama o método
	 * estático {@code AcaoMitigacao.buscarPorPlano()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @param planoId  o ID do plano
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void listarAcoesPorPlano(HttpServletRequest request, HttpServletResponse response, int planoId)
			throws ServletException, IOException {
		// Busca as ações de mitigação associadas a um plano específico
		List<AcaoMitigacao> acoes = AcaoMitigacao.buscarPorPlano(planoId);
		// Adiciona a lista de ações à requisição
		request.setAttribute("listaAcoes", acoes);
		// Encaminha a requisição para a página JSP que exibirá a lista
		request.getRequestDispatcher("/listaAcoes.jsp").forward(request, response);
	}

	/**
	 * Insere um novo risco no banco de dados. Agora chama o método de instância
	 * {@code novoRisco.salvar()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void inserirRisco(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Extrai os parâmetros da requisição e converte para os tipos corretos
			String descricao = request.getParameter("descricao");
			String origem = request.getParameter("origem");
			int tipoRiscoId = Integer.parseInt(request.getParameter("tipoRiscoId"));
			String dataIdentificacao = LocalDate.now().toString();

			// Cria um novo objeto Risco com os dados do formulário
			Risco novoRisco = new Risco(descricao, origem, dataIdentificacao, tipoRiscoId);
			// Salva o novo risco chamando o método de instância do modelo
			novoRisco.salvar();

			// Redireciona para a página de listagem após a inserção
			response.sendRedirect("app?acao=listarRiscos");
		} catch (NumberFormatException e) {
			// Em caso de erro de formato de número, exibe uma mensagem para o usuário
			request.setAttribute("erro", "ID do Tipo de Risco inválido.");
			// Retorna o usuário para o formulário com a mensagem de erro
			request.getRequestDispatcher("/formNovoRisco.jsp").forward(request, response);
		}
	}

	/**
	 * Avalia um risco existente no banco de dados. Agora chama
	 * {@code avaliacao.salvar()} e {@code Risco.atualizarStatus()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void avaliarRisco(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Extrai os parâmetros e converte para inteiros
			int idRisco = Integer.parseInt(request.getParameter("idRisco"));
			int impacto = Integer.parseInt(request.getParameter("impacto"));
			int probabilidade = Integer.parseInt(request.getParameter("probabilidade"));
			int urgencia = Integer.parseInt(request.getParameter("urgencia"));
			String responsavel = request.getParameter("responsavel");
			String justificativa = request.getParameter("justificativa");

			// Calcula a pontuação e obtém a data atual
			int pontuacaoGeral = impacto * probabilidade * urgencia;
			String dataAvaliacao = LocalDate.now().toString();

			// Cria um novo objeto Avaliacao com os dados e a pontuação calculada
			Avaliacao avaliacao = new Avaliacao(idRisco, impacto, probabilidade, urgencia, pontuacaoGeral,
					dataAvaliacao, responsavel, justificativa);
			// Salva a nova avaliação no banco de dados
			avaliacao.salvar();
			// Atualiza o status do risco para 'Avaliado' usando o método estático
			Risco.atualizarStatus(idRisco, "Avaliado");

			response.sendRedirect("app?acao=listarRiscos");
		} catch (NumberFormatException e) {
			// Em caso de erro, define uma mensagem de erro e redireciona para o formulário
			request.setAttribute("erro", "Valores de avaliação inválidos.");
			request.getRequestDispatcher("/formAvaliacao.jsp").forward(request, response);
		}
	}

	/**
	 * Insere um novo plano de mitigação para um risco. Agora chama
	 * {@code plano.salvar()} e {@code Risco.atualizarStatus()}.
	 *
	 * @param request  o objeto HttpServletRequest
	 * @param response o objeto HttpServletResponse
	 * @throws ServletException se ocorrer um erro de servlet
	 * @throws IOException      se ocorrer um erro de I/O
	 */
	private void inserirPlano(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Extrai os parâmetros e cria um novo objeto PlanoMitigacao
			int idRisco = Integer.parseInt(request.getParameter("idRisco"));
			String descricao = request.getParameter("descricao");
			String dataProposta = LocalDate.now().toString();

			PlanoMitigacao plano = new PlanoMitigacao(idRisco, descricao, dataProposta, "Proposto");
			// Salva o plano no banco
			plano.salvar();
			// Atualiza o status do risco associado
			Risco.atualizarStatus(idRisco, "Com Plano");

			response.sendRedirect("app?acao=listarRiscos");
		} catch (NumberFormatException e) {
			request.setAttribute("erro", "ID do Risco inválido.");
			request.getRequestDispatcher("/formPlano.jsp").forward(request, response);
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
			// Extrai os parâmetros para criar a nova ação
			int planoId = Integer.parseInt(request.getParameter("planoId"));
			String descricao = request.getParameter("descricao");
			String responsavel = request.getParameter("responsavel");
			String prazoConclusao = request.getParameter("prazoConclusao");

			// O progresso é definido internamente como 'Pendente' no modelo AcaoMitigacao
			// O construtor correto é chamado aqui:
			AcaoMitigacao acao = new AcaoMitigacao(planoId, descricao, responsavel, prazoConclusao);
			// Salva a ação no banco
			acao.salvar();

			// Redireciona para a lista principal após a inserção
			response.sendRedirect("app?acao=listarRiscos");
		} catch (NumberFormatException e) {
			request.setAttribute("erro", "ID do Plano inválido.");
			request.getRequestDispatcher("/formAcao.jsp").forward(request, response);
		}
	}
}