<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Editar Risco</title>
</head>
<body>
<h2>Editar Risco</h2>
<form action="atualizar" method="post">
    <input type="hidden" name="id" value="${risco.id}" /> 
    
    <p>Descrição: <input type="text" name="descricao" value="${risco.descricao}" required></p>
    
    <p>Origem: <input type="text" name="origem" value="${risco.origem}" required></p>
    
    <p>Data Identificação: <input type="date" name="dataIdentificacao" value="${risco.dataIdentificacao}" required></p>
    
    <p>Status:
        <select name="status">
            <option value="Identificado" ${risco.status == 'Identificado' ? 'selected' : ''}>Identificado</option>
            <option value="Avaliado" ${risco.status == 'Avaliado' ? 'selected' : ''}>Avaliado</option>
            <option value="Em Mitigação" ${risco.status == 'Em Mitigação' ? 'selected' : ''}>Em Mitigação</option>
            <option value="Encerrado" ${risco.status == 'Encerrado' ? 'selected' : ''}>Encerrado</option>
        </select>
    </p>
    <button type="submit">Salvar Alterações</button>
</form>
</body>
</html>