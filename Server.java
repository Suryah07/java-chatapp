import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;


public class Server
{
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    public void startServer()
    {
        try 
        {
            System.out.println("Server started...");
            while(!serverSocket.isClosed())
            {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Couldnt start server");    
        }
    }
    public static void main(String args[]) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(5000);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}