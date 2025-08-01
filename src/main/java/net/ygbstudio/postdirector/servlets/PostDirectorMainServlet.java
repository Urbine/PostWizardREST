package net.ygbstudio.postdirector.servlets;

// Jakarta imports
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.ejb.EJB;

// Java imports
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

// Local imports
import net.ygbstudio.postdirector.dao.PostReaderDAO;
import net.ygbstudio.postdirector.models.WPMeta;

@WebServlet("/allposts")
public class PostDirectorMainServlet extends HttpServlet{
	
	@EJB
	private PostReaderDAO postReader;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Collection<WPMeta> allPostItems = postReader.getAll();
		PrintWriter out = resp.getWriter();
		
		allPostItems.forEach(out::println);
		allPostItems.forEach(System.out::println);

		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'doPost'");
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'doPut'");
	}
	
	

}
