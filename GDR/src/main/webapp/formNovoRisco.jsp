<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Configura a JSTL Core Library (prefixo "c") --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registrar Risco</title>
<style>
    /* Estilos CSS para formatação e layout da página */
    body { 
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
        margin: 0; 
        padding: 40px; 
        background-color: #f4f7fa; 
        color: #333;
        display: flex;
        flex-direction: column;
        align-items: center; /* Centraliza o conteúdo (título e formulário) horizontalmente */
    }
    h2 { 
        color: #2c3e50; 
        margin-bottom: 25px;
        border-bottom: 3px solid #28a745; /* Cor verde para destaque, associada ao registro/sucesso */
        padding-bottom: 10px;
        width: 100%;
        max-width: 600px;
        text-align: center;
    }
    /* Estilo principal do contêiner do formulário */
    form { 
        width: 100%;
        max-width: 600px; 
        margin-top: 20px; 
        padding: 30px; 
        background-color: #ffffff;
        border: 1px solid #e0e0e0; 
        border-radius: 12px; 
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
    label { 
        display: block; 
        margin-bottom: 5px; 
        font-weight: 600; 
        color: #2c3e50;
    }
    /* Estilo unificado para campos de entrada de texto, número e área de texto */
    input[type="text"], 
    input[type="number"], 
    textarea { 
        width: 100%; 
        padding: 10px; 
        margin-bottom: 20px; 
        border: 1px solid #bdc3c7; 
        border-radius: 6px; 
        box-sizing: border-box; 
        transition: border-color 0.3s;
        resize: vertical; /* Permite redimensionar verticalmente o textarea */
    }
    /* Efeito de foco com a cor de destaque (verde) */
    input[type="text"]:focus, 
    input[type="number"]:focus,
    textarea:focus {
        border-color: #28a745; /* Foco verde */
        outline: none;
        box-shadow: 0 0 5px rgba(40, 167, 69, 0.5);
    }
    /* Estilo base para todos os botões/links */
    .btn { 
        padding: 12px 20px; 
        color: white; 
        border: none; 
        border-radius: 6px; 
        cursor: pointer; 
        font-weight: bold;
        transition: background-color 0.3s;
        text-decoration: none;
        display: inline-block;
        text-align: center;
    }
    /* Estilo específico para o botão de submissão (Salvar Risco) */
    .btn-submit {
        background-color: #28a745; /* Cor verde */
    }
    .btn-submit:hover { 
        background-color: #1e7e34; 
    }
    /* Estilo para o link/botão de Voltar */
    .back-link {
        margin-top: 20px;
        background-color: #95a5a6;
        color: white; /* Cor do texto alterada para combinar com o fundo cinza */
    }
    .back-link:hover {
        background-color: #7f8c8d;
    }
    /* Estilo para exibição de mensagens de erro */
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
    <h2>Registrar Novo Risco</h2>
    
    <%-- JSTL: Verifica se a variável 'erro' (definida no Servlet) NÃO está vazia --%>
    <c:if test="${not empty erro}">
        <%-- Exibe a mensagem de erro formatada, usando <c:out> para evitar injeção de código --%>
        <p class="error"><c:out value="${erro}"/></p>
    </c:if>
    
    <form action="app" method="post">
        <input type="hidden" name="acao" value="inserirRisco">
        
        <label for="descricao">Descrição:</label>
        <textarea id="descricao" name="descricao" rows="4" required></textarea>
        
        <label for="origem">Origem:</label>
        <input type="text" id="origem" name="origem">
        
        <label for="tipoRiscoId">Tipo do Risco (ID):</label>
        <input type="number" id="tipoRiscoId" name="tipoRiscoId" required>
        
        <button type="submit" class="btn btn-submit">Salvar Risco</button>
    </form>
    
    <a href="app" class="btn back-link">Voltar para a Lista de Riscos</a>
</body>
</html>