import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class createdb 
{
    public static void main(String args[])
    {
        try 
        {
            String name = "Auryah";
            String pass = "Aoro07";
            int roll = 210;
            Connection con = DriverManager.getConnection("jdbc:derby:users;create=true");
            System.out.println("dbCreated");
            //Statement st = con.createStatement();
            //st.executeUpdate("Create table users(name varchar(30),rollno int,pass varchar(30))");
            //System.out.println("tableCreated");
            PreparedStatement ps = con.prepareStatement("insert into users values(?,?,?)");
            ps.setString(1,name);
            ps.setInt(2,roll);
            ps.setString(3,pass);
            ps.executeUpdate();
            System.out.println("Inserted into table");String sql="SELECT * FROM users";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                
                String uname = rs.getString("name");
                String upass = rs.getString("pass");
                System.out.println(uname);
                System.out.println(upass);
                if(uname == name)
                {
                    if(upass == pass)
                    {
                        System.out.println("passesss");
                        
                    }
                }
            }

            
            
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }  
}
