import java.io.*;
import java.net.Socket;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


public class Client extends JFrame implements ActionListener
{
    private String newuser = "dlshskjhdkhskfiuwfwie6f7wyeffw8fw7fyw8yfe";
    private String broadcaststring = "kljfldkfgge6r78g68g76er7ggeg87erwe67sdvx687bg7r7jy/8";
    private String authorisechat = "jdshfkdjfhskuhfkdjfh564dfg65s4fb5d4bd6b@873gbdjkhkjf";


    private Socket socket;
    private BufferedReader bufferReader;
    private BufferedWriter bufferWriter;
    String username;
    String password;
    String rollno;
    Container a;
    JButton msgsend;
    JTextArea tmsgsend;
    JLabel messages;
    JTextArea tmessages;
    JTextField sendmsg;
    JLabel sendlabel;
    String msg;
    JButton close;
    Font font = new Font("Times New Roman", Font.PLAIN, 20);
    Font tfont = new Font("Times New Roman", Font.PLAIN, 18);

    Container z;
    JTextField tuname;
    JLabel luname;
    JButton buname;
    JLabel lupass;
    JPasswordField tupass;
    JButton newusr;
    JButton createusr;
    JTextField turoll;
    JLabel luroll;
    JButton subdetails;

    

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
            chatnow();
        }
        catch(IOException e)
        {
            System.out.println("Client client closing everytjing");
            closeEverything(socket,bufferReader,bufferWriter);
        }
    }



    public void chatnow()
    {
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

            tmessages = new JTextArea("Welcome to chat room\n");
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

            /*close = new JButton("Exit");
            
            close.setSize(100,30);
            close.setLocation(600,450);
            a.add(close);
            close.addActionListener(this);*/
            

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

        luroll = new JLabel("Roll-no");
        luroll.setFont(font);
        luroll.setSize(100,30);
        luroll.setLocation(100,240);
        
        turoll = new JTextField();
        turoll.setSize(300,30);
        turoll.setLocation(210,240);
        

        createusr = new JButton("New user");
        createusr.setSize(200,50);
        createusr.setLocation(250,450);
        z.add(createusr);
        createusr.addActionListener(this);

        subdetails = new JButton("Submit details");
        subdetails.setSize(200,50);
        subdetails.setLocation(250,450);
        //z.add(subdetails);
        subdetails.addActionListener(this);
    }




    public void sendMessage(String msg)
    {
        try
        {
            String messageToSend = msg;
            bufferWriter.write(broadcaststring);
            bufferWriter.newLine();
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
       new Thread(new Runnable() 
       {
           @Override
           public void run()
           {
               String msgFromGroupChat;
               while(socket.isConnected())
               {
                   try
                   {
                       
                       msgFromGroupChat = bufferReader.readLine();
                       if(msgFromGroupChat.equals(authorisechat))
                       {
                           continue;
                       }
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
            
            try
            {
                String ua = tuname.getText();
                String pa = tupass.getText();
                bufferWriter.write(ua);
                bufferWriter.newLine();
                bufferWriter.write(pa);
                bufferWriter.newLine();
                bufferWriter.flush();
                String response = bufferReader.readLine();
                if(response.equals(authorisechat))
                {
                    username = tuname.getText();
                    password = tupass.getText();
                    z.removeAll();
                    z.repaint();
                }
                else
                {
                    JOptionPane.showMessageDialog(this,"Check your credentials");
                    System.out.println(response);
                }
                
            }
            catch(Exception g)
            {
                g.printStackTrace();
                System.out.println("Unable to login");
            }
        }

        if(e.getSource() == createusr)
        {
            z.add(luroll);
            z.add(turoll);
            createusr.setText("Submit details");
            z.remove(createusr);
            z.add(subdetails);
            z.remove(buname);
            z.repaint();
            
        }
        if(e.getSource() == subdetails)
        {
            username = tuname.getText();
            password = tupass.getText();
            rollno = turoll.getText();
            try
            {
                bufferWriter.write(newuser);
                bufferWriter.newLine();
                bufferWriter.write(username);
                bufferWriter.newLine();
                bufferWriter.write(password);
                bufferWriter.newLine();
                bufferWriter.write(rollno);
                bufferWriter.newLine();
                bufferWriter.flush();    
            }
            catch(Exception u)
            {
                System.out.println("Unable to add user");
            }
            z.removeAll();
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