package cz.cvut.pin_b13;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;

import org.postgresql.util.PSQLException;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

/**
 * Servlet implementation class SaveToDB
 */
@WebServlet("/SaveToDB")
@MultipartConfig
public class SaveToDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public int bar = -1;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveToDB() {
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
		
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
      //  doGet(request, response);
		InputStream is=null;
		BufferedInputStream buf=null;
		FileOutputStream fos=null;
		Connection conn=null;
		try
		{
			Part part=request.getPart("jmeno");
			is=part.getInputStream();
			InputStreamReader isr=new InputStreamReader(is);
			BufferedReader reader=new BufferedReader(isr);
			String luxus=reader.readLine();
			reader = new BufferedReader( new InputStreamReader(request.getPart("lat").getInputStream()));
			double lat=Double.parseDouble(reader.readLine());
			reader = new BufferedReader( new InputStreamReader(request.getPart("lon").getInputStream()));
			double lon=Double.parseDouble(reader.readLine());
			buf = new BufferedInputStream(request.getPart("uploaded").getInputStream());
			SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmssSSS");
			//File file=new File("/home/martin/img/"+String.format(sdf.format(new Date()))+".jpg");
			File file=new File("/home/pin2_b13/kamery_img/"+String.format(sdf.format(new Date()))+".jpg");
			String imagePath=file.getAbsolutePath();
			ByteArrayBuffer baf=new ByteArrayBuffer();
			int current=0;
			 while ((current = buf.read()) != -1) 
             {
                      baf.write((byte) current);
             }

             fos = new FileOutputStream(file);
             fos.write(baf.toByteArray());
			if (ds != null) 
			{
				conn=ds.getConnection();
	    
				if(conn != null) 
				{
					Statement stmt = conn.createStatement();
					stmt.executeQuery("INSERT INTO b_kamery(jmeno,lat,lon,image) VALUES ('"+luxus+"',"+lat+","+lon+",'"+imagePath+"')");
					
				}
			}
		}
		catch(PSQLException esql)
		{
			
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		finally{
			if(fos!=null)fos.close();
			if(is!=null)is.close();
			if(buf!=null)buf.close();
			if(conn != null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}

		}
	}

}
