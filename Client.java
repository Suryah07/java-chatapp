import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame implements ActionListener
{
    private Socket socket;
    private BufferedReader bufferReader;
    private BufferedWriter bufferWriter;
    private String username;
    Container a;
    JButton msgsend;
    JTextArea tmsgsend;
    JLabel messages;
    JTextArea tmessages;
    JTextField sendmsg;
    JLabel sendlabel;
    String msg;
    Font font = new Font("Times New Roman", Font.PLAIN, 20);
    Font tfont = new Font("Times New Roman", Font.PLAIN, 18);
    

    public Client(Socket socket,String username)
    {
        try
        {
            this.socket = socket;
            this.bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;   
            bufferWriter.write(username);
            bufferWriter.newLine();
            bufferWriter.flush();

        }
        catch(IOException e)
        {
            System.out.println("Client client closing everytjing");
            closeEverything(socket,bufferReader,bufferWriter);
        }
        setTitle("Chat app");
        setBounds(300,90,800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        a = getContentPane();
        a.setVisible(true);
        a.setLayout(null);


        messages = new JLabel("Messages");
        messages.setFont(font);
        messages.setSize(300,30);
        messages.setLocation(100,55);
        a.add(messages);

        tmessages = new JTextArea();
        tmessages.setSize(500,300);
        tmessages.setLocation(100,100);
        tmessages.setFont(tfont);
        tmessages.setEditable(false);
        JScrollPane scrolll = new JScrollPane(tmessages);
        scrolll.setSize(500,300);
        scrolll.setLocation(100,100);
        a.add(scrolll);

        msgsend = new JButton("Send");
        
        msgsend.setSize(100,30);
        msgsend.setLocation(500,450);
        a.add(msgsend);
        msgsend.addActionListener(this);
        

        sendlabel = new JLabel("Your message");
        sendlabel.setFont(font);
        sendlabel.setSize(300,30);
        sendlabel.setLocation(100,420);
        a.add(sendlabel);

        sendmsg = new JTextField();
        sendmsg.setSize(400,30);
        sendmsg.setLocation(100,450);
        a.add(sendmsg);
            
        setVisible(true);  
    }
   

    public void sendMessage(String msg)
    {
        try
        {
            String messageToSend = msg;
            bufferWriter.write(messageToSend);
            bufferWriter.newLine();
            bufferWriter.flush();
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
                       writemsg(msgFromGroupChat);
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

    public void writemsg(String msg)
    { 
        tmessages.append(msg+"\n");
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==msgsend)
        {
            String msg = sendmsg.getText();
            sendMessage(msg);
            writemsg("you: "+msg);
            sendmsg.setText(null);           
        }
    }

    public static void main(String args[])
    {
        try
        {
            
            Socket socket = new Socket("localhost" , 5000);
            System.out.println("Enter your username for the chat: ");
            Scanner sc = new Scanner(System.in);
            String username;
            username = sc.nextLine();
            Client client = new Client(socket,username);
            System.out.println("Joined Chat-Room");
            client.listenForMessage();
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
