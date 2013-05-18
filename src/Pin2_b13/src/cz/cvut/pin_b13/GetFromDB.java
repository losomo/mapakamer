package cz.cvut.pin_b13;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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
		ServletOutputStream os=null;
		try
		{
			String path=request.getParameter("path");
			if(path!=null)
			{
			 	int bytesRead = 0;  
		        byte[] buff = new byte[1024*10];  
		   
		        os = response.getOutputStream();  
		   
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
			else{
	
			
			if (ds != null) 
			{
				Connection conn=ds.getConnection();
	    
				if(conn != null)
				{
					os=response.getOutputStream();
					response.setContentType("text/plain; charset=UTF-8");
					response.setCharacterEncoding("UTF-8");
					Statement stmt = conn.createStatement();
					ResultSet rst = stmt.executeQuery("SELECT osm_id,lat,lon,image FROM b_kamery limit 50");
					
					while(rst.next()!=false) {
						for(int i=1;i<=4;i++)
						{os.print(rst.getString(i) + "::");}
						os.print(";");
					}
					conn.close();
				}
			}}}
		
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		finally{
			os.close();
		}
	}}
	


