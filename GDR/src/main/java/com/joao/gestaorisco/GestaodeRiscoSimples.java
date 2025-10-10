package com.joao.gestaorisco;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List; // Import necessário para o método visualizarRiscos

// A classe `GestaodeRiscoSimples` atua como a interface de linha de comando (CLI)
// para o sistema de gestão de riscos. Ela coordena as interações com o usuário
// e utiliza os métodos de persistência incorporados nos modelos (Active Record).
public class GestaodeRiscoSimples {

	// Scanner para ler a entrada do usuário a partir do console.
	private static Scanner n = new Scanner(System.in);

	// As instâncias DAO foram removidas, pois a lógica de persistência
	// agora está nos métodos estáticos e de instância das classes de modelo.
	// private static RiscoDAO riscoDAO = new RiscoDAO();
	// private static AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
	// private static PlanoMitigacaoDAO planoDAO = new PlanoMitigacaoDAO();
	// private static AcaoMitigacaoDAO acaoDAO = new AcaoMitigacaoDAO();

	// Método principal que inicia a aplicação.
	public static void main(String[] args) {
		// Inicializa o banco de dados, criando as tabelas se elas ainda não existirem.
		DatabaseManager.initializeDatabase();
		// O bloco try-finally garante que o scanner seja fechado de forma segura.
		try {
			exibirMenuPrincipal();
		} finally {
			n.close();
		}
	}

	// Método que exibe o menu principal e gerencia o fluxo da aplicação.
	private static void exibirMenuPrincipal() {
		while (true) { // Loop infinito para manter o menu ativo até que o usuário saia.
			System.out.println("\n--- Sistema de Gestão de Riscos ---");
			System.out.println("1. Registrar Novo Risco");
			System.out.println("2. Avaliar Risco");
			System.out.println("3. Registrar Plano de Mitigação");
			System.out.println("4. Registrar Ação de Mitigação");
			System.out.println("5. Visualizar Riscos");
			System.out.println("6. Sair");
			System.out.print("Escolha uma opção: ");

			try {
				// Lê a opção do usuário e consome a quebra de linha restante.
				int opcao = n.nextInt();
				n.nextLine();

				// Usa um switch para executar a ação correspondente à opção escolhida.
				switch (opcao) {
				case 1:
					registrarNovoRisco();
					break;
				case 2:
					avaliarRiscoExistente();
					break;
				case 3:
					registrarPlanoMitigacao();
					break;
				case 4:
					registrarAcaoMitigacao();
					break;
				case 5:
					visualizarRiscos();
					break;
				case 6:
					System.out.println("Saindo...");
					return; // Sai do método e encerra o loop.
				default:
					System.out.println("Opção inválida.");
				}
			} catch (InputMismatchException e) {
				// Captura a exceção se o usuário digitar algo que não seja um número.
				System.out.println("Opção inválida. Por favor, digite um número.");
				n.nextLine(); // Limpa o buffer do scanner para evitar um loop infinito.
			}
		}
	}

	// Método para registrar um novo risco.
	private static void registrarNovoRisco() {
		System.out.print("Descrição: ");
		String descricao = n.nextLine();
		System.out.print("Origem: ");
		String origem = n.nextLine();

		// Lista os tipos de risco disponíveis chamando o método estático do modelo
		// Risco.
		Risco.listarTiposRisco();

		System.out.print("Tipo do Risco (ID): ");
		int tipoRiscoId = n.nextInt();
		n.nextLine(); // Consome a quebra de linha.

		// Obtém a data atual no formato 'yyyy-MM-dd'.
		String dataIdentificacao = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

		// Cria um novo objeto `Risco` e o salva no banco usando o método de instância
		// 'salvar()'.
		Risco risco = new Risco(descricao, origem, dataIdentificacao, tipoRiscoId);
		risco.salvar();
		System.out.println("Risco registrado!");
	}

