// Arquivo: serv/editar.java

package serv;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// ✅ Correção: Importa a classe Risco do seu pacote
import com.joao.gestaorisco.Risco; 

@WebServlet("/editar")
public class EditarRisco extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	    try {
	        int id = Integer.parseInt(request.getParameter("id"));
	        // 1. Busca um objeto Risco (não Problema)
	        Risco risco = Risco.buscarPorId(id); 

	        // 2. Define o atributo para o JSP usando o nome "risco"
	        request.setAttribute("risco", risco); 
	        
            // 3. Encaminha para o JSP com o nome correto
	        request.getRequestDispatcher("formEditarRisco.jsp").forward(request, response);
	        
	    } catch (Exception e) {
	        // Em caso de erro, apenas redireciona para a lista para não travar
            response.sendRedirect("listarRiscos"); 
	    }
	}
}