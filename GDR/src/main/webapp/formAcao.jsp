<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Registrar Ação de Mitigação</title>
<style>
    /* Estilos CSS para formatação da página */
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
        border-bottom: 3px solid #3498db;
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
    /* Estilos para rótulos e campos de input */
    label { 
        display: block; 
        margin-bottom: 5px; 
        font-weight: 600; 
        color: #2c3e50;
    }
    input[type="text"], input[type="number"] { 
        width: 100%; 
        padding: 10px; 
        margin-bottom: 20px; 
        border: 1px solid #bdc3c7; 
        border-radius: 6px; 
        box-sizing: border-box; 
        transition: border-color 0.3s;
    }
    /* Efeito de foco nos campos */
    input[type="text"]:focus, input[type="number"]:focus {
        border-color: #3498db;
        outline: none;
        box-shadow: 0 0 5px rgba(52, 152, 219, 0.5);
    }
    /* Estilo para o botão principal (Salvar) */
    .btn { 
        padding: 12px 20px; 
        background-color: #3498db; 
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
    .btn:hover { 
        background-color: #2980b9; 
    }
    /* Estilo para o botão/link de Voltar */
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
    <h2>Registrar Ação de Mitigação</h2>
    
    <% 
       // Scriptlet JSP para checar se há uma mensagem de erro na requisição.
       String erro = (String) request.getAttribute("erro");
       if (erro != null) { 
    %>
        <p class="error">Erro: <%= erro %></p>
    <% 
       } 
    %>

    <form action="app" method="post">
        <input type="hidden" name="acao" value="inserirAcao">
        
       <% 
          // Scriptlet JSP: Tenta obter o ID do Plano de Mitigação da requisição.
          Integer planoId = (Integer) request.getAttribute("planoId"); 
       %>
    <% 
       // Bloco condicional para garantir que o planoId foi passado corretamente.
       if (planoId != null) { 
    %>
        <input type="hidden" name="planoId" value="<%= planoId %>">
        
        <label>Registrando Ação para o Plano ID:</label>
        <input type="text" value="<%= planoId %>" readonly style="background-color:#ecf0f1; font-weight:bold; margin-bottom: 30px;">
    <% 
       } else { 
    %>
        <p class="error">Erro: ID do Plano não encontrado na requisição.</p>
        <% } %>

        <label for="descricao">Descrição da Ação:</label>
        <input type="text" id="descricao" name="descricao" required>

        <label for="responsavel">Responsável:</label>
        <input type="text" id="responsavel" name="responsavel" required>

        <label for="prazoConclusao">Prazo de Conclusão (YYYY-MM-DD):</label>
        <input type="text" id="prazoConclusao" name="prazoConclusao" placeholder="Ex: 2025-12-31" required>

        <button type="submit" class="btn">Salvar Ação</button>
    </form>
    
    <a href="app?acao=formAcao&planoId=${plano.id}">Registrar Ação de Mitigação</a>
</body>
</html>