	// Método para avaliar um risco existente.
	private static void avaliarRiscoExistente() {
		visualizarRiscos(); // Exibe a lista de riscos para que o usuário escolha.
		System.out.print("Digite o ID do risco que deseja avaliar: ");

		try {
			int idRisco = n.nextInt();
			n.nextLine();

			// Verifica se o ID do risco realmente existe no banco usando o método estático.
			if (!Risco.existe(idRisco)) {
				System.out.println("ID de risco não encontrado. Por favor, digite um ID válido.");
				return; // Retorna ao menu principal.
			}

			System.out.print("Impacto (1-5): ");
			int impacto = n.nextInt();
			System.out.print("Probabilidade (1-5): ");
			int probabilidade = n.nextInt();
			System.out.print("Urgência (1-5): ");
			int urgencia = n.nextInt();
			n.nextLine();

			// Calcula a pontuação geral do risco.
			int pontuacaoGeral = impacto * probabilidade * urgencia;

			System.out.print("Responsável: ");
			String responsavel = n.nextLine();
			System.out.print("Justificativa: ");
			String justificativa = n.nextLine();

			// Obtém a data atual.
			String dataAvaliacao = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

			// Cria um objeto `Avaliacao` e o salva no banco usando o método de instância
			// 'salvar()'.
			Avaliacao avaliacao = new Avaliacao(idRisco, impacto, probabilidade, urgencia, pontuacaoGeral,
					dataAvaliacao, responsavel, justificativa);
			avaliacao.salvar();

			// Atualiza o status do risco para 'Avaliado' após a avaliação.
			Risco.atualizarStatus(idRisco, "Avaliado");

			System.out.println("Risco avaliado com sucesso! Pontuação geral: " + pontuacaoGeral);

		} catch (InputMismatchException e) {
			System.out.println("ID inválido ou valor numérico esperado. Por favor, digite um número.");
			n.nextLine();
		}
	}

	// Método para exibir todos os riscos cadastrados.
	private static void visualizarRiscos() {
		// Itera sobre a lista de riscos retornada pelo método estático 'listar()' do
		// modelo Risco.
		List<Risco> riscos = Risco.listar();
		if (riscos.isEmpty()) {
			System.out.println("\n--- Nenhum risco registrado. ---");
			return;
		}
		System.out.println("\n--- Lista de Riscos ---");
		for (Risco risco : riscos) {
			System.out.println(risco);
		}
	}

	// Método para registrar um novo plano de mitigação para um risco.
	private static void registrarPlanoMitigacao() {
		visualizarRiscos();
		System.out.print("Digite o ID do risco para o qual você quer criar um plano: ");
		try {
			int idRisco = n.nextInt();
			n.nextLine();

			// Verifica se o ID do risco é válido.
			if (!Risco.existe(idRisco)) {
				System.out.println("ID de risco não encontrado.");
				return;
			}

			System.out.print("Descrição do Plano: ");
			String descricao = n.nextLine();

			String dataProposta = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

			// Cria e salva o plano de mitigação no banco.
			PlanoMitigacao plano = new PlanoMitigacao(idRisco, descricao, dataProposta, "Proposto");
			plano.salvar();

			// Atualiza o status do risco para 'Com Plano'.
			Risco.atualizarStatus(idRisco, "Com Plano");
			System.out.println("Plano de mitigação registrado com sucesso!");
		} catch (InputMismatchException e) {
			System.out.println("Entrada inválida. Por favor, digite um número.");
			n.nextLine();
		}
	}

	// Método para registrar uma nova ação de mitigação para um plano existente.
	private static void registrarAcaoMitigacao() {
		// Lista todos os planos de mitigação para que o usuário possa escolher.
		PlanoMitigacao.listarTodos();

		System.out.print("Digite o ID do plano de mitigação para adicionar a ação: ");
		try {
			int idPlano = n.nextInt();
			n.nextLine();

			System.out.print("Descrição da Ação: ");
			String descricao = n.nextLine();
			System.out.print("Responsável: ");
			String responsavel = n.nextLine();
			System.out.print("Prazo de Conclusão (yyyy-MM-dd): ");
			String prazoConclusao = n.nextLine();

			// Cria e salva a ação de mitigação no banco de dados usando o método de
			// instância.
			AcaoMitigacao acao = new AcaoMitigacao(idPlano, descricao, responsavel, prazoConclusao);
			acao.salvar();
			System.out.println("Ação de mitigação registrada com sucesso!");
		} catch (InputMismatchException e) {
			System.out.println("Entrada inválida. Por favor, digite um número.");
			n.nextLine();
		}
	}
}
