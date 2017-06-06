package ClientHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.json.JSONException;

import Coordinator.Coordinator;
import Coordinator.Coordinator.CoordinatorInterrupts;

/**
 * Servlet implementation class RequestHandler
 */
@WebServlet("/request")

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class RequestHandler extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String SAVE_DIR = "uploadFiles";

	public RequestHandler() {
		super();
	}

	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}
	
	private String extractFields(Part part, String field) {
		String contentDisp = part.getHeader("content-disposition");		
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith(field)) {
				String[] details = s.split("%22");
				System.out.println(details[3]+"--"+details[7]+"--"+details[11]);
				try {					
					Coordinator.personDetails.append("name", details[3]);
					Coordinator.personDetails.append("upperBody", details[7]);
					Coordinator.personDetails.append("lowerBody", details[11]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return s.substring(s.indexOf("=") + 2, s.length() - 1);
			}
		}
		return "";
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) {

		try {
//			String name = (String) request.getAttribute("person-name");
//			String upperBodyColour = (String) request.getAttribute("upperBodyColour");
//			String lowerBodyColour = (String) request.getAttribute("lowerBodyColour");
//
//			System.out.println(name);

			// TODO Auto-generated method stub
			// response.getWriter().append("Served at:
			// ").append(request.getContextPath());

			// gets absolute path of the web application
			String appPath = request.getServletContext().getRealPath("");
			// constructs path of the directory to save uploaded file
			String savePath = appPath + File.separator + SAVE_DIR;

			// creates the save directory if it does not exists
			File fileSaveDir = new File(savePath);
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdir();
			} else {
				fileSaveDir.delete();
				fileSaveDir.mkdir();
			}

			for (Part part : request.getParts()) {
				String fileName = extractFileName(part);
				if (!fileName.equals("")) {
					// refines the fileName in case it is an absolute path
					fileName = new File(fileName).getName();
					part.write(savePath + File.separator + fileName);
					Coordinator.personDetails.append("image",savePath + File.separator + fileName);
				}
				extractFields(part, "name=\"{%22name%22");
				
			}
			while (Coordinator.interrupt != CoordinatorInterrupts.NO_EVENT);
			Coordinator.interrupt = CoordinatorInterrupts.CLIENT_ADD_A_REQUEST;
			Coordinator.Coordinator.interrupt();
			PrintWriter out = response.getWriter();
			out.println("OK");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
