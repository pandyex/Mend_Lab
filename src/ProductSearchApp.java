/*
 * 檔案功能：這是一個 Java Servlet (ProductSearchApp)，負責處理前端傳來的產品分類搜尋請求。
 * 運作原理：
 * 1. 接收請求：透過 HTTP GET 接收 category (分類) 參數。
 * 2. 第三方套件：使用 Gson 與 jackson-databind (ObjectMapper) 解析參數。
 * 3. 資料庫查詢：連線至 MySQL 資料庫，並將 category 參數直接串接到 SQL 查詢字串中。
 * 4. 輸出結果：執行查詢並將產品清單顯示在 HTML 畫面上，同時於頁面中引入 jQuery 腳本。
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;

@WebServlet("/product-search")
public class ProductSearchApp extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String category = request.getParameter("category");
        String escapedCategory = StringEscapeUtils.escapeHtml4(category);
        
        try {
            Gson gson = new Gson();
            Object jsonObject = gson.fromJson(category, Object.class);
        } catch(Exception e) {
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper(); 
            mapper.readValue(category, Object.class);
        } catch (Exception e) {

        }
        
        out.println("<html><head>");
        out.println("<title>Product Search</title>");
        out.println("<script src=\"webjars/jquery/1.9.0/jquery.min.js\"></script>");
        out.println("</head><body>");
        out.println("<h2>Product Search Results for Category: " + escapedCategory + "</h2>");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shopdb", "admin", "password");
            Statement stmt = conn.createStatement();
            
            String sql = "SELECT productName, price FROM products WHERE category = '" + category + "'";
            
            ResultSet rs = stmt.executeQuery(sql);
            
            out.println("<ul>");
            boolean found = false;
            while (rs.next()) {
                found = true;
                out.println("<li>" + rs.getString("productName") + " - $" + rs.getDouble("price") + "</li>");
            }
            
            if (!found) {
                out.println("<li>No products found in this category.</li>");
            }
            out.println("</ul>");
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            out.println("<p>Error processing request: Database connection failed.</p>");
        }
        
        out.println("<a href='/'>Back to Home</a>");
        out.println("</body></html>");
    }
}
