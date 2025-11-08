// Arquivo: serv/atualizar.java

package serv;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// ✅ Correção: Importa a classe Risco do seu pacote
import com.joao.gestaorisco.Risco; 

@WebServlet("/atualizar")
public class AtualizarRisco extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    request.setCharacterEncoding("UTF-8");
	    
	    try {
	        int id = Integer.parseInt(request.getParameter("id"));
	        String descricao = request.getParameter("descricao");
	        String origem = request.getParameter("origem");
	        String dataIdentificacao = request.getParameter("dataIdentificacao");
	        String status = request.getParameter("status");

	        // ✅ Correção: Chama o método estático na classe Risco
	        boolean sucesso = Risco.atualizar(id, descricao, origem, dataIdentificacao, status); 

	        if (sucesso) {
	            // ✅ Correção: Redireciona para o seu novo Servlet de listagem.
	            response.sendRedirect("listarRiscos"); 
	        } else {
	            response.getWriter().println("<h3>Erro: não foi possível atualizar o risco.</h3>");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.getWriter().println("<h3>Erro ao processar atualização: " + e.getMessage() + "</h3>");
	    }
	}
}