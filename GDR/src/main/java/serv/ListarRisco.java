package serv;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.joao.gestaorisco.Risco; // Importa a nova classe Risco

@WebServlet("/listarRiscos") // Mapeamento do URL
public class ListarRisco extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 1. Chama o Model
        List<Risco> listaRiscos = Risco.listar();
        
        // 2. Define o atributo para o JSP
        request.setAttribute("listaRiscos", listaRiscos);
        
        // 3. Encaminha para o JSP (View)
        request.getRequestDispatcher("/listarRiscos.jsp").forward(request, response);
    }
}