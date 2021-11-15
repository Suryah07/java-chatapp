import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ClientHandler implements Runnable
{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferReader;
    private BufferedWriter bufferWriter;
    private String clientUsername;
    private String clientPassword;

    public ClientHandler(Socket socket)
    {
        try
        {
            this.socket = socket;
            this.bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferReader.readLine();
            this.clientPassword = bufferReader.readLine();
            checkuser(clientUsername, clientPassword);
            if(checkuser(clientUsername, clientPassword)=="True")
            {              
                this.bufferWriter.write("Allow login");
                this.bufferWriter.newLine();
                this.bufferWriter.flush();
                clientHandlers.add(this);
                broadcastMessage(" has entered the chat");
            }
            else
            {
                this.bufferWriter.write(clientUsername+"sdfsdf"+clientPassword);
                this.bufferWriter.newLine();
                this.bufferWriter.flush();
            }
        }
        catch(IOException e)
        {
            closeEverything(socket,bufferReader,bufferWriter);
            
        }
    }

    @Override
    public void run()
    {
        String messageFromClient;
        
        while(socket.isConnected())
        {
            try
            {
                messageFromClient = bufferReader.readLine();
                broadcastMessage(messageFromClient);
            }
            catch(IOException e)
            {
                closeEverything(socket,bufferReader,bufferWriter);
                break;
            }

        }
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
}