package ApiRest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joao.gestaorisco.Risco;

import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;

// Mapeamento para a URL da API. O '/*' permite capturar o ID (ex: /api/riscos/5)
@WebServlet("/api/riscos/*") 
public class RiscoAPIServlet extends HttpServlet {

    private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

    // ***************************************************************
    // 1. GET /api/riscos (Listar Todos) E GET /api/riscos/{id} (Buscar por ID)
    // ***************************************************************
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(JSON_CONTENT_TYPE);
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Caso 1: GET /api/riscos - Listar todos
                List<Risco> riscos = Risco.listar();
                String jsonResponse = serializeRiscosToJson(riscos);
                response.getWriter().write(jsonResponse);

            } else {
                // Caso 2: GET /api/riscos/{id} - Buscar por ID
                String idStr = pathInfo.substring(1); 
                int riscoId = Integer.parseInt(idStr);

                Risco risco = Risco.buscarPorId(riscoId);

                if (risco != null) {
                    response.getWriter().write(serializeRiscoToJson(risco));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    response.getWriter().write("{\"erro\": \"Risco com ID " + riscoId + " não encontrado.\"}");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("{\"erro\": \"ID do Risco inválido.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"erro\": \"Erro interno ao buscar riscos: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    // ***************************************************************
    // 2. POST /api/riscos - Criar um novo risco
    // ***************************************************************
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(JSON_CONTENT_TYPE);

        try {
            String jsonBody = readJsonBody(request);
            Risco novoRisco = deserializeRiscoFromJson(jsonBody);
            
            novoRisco.salvar(); // Salva e popula o ID no objeto

            response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
            response.getWriter().write(serializeRiscoToJson(novoRisco));

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("{\"erro\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"erro\": \"Erro ao processar a requisição POST: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    // ***************************************************************
    // 3. PUT /api/riscos/{id} - Atualizar um risco existente
    // ***************************************************************
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType(JSON_CONTENT_TYPE);
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            response.getWriter().write("{\"erro\": \"ID do Risco não fornecido na URL para atualização.\"}");
            return;
        }

        try {
            // 1. Extrai o ID da URL
            String idStr = pathInfo.substring(1); 
            int riscoId = Integer.parseInt(idStr);
            
            // 2. Deserializa o JSON do corpo
            String jsonBody = readJsonBody(request);
            Risco riscoAtualizado = deserializeRiscoFromJson(jsonBody);

            // 3. Garante que o ID do objeto é o ID da URL
            riscoAtualizado.setId(riscoId); 
            
            // 4. Salva (UPDATE) no banco
            riscoAtualizado.atualizar(); 

            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            response.getWriter().write(serializeRiscoToJson(riscoAtualizado));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"erro\": \"ID do Risco ou campo numérico inválido.\"}");
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 ou 404 (do IllegalStateException)
            response.getWriter().write("{\"erro\": \"Falha na atualização: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"erro\": \"Erro ao processar a requisição PUT: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }


    // ***************************************************************
    // 4. DELETE /api/riscos/{id} - Remover um risco
    // ***************************************************************
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType(JSON_CONTENT_TYPE);
        String pathInfo = request.getPathInfo(); 
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"erro\": \"ID do Risco não fornecido na URL.\"}");
            return;
        }
        
        String idStr = pathInfo.substring(1); 
        int riscoId;

        try {
            riscoId = Integer.parseInt(idStr);
            
            boolean sucesso = Risco.excluir(riscoId);

            if (sucesso) {
                // Padrão REST: 204 No Content para exclusão bem-sucedida
                response.setStatus(HttpServletResponse.SC_NO_CONTENT); 
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                response.getWriter().write("{\"erro\": \"Risco com ID " + riscoId + " não encontrado para exclusão.\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("{\"erro\": \"ID do Risco inválido (não numérico).\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"erro\": \"Erro ao processar a exclusão do risco: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
    
    // -----------------------------------------------------------------
    // MÉTODOS AUXILIARES (Simulação de Gson/Jackson para JSON)
    // NOTA: Em produção, use uma biblioteca como Jackson ou Gson.
    // -----------------------------------------------------------------
    
    private String readJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        if (sb.length() == 0) {
            throw new IllegalArgumentException("Corpo da requisição JSON não pode ser vazio.");
        }
        return sb.toString();
    }

    private Risco deserializeRiscoFromJson(String jsonBody) {
        String descricao = extractValue(jsonBody, "descricao");
        String origem = extractValue(jsonBody, "origem");
        String dataIdentificacao = extractValue(jsonBody, "dataIdentificacao");
        String tipoRiscoIdStr = extractValue(jsonBody, "tipoRiscoId");
        String status = extractValue(jsonBody, "status"); // Opcional para POST, essencial para PUT
        
        if (descricao == null || origem == null || dataIdentificacao == null || tipoRiscoIdStr == null) {
            throw new IllegalArgumentException("Campos obrigatórios ausentes: descricao, origem, dataIdentificacao, tipoRiscoId.");
        }
        
        int tipoRiscoId;
        try {
            tipoRiscoId = Integer.parseInt(tipoRiscoIdStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tipoRiscoId deve ser um número inteiro válido.");
        }
        
        // Usa o construtor de novo risco
        Risco risco = new Risco(descricao, origem, dataIdentificacao, tipoRiscoId);
        // Permite que o status seja alterado se for um PUT
        if (status != null) {
            risco.setStatus(status);
        }
        return risco;
    }
    
    private String serializeRiscosToJson(List<Risco> riscos) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < riscos.size(); i++) {
            sb.append(serializeRiscoToJson(riscos.get(i)));
            if (i < riscos.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    private String serializeRiscoToJson(Risco risco) {
        return String.format(
            "{\"id\":%d, \"descricao\":\"%s\", \"origem\":\"%s\", \"dataIdentificacao\":\"%s\", \"status\":\"%s\", \"tipoRiscoId\":%d}",
            risco.getId(),
            escapeJson(risco.getDescricao()),
            escapeJson(risco.getOrigem()),
            risco.getDataIdentificacao(),
            risco.getStatus(),
            risco.getTipoRiscoId()
        );
    }
    
    private String escapeJson(String value) {
        // Trata aspas duplas e novas linhas para evitar quebra no JSON
        return value.replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String extractValue(String json, String key) {
        // Simulação básica de extração de valor por chave
        String search = "\"" + key + "\":";
        int start = json.indexOf(search);
        if (start == -1) return null;
        
        start += search.length();
        
        // Trata string (valor entre aspas)
        if (json.charAt(start) == '"') {
            start++; 
            int end = json.indexOf('"', start);
            if (end == -1) return null;
            return json.substring(start, end);
        } else {
            // Trata número (sem aspas, buscando pela próxima vírgula ou chave de fechamento)
            int end = json.indexOf(',', start);
            int endBrace = json.indexOf('}', start);
            
            if (end == -1) {
                end = endBrace;
            } else if (endBrace != -1 && endBrace < end) {
                end = endBrace;
            }
            
            if (end == -1) return null;
            return json.substring(start, end).trim();
        }
    }
}