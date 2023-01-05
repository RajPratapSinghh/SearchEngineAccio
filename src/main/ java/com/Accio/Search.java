package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/Search")
public class Search extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        String keyword=request.getParameter("keyword");
        System.out.println(keyword);
        try{
            Connection connection=DatabaseConnection.getConnection();
            PreparedStatement preparedStatement= connection.prepareStatement("Insert into history values(?, ?)");
            preparedStatement.setString(1,keyword);
            preparedStatement.setString(2, "http://localhost:8080/SearchEngine/Search?keyword="+keyword);
            preparedStatement.executeUpdate();
            ResultSet resultSet=connection.createStatement().executeQuery("select pagetitle, pagelink, (length(lower(pagedata))-length(lower(replace(pagedata,'"+keyword+"',\"\"))))/length('"+keyword+"') as countoccurance from pages order by countoccurance desc;");
            ArrayList<SearchResult> results=new ArrayList<SearchResult>();
            while (resultSet.next()){
                SearchResult searchResult=new SearchResult();
                searchResult.setPageTitle(resultSet.getString("pageTitle"));
                searchResult.setPageLink(resultSet.getString("pageLink"));
                results.add(searchResult);
                /* System.out.println(resultSet.getString("pagetitle"));
                System.out.println(" "+resultSet.getString("pagelink")+"\n");*/
            }
            for(SearchResult result: results){
                System.out.println(result.getPageLink()+" "+ result.getPageTitle()+"\n");
            }
            request.setAttribute("results", results);
            request.getRequestDispatcher("/Search.jsp").forward(request,response);
            response.setContentType("text/html");
                        PrintWriter out= response.getWriter();
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
        catch (ServletException servletException){
            servletException.printStackTrace();
        }
        catch (IOException ioException)
        {ioException.printStackTrace();}
    }

}
