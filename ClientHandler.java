import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ClientHandler 
{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferReader;
    private BufferedWriter bufferWriter;
    private String clientUsername;
    private String clientPassword;
    private String newuser = "dlshskjhdkhskfiuwfwie6f7wyeffw8fw7fyw8yfe";
    private String broadcaststring = "kljfldkfgge6r78g68g76er7ggeg87erwe67sdvx687bg7r7jy/8";
    private String authorisechat = "jdshfkdjfhskuhfkdjfh564dfg65s4fb5d4bd6b@873gbdjkhkjf";
    boolean login = false;

    public ClientHandler(Socket socket)
    {
        try
        {
            while(!login)
            {
                createtable();
                this.socket = socket;
                this.bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.clientUsername = bufferReader.readLine();
                this.clientPassword = bufferReader.readLine();

                if(clientUsername.equals(newuser))
                {
                    String nm = clientPassword;
                    String pas = bufferReader.readLine();
                    String rolno = bufferReader.readLine();
                    adduser(nm, pas,rolno);
                    this.clientUsername = nm;
                    this.clientPassword = pas;
                    
                }

                if(checkuser(clientUsername, clientPassword)=="True")
                {              
                    this.bufferWriter.write(authorisechat);
                    this.bufferWriter.newLine();
                    this.bufferWriter.flush();
                    clientHandlers.add(this);
                    broadcastMessage(" has entered the chat");
                    login = true;
                    getinput();
                }
                else
                {
                    this.bufferWriter.write("You are not authorised to chat");
                    this.bufferWriter.newLine();
                    this.bufferWriter.flush();
                    //closeEverything(socket, bufferReader, bufferWriter);
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            closeEverything(socket,bufferReader,bufferWriter);
            
        }
    }

    public void getinput()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                String messageFromClient;
                while(socket.isConnected())
                {
                    try
                    {
                        String header = bufferReader.readLine();
                        if(header.equals(broadcaststring))
                        {
                            messageFromClient = bufferReader.readLine();                    
                            broadcastMessage(messageFromClient);
                        }
                            
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                        closeEverything(socket,bufferReader,bufferWriter);
                        break;
                    }

                }
            }
        }).start();
    }

    public void broadcastMessage(String messageToSend)
    {
        for(ClientHandler clientHandler : clientHandlers)
        {
            try
            {
                if(!clientHandler.clientUsername.equals(clientUsername))
                {
                    clientHandler.bufferWriter.write(this.clientUsername+": "+messageToSend);
                    clientHandler.bufferWriter.newLine();
                    clientHandler.bufferWriter.flush();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
                closeEverything(socket,bufferReader,bufferWriter);
            }    
        }
    }
    

    public void removeClientHandler()
    {
        clientHandlers.remove(this);
        broadcastMessage(" has left the chat");
    }

    public void closeEverything(Socket socket,BufferedReader bufferReader,BufferedWriter bufferWriter)
    {
        removeClientHandler();
        
        try
        {
            if(bufferReader != null)
            {
                bufferReader.close();
            }
            if(bufferWriter != null)
            {
                bufferWriter.close();
            }
            if(socket != null)
            {
                socket.close();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void adduser(String name,String pass,String rollno)
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:users;create=true");
            PreparedStatement ps = con.prepareStatement("Insert into users values(?,?,?)");
            ps.setString(1,name);
            ps.setString(2,rollno);
            ps.setString(3,pass);
            ps.executeUpdate();
            con.commit();
            con.close();
        }
        catch(Exception e)
        {
            System.out.println("Unable to add user");
            e.printStackTrace();
        }
    }

    public String checkuser(String name,String pass)
    {
        int count = 0;
        try
        {   
            
            Connection con = DriverManager.getConnection("jdbc:derby:users;create=true");
            String sql="SELECT * FROM users";
            
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while(rs.next())
            {
                String uname = rs.getString("name");               
                String upass = rs.getString("pass");
                if(name .equals(uname))
                {
                    
                    if(pass.equals(upass))
                    {
                        count = count+1;                        
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(count>0)
        {
            return "True";
        }
        else
        {
            return "False";
        }
           
    }
    public void createtable()
    {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:derby:users;create=true");
            Statement st = con.createStatement();
            st.executeUpdate("Create table users(name varchar(30),rollno varchar(10),pass varchar(30))");
            System.out.println("dbCreated");
            System.out.println("tableCreated");
        }
        catch(Exception v)
        {
            
        }
    }
}