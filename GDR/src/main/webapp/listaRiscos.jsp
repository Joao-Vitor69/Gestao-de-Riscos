<%@page import="com.joao.gestaorisco.Risco"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%-- Importa a Tag Library JSTL Core, permitindo o uso de tags de controle como <c:if>, embora o código esteja usando Scriptlets for loops --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gestão de Riscos</title>
<style>
    /* Estilos CSS para a aparência e layout da página */
    body { 
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
        margin: 0; 
        padding: 40px; 
        background-color: #f4f7fa; 
        color: #333;
        display: flex;
        flex-direction: column;
        align-items: center; /* Centraliza o conteúdo horizontalmente */
    }
    h2 { 
        color: #2c3e50; 
        margin-bottom: 25px;
        border-bottom: 3px solid #34495e; /* Cor escura/neutra para destaque do título principal */
        padding-bottom: 10px;
        width: 100%;
        max-width: 1000px;
        text-align: center;
    }
    .header-links {
        width: 100%;
        max-width: 1000px;
        margin-bottom: 20px;
        text-align: right; /* Alinha o link de registro à direita */
    }
    .btn {
        text-decoration: none;
        padding: 10px 15px;
        border-radius: 8px;
        font-weight: bold;
        transition: background-color 0.3s ease;
        display: inline-block; /* Permite padding e margem */
    }
    .btn-register {
        background-color: #28a745; /* Verde */
        color: white;
    }
    .btn-register:hover {
        background-color: #1e7e34;
    }

    /* Estilos para a tabela */
    table {
        width: 100%;
        max-width: 1000px;
        border-collapse: collapse;
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        background-color: white;
        border-radius: 8px;
        overflow: hidden; /* Garante que os cantos arredondados sejam aplicados */
    }
    th, td {
        padding: 12px 15px;
        text-align: left;
        border-bottom: 1px solid #ddd;
    }
    th {
        background-color: #34495e; /* Cor escura */
        color: white;
        text-transform: uppercase;
        font-size: 0.9em;
    }
    tr:nth-child(even) {
        background-color: #f9f9f9; /* Linhas zebradas */
    }
    tr:hover {
        background-color: #f1f1f1;
    }
    /* Estilos para os botões dentro da tabela */
    .btn-action {
        text-decoration: none;
        padding: 6px 10px;
        margin-right: 5px;
        border-radius: 5px;
        font-size: 0.9em;
        font-weight: normal;
        display: inline-block;
    }
    .btn-evaluate {
        background-color: #f39c12; /* Laranja/Amarelo */
        color: white;
    }
    .btn-evaluate:hover {
        background-color: #e08e0b;
    }
    .btn-plan {
        background-color: #17a2b8; /* Ciano/Azul */
        color: white;
    }
    .btn-plan:hover {
        background-color: #138496;
    }
    .btn-delete {
        background-color: #e74c3c; /* Vermelho */
        color: white;
        margin-left: 10px; /* Adiciona espaçamento à esquerda */
    }
    .btn-delete:hover {
        background-color: #c0392b;
    }
    .error { 
        color: #e74c3c; 
        font-weight: bold; 
        margin-bottom: 15px;
        padding: 10px;
        border: 1px solid #e74c3c;
        background-color: #fceae9;
        border-radius: 6px;
    }
</style>
</head>
<body>
    <h2>Lista de Riscos Identificados</h2>
    
    <%-- Exibe a mensagem de erro (se houver) --%>
    <c:if test="${not empty erro}">
        <p class="error"><c:out value="${erro}"/></p>
    </c:if>

    <div class="header-links">
        <a href="app?acao=formNovoRisco" class="btn btn-register">
            + Registrar Novo Risco
        </a>
    </div>

    <table border="0">
        <thead>
            <tr>
                <th>ID</th>
                <th>Descrição</th>
                <th>Status</th>
                <th>Ações</th>
            </tr>
        </thead>
      <tbody>
    <%-- Trecho da tabela em listarRiscos.jsp --%>
<c:forEach var="risco" items="${requestScope.listaRiscos}">
    <tr>
        <td>${risco.id}</td>
        <td>${risco.descricao}</td>
        <%-- ... outras colunas ... --%>
        <td>
            <%-- Edição de Risco: Chama o Servlet 'editarRisco' para mostrar o formulário --%>
            | <a href="editarRisco?id=${risco.id}" class="btn-edit">Editar</a> 
            
            <%-- 🎯 AÇÃO: EXCLUIR Risco. Chama o Servlet 'deletarRisco' (que executa a exclusão e redireciona) --%>
            | <a href="deletarRisco?id=${risco.id}" class="btn-delete"
               onclick="return confirm('Tem certeza que deseja excluir o Risco (ID: ${risco.id})? Esta ação é irreversível.')">Excluir</a>
        </td>
        ...
							<td>
								<a href="gestao?acao=mostrarDetalhes&id=${problema.id}">Detalhes</a> |
								<a href="gestao?acao=formCausaRaiz&problemaId=${problema.id}">Plano Mitigacao</a> |
								<a href="gestao?acao=formAcaoCorretiva&problemaId=${problema.id}">Acao Mitigacao</a> |
								<a href="editar?id=${problema.id}" class="btn-edit">Editar</a> |
								<a href="deletar?id=${problema.id}" class="btn-delete"
								   onclick="return confirm('Tem certeza que deseja excluir este problema...')">Excluir</a>
							</td>
...
    </tr>
</c:forEach>
</tbody>
    </table>
</body>
</html>
