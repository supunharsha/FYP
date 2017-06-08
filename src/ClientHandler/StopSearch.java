package ClientHandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import Coordinator.Coordinator;
import Coordinator.Coordinator.CoordinatorInterrupts;

/**
 * Servlet implementation class StopSearch
 */
@WebServlet("/stopsearch")
public class StopSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StopSearch() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Coordinator.personDetails = new JSONObject();
		while (Coordinator.interrupt != CoordinatorInterrupts.NO_EVENT);
		Coordinator.interrupt = CoordinatorInterrupts.CLIENT_ADD_A_REQUEST;
		Coordinator.Coordinator.interrupt();
		PrintWriter out = response.getWriter();
		out.println("OK");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
