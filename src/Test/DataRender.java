package Test;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DataRender
 */
@WebServlet("/datarender")
public class DataRender extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataRender() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//    	if(!WebSocketServer.msg.isEmpty()){
//    		
//    		JSONObject jsonObject = new JSONObject(); 
//    		try {
//				jsonObject.append("msg",WebSocketServer.msg);
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    		
//    		resp.setContentType("application/json");
//    		// Get the printwriter object from response to write the required json object to the output stream      
//    		PrintWriter out = resp.getWriter();
//    		// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
//    		out.print(jsonObject);
//    		WebSocketServer.msg="";
//    		out.flush();
//    	}else{
//    		
//    	}
    	
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// TODO Auto-generated method stub
    	doPost(req, resp);
    }

}
