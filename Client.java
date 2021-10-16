import javax.imageio.IIOException;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    private Socket socket;
    private BufferedReader bufferReader;
    private BufferedWriter bufferWriter;
    private String username;

    public Client(Socket socket,String username)
    {
        try
        {
            this.socket = socket;
            this.bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }
        catch(IOException e)
        {
            closeEverything(socket,bufferReader,bufferWriter);
        }
    }

    public void sendMessage()
    {
        try
        {
            bufferWriter.write(username);
            bufferWriter.newLine();
            bufferWriter.flush();

            Scanner sc = new Scanner(System.in);
            while(socket.isConnected())
            {
                String messageToSend = sc.nextLine();
                bufferWriter.write(username + ":" +messageToSend);
                bufferWriter.newLine();
                bufferWriter.flush();
            }
        }
        catch(IOException e)
        {
            closeEverything(socket,bufferReader,bufferWriter);
        }
    }

    public void listenForMessage()
    {
       new Thread(new Runnable() {
           @Override
           public void run()
           {
               String msgFromGroupChat;
               while(socket.isConnected())
               {
                   try
                   {
                       msgFromGroupChat = bufferReader.readLine();
                       System.out.println(msgFromGroupChat);
                   }
                   catch(IOException e)
                   {
                       closeEverything(socket,bufferReader,bufferWriter);
                   }
               }

           }
       }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferReader, BufferedWriter bufferWriter)
    {
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

    public static void main(String args[])
    {
        try
        {
        System.out.println("Enter your username for the chat: ");
        Scanner sc = new Scanner(System.in);
        String username = sc.nextLine();
        Socket socket = new Socket("localhost" , 5000);
        Client client = new Client(socket,username);
        client.listenForMessage();
        client.sendMessage();
        }
        catch(UnknownHostException u)
        {
            System.out.println("hell");
        }
    }
}