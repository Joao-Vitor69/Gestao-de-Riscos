<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Configura a JSTL Core Library (prefixo "c") --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Avaliar Risco</title>
<style>
    /* Estilos CSS para formatação e aparência da página */
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
        /* Define a cor da linha de destaque abaixo do título (amarelo/laranja) */
        border-bottom: 3px solid #f39c12; 
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
    /* Efeito de foco com a cor de destaque (laranja/amarelo) */
    input[type="text"]:focus, 
    input[type="number"]:focus,
    textarea:focus {
        border-color: #f39c12; 
        outline: none;
        box-shadow: 0 0 5px rgba(243, 156, 18, 0.5);
    }
    /* Estilo base para todos os botões/links */
    .btn { 
        padding: 12px 20px; 
        color: black; 
        border: none; 
        border-radius: 6px; 
        cursor: pointer; 
        font-weight: bold;
        transition: background-color 0.3s;
        text-decoration: none;
        display: inline-block;
        text-align: center;
    }
    /* Estilo específico para o botão de submissão */
    .btn-submit {
        background-color: #ffc107; /* Amarelo/Laranja */
    }
    .btn-submit:hover { 
        background-color: #e0a800; 
    }
    /* Estilo para o botão de voltar */
    .back-link {
        margin-top: 20px;
        background-color: #95a5a6;
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
    <h2>Avaliar Risco</h2>
    
    <%-- JSTL: Verifica se a variável 'erro' (definida no Servlet) NÃO está vazia --%>
    <c:if test="${not empty erro}">
        <%-- Exibe a mensagem de erro formatada --%>
        <p class="error"><c:out value="${erro}"/></p>
    </c:if>
    
    <form action="app" method="post">
        <input type="hidden" name="acao" value="avaliarRisco">
        <input type="hidden" name="idRisco" value="${idRisco}">
        
        <label for="impacto">Impacto (1-5):</label>
        <input type="number" id="impacto" name="impacto" min="1" max="5" required>
        
        <label for="probabilidade">Probabilidade (1-5):</label>
        <input type="number" id="probabilidade" name="probabilidade" min="1" max="5" required>
        
        <label for="urgencia">Urgência (1-5):</label>
        <input type="number" id="urgencia" name="urgencia" min="1" max="5" required>

        <label for="responsavel">Responsável:</label>
        <input type="text" id="responsavel" name="responsavel" required>
        
        <label for="justificativa">Justificativa:</label>
        <textarea id="justificativa" name="justificativa" rows="4"></textarea>
        
        <button type="submit" class="btn btn-submit">Salvar Avaliação</button>
    </form>
    
    <a href="app" class="btn back-link">Voltar para a Lista de Riscos</a>
</body>
</html>