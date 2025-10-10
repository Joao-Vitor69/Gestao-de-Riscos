<%@page import="com.joao.gestaorisco.Risco"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Gestão de Riscos</title>
<style>
    body { 
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
        margin: 0; 
        padding: 40px; 
        background-color: #f4f7fa; 
        color: #333;
        display: flex;
        flex-direction: column;
        align-items: center;
    }
    h2 { 
        color: #2c3e50; 
        margin-bottom: 25px;
        border-bottom: 3px solid #34495e; /* Cor neutra/escura para a página principal */
        padding-bottom: 10px;
        width: 100%;
        max-width: 900px;
        text-align: center;
    }
    h3 {
        color: #555;
        margin-top: 30px;
        margin-bottom: 15px;
        width: 100%;
        max-width: 900px;
    }
    /* Estilo para Botões Gerais */
    .btn { 
        display: inline-block; 
        padding: 10px 15px; 
        margin-right: 8px; 
        text-decoration: none; 
        border: none; 
        border-radius: 6px; 
        cursor: pointer; 
        font-weight: 600;
        transition: background-color 0.3s, box-shadow 0.3s;
        text-align: center;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        white-space: nowrap; /* Impede quebras de linha em botões pequenos */
    }
    /* Botão Principal (Registrar Novo Risco) */
    .btn-primary { 
        background-color: #007bff; 
        color: white; 
    }
    .btn-primary:hover { 
        background-color: #0056b3;
        box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3); 
    }
    /* Botão Secundário (Registrar Ação) */
    .btn-secondary {
        background-color: #6c757d;
        color: white;
    }
    .btn-secondary:hover {
        background-color: #5a6268;
        box-shadow: 0 4px 8px rgba(108, 117, 125, 0.3);
    }
    /* Botão Avaliar (Amarelo) */
    .btn-evaluate {
        background-color: #ffc107;
        color: #333;
        font-size: 0.9em;
        padding: 6px 10px;
    }
    .btn-evaluate:hover {
        background-color: #e0a800;
    }
    /* Botão Criar Plano (Ciano/Azul) */
    .btn-plan {
        background-color: #17a2b8;
        color: white;
        font-size: 0.9em;
        padding: 6px 10px;
    }
    .btn-plan:hover {
        background-color: #138496;
    }

    /* Estilo da Tabela */
    table { 
        width: 100%; 
        max-width: 900px;
        border-collapse: separate; /* Permite border-radius nas células */
        border-spacing: 0;
        margin-top: 20px; 
        background-color: white;
        border-radius: 8px;
        overflow: hidden; /* Garante que o border-radius funcione */
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    }
    th, td { 
        padding: 15px; 
        text-align: left; 
        border-bottom: 1px solid #eee;
    }
    th { 
        background-color: #ecf0f1; 
        color: #2c3e50; 
        font-weight: 700;
        text-transform: uppercase;
        font-size: 0.9em;
    }
    tr:last-child td {
        border-bottom: none;
    }
    tr:hover {
        background-color: #f9f9f9;
    }
    td:last-child {
        white-space: nowrap; /* Impede quebras de linha nas colunas de ações */
    }
</style>
</head>
<body>
    <h2>Sistema de Gestão de Riscos</h2>
    
    <div style="width: 100%; max-width: 900px; margin-bottom: 20px;">
        <a href="app?acao=formRisco" class="btn btn-primary">Registrar Novo Risco</a>
        <a href="app?acao=formAcao" class="btn btn-secondary">Registrar Ação de Mitigação</a>
    </div>
    
    <h3>Riscos Registrados</h3>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Descrição</th>
                <th>Status</th>
                <th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <% 
                // Acessa a lista de riscos do objeto 'request'
                List<Risco> listaRiscos = (List<Risco>) request.getAttribute("listaRiscos");
                // Verifica se a lista não é nula antes de iterar
                if (listaRiscos != null) {
                    // Itera sobre cada objeto Risco na lista
                    for (Risco risco : listaRiscos) {
            %>
                <tr>
                    <td><%= risco.getId() %></td>
                    <td><%= risco.getDescricao() %></td>
                    <td><%= risco.getStatus() %></td>
                    <td>
                        <a href="app?acao=formAvaliacao&idRisco=<%= risco.getId() %>" class="btn btn-evaluate">Avaliar</a>
                        <a href="app?acao=formPlano&idRisco=<%= risco.getId() %>" class="btn btn-plan">Criar Plano</a>
                    </td>
                </tr>
            <% 
                    }
                } else {
            %>
                <tr>
                    <td colspan="4" style="text-align: center; color: #7f8c8d;">Nenhum risco registrado.</td>
                </tr>
            <% 
                }
            %>
        </tbody>
    </table>
</body>
</html>
