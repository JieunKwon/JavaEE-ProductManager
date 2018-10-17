package com.product;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * --------------------------------------------- 
 * @author JIEUN KWON (991447941)
 *	
 * TASK : Assignment 2 
 * Product Information Form & Servlet 
 * 
 * created Date : Oct 10, 2018 
 * modified Date : Oct 12, 2018
 * --------------------------------------------- 
 *
 * Page : AddProduct.java (servlet)
 * Task	: DB connection
 * 		: insert new product into table 
 * 		: update product for price and quantity
 * 		: select product for viewing  
 *
 * Reference : product table structure
 *		    productid	varchar(10) NOT NULL Primary Key,
 *		    productname varchar(30) NOT NULL,
 *		    quantity	int NOT NULL,
 *		    price		decimal(7,2) NOT NULL,
 *		    category	varchar(20)
 *
 */ 

/**
 * Servlet implementation class AddProduct
 */
@WebServlet(name = "ProductInformation", 
description = "This is our servlet description", 
urlPatterns = {"/AddProduct", "/ProductInformation"}, 
asyncSupported = false
)

public class AddProduct extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	Connection con;
	PreparedStatement pstView;
	PreparedStatement pstUpdate;
	ResultSet rs;
	// for price value as currency format
	NumberFormat formatter = NumberFormat.getCurrencyInstance();
			 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddProduct() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// variables
		 response.setContentType("text/html");
		 PrintWriter out=response.getWriter();
		 String connectionUrl = "jdbc:mysql://localhost:3306/productinventory";
		 String connectionUser = "root";
		 String connectionPassword = "mydb1234";
		 String viewQuery="SELECT * from product where productid=?";
		 
		 // for html
         String htmlRespone = "";	
         // for print result message
         String rstTitle = "";
         String rstMsg = ""; 
        
		 out.println("<html><head>"); 
		 out.println("<link rel='stylesheet' type='text/css' href='" + request.getContextPath() +  "/style.css' />");
		 out.println("</head><body>");
		 
		 try{
			 	// init variables and get parameters
			 	String mode = request.getParameter("mode");
				String pcode = request.getParameter("pcode");
				String pname = "";
		        double price = 0; 
		        int qty = 0;
		        String category = "";
		        
		         
		        // DB connection
		        Class.forName("com.mysql.jdbc.Driver").newInstance();     
				con = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
				
				// ---------------------------------
				// 	Add New Product (mode is add)
				// ---------------------------------
		        if(mode.equals("add")) {
		        	
		        	rstTitle = "Add New Product";
		        	// get params from form 
					 pname = request.getParameter("pname");
			         price = Double.parseDouble(request.getParameter("price")); 
			         qty = Integer.parseInt(request.getParameter("qty"));
			         category = request.getParameter("category");
			      
		        	// insert query
		        	String insertQuery = "insert into product "
	 					+ " (productid,productname,quantity,price,category) values (?,?,?,?,?)";
		        
		         
			 		pstUpdate=con.prepareStatement(insertQuery);
			 		
			 		// set
			 		pstUpdate.setString(1,pcode);
			 		pstUpdate.setString(2,pname);
			 		pstUpdate.setInt(3,qty);		 		 
			 		pstUpdate.setDouble(4,price);
			 		pstUpdate.setString(5,category);
			 		
			 		int chk = pstUpdate.executeUpdate();
			 		
			 	// print result
			         if(chk == 0) {
			        	 rstMsg = "It was failed to add Product ID<br>";
			         }else {				         
				         rstMsg = "Product is successfully added<br>";
			         }
			         
			 		rstMsg = "Product is successfully added";
		        
			 	// ---------------------------------
				// 	Edit Product (mode is edit)
				// ---------------------------------
		        }else if(mode.equals("edit")) {
			 	
		        	rstTitle = "Edit Product";
		        	
		        	String updateQuery="Update product set price=?, quantity=? where productid=?";
		        	
		        	// get params from form					
					 price = Double.parseDouble(request.getParameter("price")); 
			         qty = Integer.parseInt(request.getParameter("qty"));
			         
		        	 pstUpdate = con.prepareStatement(updateQuery);
					 pstUpdate.setDouble(1,price);
					 pstUpdate.setInt(2, qty);
					 pstUpdate.setString(3, pcode);
			         int chk = pstUpdate.executeUpdate();
			          
			         // print result
			         if(chk == 0) {
			        	 rstMsg = "Product update failed<br>";
			         }else {				         
				         rstMsg = "Product is successfully updated<br>";
			         }
			         
			    // ---------------------------------
				// 	View Product (mode is view)
				// ---------------------------------
		        }else if(mode.equals("view")) {
		        	rstTitle = "View Product";
		        }
		        
		        
		        // ---------------------------------
				// 	Product Information
				// ---------------------------------
		         pstView = con.prepareStatement(viewQuery);
				 pstView.setString(1,pcode);
		         rs = pstView.executeQuery();
		          
		         // move to last row to count rows	          
		         rs.last();		       
		         
		         // no result
		         if(rs.getRow() == 0) {
		        	 
		        	// result msg 
		        	rstMsg += "Product ID entered is invalid"; 
		        	
		         }else {
		        	 
		        	 // move to first row
		        	 rs.beforeFirst();	
		        	 
		        	 // get result
			         while(rs.next())
			  		 {
			        	  
			        	// get information
			  		    pcode=rs.getString("productid");
			  		    pname=rs.getString("productname");
			  		    category=rs.getString("category");
			  		    price=rs.getDouble("price");
			  		    qty = rs.getInt("quantity");
			  		     
			  		    // make html for printing information				        
				  		htmlRespone += "<table class='listT' border='0'><tr><td width='130px' align='right'>Product ID:";
				  		// -- product id
				  		htmlRespone += "</td><td width='200px' class='prod_info'>"+ pcode ;
				  		htmlRespone += "</td></tr>";
				  		htmlRespone += "<tr><td width='130px' align='right'>Product Name:</td>";				  		
				  		// -- product name
				  		htmlRespone += "<td class='prod_info'>" + pname + "</td></tr>" +
				  				"<tr><td width='130px' align='right'>Price:</td>";
				  		// -- price
				  		htmlRespone += "<td class='prod_info'>" + formatter.format(price) + "</td></tr>" +
				  				"<tr><td width='130px' align='right'>Quantity:</td>";
				  		// -- quantity
				  		htmlRespone += "<td class='prod_info'>" + qty + "</td></tr>";
				  		// -- category
				  		htmlRespone += "<tr><td width='130px' align='right'>Category:</td>" +
				  					"<td class='prod_info'>" + category + "</td></tr></table>";
				  		  
			  		 }
		         
		         }
		           
		 }
		catch(SQLException e)
	    {
		  // e.printStackTrace();
		   rstMsg = "Request has failed to add";
	    }
	    catch(ClassCastException e)
	    {
		   //e.printStackTrace();  
		   rstMsg = "Request has failed";
	    }
	    catch(Exception e)
	    {
		  // e.printStackTrace(); 
		   rstMsg = "Request has failed";
	    }
		finally{
			
			// close con & pstView & pstUpate & rs
			 if(con!=null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	         if(pstView!=null)
				try {
					pstView.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	         if(pstUpdate!=null)
					try {
						pstUpdate.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
	         if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}    	
		}
		 
		 // Ending HTML with title and button
         htmlRespone = "<div align='center' class='div_title'><h2>" + rstTitle + "</h2></div>" +
        		 		"<div align='center' class='div_title'><h4>" + rstMsg + "</h4></div>" + 
        		 		htmlRespone;
         htmlRespone += "<div align='center'><input type='button' value='Go to Main' class='bt_general' onclick=\"javascript:location.href='NewProduct.html';\"></div>";	
         htmlRespone += "</body></html>";
         
         //print HTML
         try {
        	 out.println(htmlRespone); 
 		 } finally {
 			out.close();
 		 }
         
          
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
