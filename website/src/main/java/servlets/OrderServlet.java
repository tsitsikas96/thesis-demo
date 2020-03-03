package servlets;

import servers.JettyServer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {

    private static final long serialVersionUID = -6154475799000019575L;
    private Statement stmt = null;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String headcushion = request.getParameter("headcushion");
        String quantity = request.getParameter("quantity");
        insertOrder(headcushion, Integer.parseInt(quantity));
        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("/");
    }

    private void insertOrder(String head_cushion, int quantity){
        boolean head = false;
        if(head_cushion.matches("yes")){head = true;}
        try{
            stmt = JettyServer.conn.createStatement();
            String sql = "INSERT INTO orders( head_cushion, quantity) " +
                    "VALUES (" + head +", "+quantity+")";
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}