import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class clientlayout extends JFrame implements ActionListener
{
    Container a;
    JButton msgsend;
    JTextArea tmsgsend;
    JLabel messages;
    JTextArea tmessages;
    JTextField sendmsg;
    JLabel sendlabel;


    public clientlayout()
    {

        setTitle("Chat app");
        setBounds(300,90,1000,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        a = getContentPane();
        a.setVisible(true);
        a.setLayout(null);
        messages = new JLabel("Messages");
        messages.setSize(300,30);
        messages.setLocation(100,55);
        a.add(messages);

        tmessages = new JTextArea();
        tmessages.setSize(500,300);
        tmessages.setLocation(100,100);
        a.add(tmessages);

        msgsend = new JButton("Send");
        msgsend.setSize(100,30);
        msgsend.setLocation(500,450);
        a.add(msgsend);
        msgsend.addActionListener(this);

        sendlabel = new JLabel("Send");
        sendlabel.setSize(300,30);
        sendlabel.setLocation(100,420);
        a.add(sendlabel);

        sendmsg = new JTextField();
        sendmsg.setSize(400,30);
        sendmsg.setLocation(100,450);
        a.add(sendmsg);
            
        setVisible(true);  
    }

    public void writemsg(String msg)
    {

    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==msgsend)
        {

        }

    }
}

public class Client
{
    private Socket socket;
    private BufferedReader bufferReader;
    private BufferedWriter bufferWriter;
    private String username;
    clientlayout cl = new clientlayout();

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
            System.out.println("Client client closing everytjing");
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
                //clientlayout cl = new clientlayout();
                String messageToSend = sc.nextLine();
                bufferWriter.write(username + ":" +messageToSend);
                bufferWriter.newLine();
                bufferWriter.flush();
            }
            sc.close();
        }
        catch(IOException e)
        {
            System.out.println("Closing all");
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
                       System.out.println("Closing everything");
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
            
            Socket socket = new Socket("localhost" , 5000);
            //layout obj = new layout(socket);
            System.out.println("Enter your username for the chat: ");
            Scanner sc = new Scanner(System.in);
            String username;
            username = sc.nextLine();
            //String username = obj.usernamestring;
            Client client = new Client(socket,username);
            System.out.println("Joined Chat-Room");
            client.listenForMessage();
            client.sendMessage();
            sc.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Coundnt Establish Connection with Server...");
            System.out.println("Exiting...");
        }
    }
}

/*class layout extends JFrame implements ActionListener
    {
        Container c;
        JLabel title;
        JLabel username;
        JTextField tusername;
        JButton login;
        public String usernamestring;
        public String passwordstring;
        Socket socket;


        public layout(Socket s)
        {
            socket = s;
            
            setTitle("Chat app");
            setBounds(300,90,1000,600);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setResizable(false);
            
            c = getContentPane();
            c.setVisible(true);
            c.setLayout(null);

            username = new JLabel("Username: ");
            username.setSize(300,30);
            username.setLocation(400,100);
            c.add(username);

            tusername = new JTextField();
            tusername.setSize(300,30);
            tusername.setLocation(500,100);
            c.add(tusername);

            login = new JButton("Login");
            login.setSize(300,30);
            login.setLocation(450,200);
            c.add(login);
            login.addActionListener(this);
            
            setVisible(true);
            
        }
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == login)
            {
                usernamestring = tusername.getText();
                System.out.println("Action");
                this.dispose();
                setVisible(false);
                Client client = new Client(socket,usernamestring);
                System.out.println("Joined Chat-Room");
                client.listenForMessage();
                client.sendMessage();
                
            }

        }
    }*/