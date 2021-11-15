import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Client extends JFrame implements ActionListener
{
    private Socket socket;
    private BufferedReader bufferReader;
    private BufferedWriter bufferWriter;
    String username;
    String password;
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



    Container z;
    JTextField tuname;
    JLabel luname;
    JButton buname;
    JLabel lupass;
    JPasswordField tupass;
    

    public Client(Socket socket)
    {
        try
        {           
            this.socket = socket;
            this.bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            getuname();
            while(username == null)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Cant wait");
                }
            }
            bufferWriter.write(username);
            bufferWriter.newLine();
            bufferWriter.write(password);
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
        a.repaint(); 
        
    }

    public void getuname()
    {
        
        setTitle("User");
        setBounds(300,90,800,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        z = getContentPane();
        z.setVisible(true);
        z.setLayout(null);

        luname = new JLabel("UserName");
        luname.setFont(font);
        luname.setSize(100,30);
        luname.setLocation(100,150);
        z.add(luname);

        tuname = new JTextField();
        tuname.setSize(300,30);
        tuname.setLocation(210,150);
        z.add(tuname);

        buname = new JButton("Start chat");
        buname.setSize(200,50);
        buname.setLocation(500,450);
        z.add(buname);
        buname.addActionListener(this);

        lupass = new JLabel("Password");
        lupass.setFont(font);
        lupass.setSize(100,30);
        lupass.setLocation(100,190);
        z.add(lupass);

        tupass = new JPasswordField();
        tupass.setSize(300,30);
        tupass.setLocation(210,190);
        z.add(tupass);

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
            writemsg("You: "+msg);
            sendmsg.setText(null);           
        }

        if(e.getSource() == buname)
        {
            username = tuname.getText();
            password = tupass.getText();
            System.out.println(password);
            z.remove(luname);
            z.remove(tuname);
            z.remove(buname);
            z.remove(tupass);
            z.remove(lupass);
            z.repaint();
        }

    }


    public static void main(String args[])
    {
        try
        {
            
            Socket socket = new Socket("localhost" , 5000);
            Client client = new Client(socket);
            client.listenForMessage();

        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Coundnt Establish Connection with Server...");
            System.out.println("Exiting...");
        }
    } 

}

