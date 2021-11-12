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

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Couldnt start server");    
        }
    }

    public void closeServerSocket()
    {
        try
        {
            if(serverSocket != null)
            {
                serverSocket.close();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(5000);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}