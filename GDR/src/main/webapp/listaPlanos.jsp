<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Planos de Mitigação para o Risco ${idRisco}</title>

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
    border-bottom: 3px solid #17a2b8; /* Cor do Plano de Mitigação */
    padding-bottom: 10px;
    width: 100%;
    max-width: 800px;
    text-align: center;
}

table {
    width: 80%;
    max-width: 800px;
    border-collapse: collapse;
    margin-top: 20px;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    background-color: #ffffff;
    border-radius: 8px;
    overflow: hidden;
}

th, td {
    padding: 12px 15px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}

th {
    background-color: #17a2b8;
    color: white;
    text-transform: uppercase;
    font-size: 0.9em;
}

tr:hover {
    background-color: #f5f5f5;
}

.btn {
    display: inline-block;
    padding: 8px 12px;
    margin: 3px;
    text-decoration: none;
    color: white;
    border-radius: 4px;
    transition: background-color 0.3s ease;
    font-size: 0.9em;
    text-align: center;
}

.btn-view {
    background-color: #3498db; /* Azul */
}

.btn-view:hover {
    background-color: #2980b9;
}

.btn-add {
    background-color: #2ecc71; /* Verde */
}

.btn-add:hover {
    background-color: #27ae60;
}

.no-data {
    text-align: center;
    color: #7f8c8d;
    padding: 20px;
}

.back-link {
    display: inline-block;
    margin-top: 20px;
    padding: 10px 15px;
    background-color: #95a5a6;
    color: white;
    text-decoration: none;
    border-radius: 4px;
    transition: background-color 0.3s ease;
}

.back-link:hover {
    background-color: #7f8c8d;
}
</style>
</head>

<body>
<h2>Planos de Mitigação para o Risco ID: <c:out value="${idRisco}"/></h2>

<c:choose>
    <c:when test="${not empty listaPlanos}">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Descrição do Plano</th>
                    <th>Status</th>
                    <th colspan="2">Ações</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="plano" items="${listaPlanos}">
                    <tr>
                        <td><c:out value="${plano.id}"/></td>
                        <td><c:out value="${plano.descricao}"/></td>
                        <td><c:out value="${plano.status}"/></td>
                        <td>
                            <a href="app?acao=listarAcoesPorPlano&planoId=${plano.id}" class="btn btn-view">
                                Ver Ações
                            </a>
                        </td>
                        <td>
                            <a href="app?acao=formAcao&planoId=${plano.id}" class="btn btn-add">
                                Registrar Ação
                            </a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p class="no-data">Nenhum plano de mitigação encontrado para este risco.</p>
    </c:otherwise>
</c:choose>

<a href="app?acao=listarRiscos" class="back-link">Voltar para a Lista de Riscos</a>

</body>
</html>
