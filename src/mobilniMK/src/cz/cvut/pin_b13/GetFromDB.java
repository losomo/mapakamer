package cz.cvut.pin_b13;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class GetFromDB
 */
@WebServlet("/GetFromDB")
public class GetFromDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFromDB() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    private DataSource ds=null;
    public void init(ServletConfig config) throws ServletException
    {
    	super.init(config);
    	try{
    	ds=(DataSource) new InitialContext().lookup("java:comp/env/jdbc/db");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try
		{
			String path=request.getParameter("path");
			String northS=(request.getParameter("north"));
			if(path!=null)
			{
				ServletOutputStream os=response.getOutputStream();

			 	int bytesRead = 0;  
		        byte[] buff = new byte[1024*10];  
		   
 
		   
		        response.setContentType("image/jpg");  
		        BufferedInputStream bis = null;  
		   
		        try  
		        {  
		            bis = new BufferedInputStream(new FileInputStream(path));  
		   
		            while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))  
		            { 
		                os.write(buff, 0, bytesRead);  
		                os.flush();  
		            }  
		        }  
		        finally  
		        {  
		            if (bis != null)  
		                bis.close();  
		            if (os != null)  
		                os.close();
		        }
			}
			else if(northS!=null){
			double north=Double.parseDouble(northS)/1000000;
			double south=Double.parseDouble(request.getParameter("south"))/1000000;
			double east=Double.parseDouble(request.getParameter("east"))/1000000;
			double west=Double.parseDouble(request.getParameter("west"))/1000000;

			
			
			if (ds != null) 
			{
				Connection conn=ds.getConnection();
	    
				if(conn != null)
				{
					
					response.setContentType("text/plain; charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
					Statement stmt = conn.createStatement();
					String query="SELECT name,lat,lon,image FROM b_kamery WHERE ((lon BETWEEN "+ south+" AND "+north+") AND (lat BETWEEN "+west+" AND "+east+"))";
					ResultSet rst = stmt.executeQuery(query);
					PrintWriter pw=response.getWriter();
					while(rst.next()!=false) {
						for(int i=1;i<rst.getMetaData().getColumnCount();i++)
						{pw.print(rst.getString(i) + "::");}
						pw.print(rst.getString(rst.getMetaData().getColumnCount())+";");
					}
					pw.close();
					conn.close();
				}
			}}}
		
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		finally{
			//os.close();
			
		}
	}}
	


