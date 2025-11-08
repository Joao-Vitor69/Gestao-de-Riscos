// Arquivo: serv/deletar.java

package serv;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// ✅ Correção: Importa a classe Risco do seu pacote
import com.joao.gestaorisco.Risco; 

@WebServlet("/deletar")
public class DeletarRisco extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	   try {
            int id = Integer.parseInt(request.getParameter("id"));
            
            // ✅ Correção: Chama o método estático na classe Risco
            Risco.excluir(id); 
            
            // ✅ Correção: Redireciona para o seu novo Servlet de listagem.
            response.sendRedirect("listarRiscos"); 
            
        } catch (Exception e) {
            request.setAttribute("erro", "Erro ao excluir: " + e.getMessage());
            // ✅ Correção: Encaminha em caso de erro para o novo URL de listagem.
            request.getRequestDispatcher("/listarRiscos").forward(request, response);
        }
    }
}