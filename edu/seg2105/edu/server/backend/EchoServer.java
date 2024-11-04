package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

import javax.management.DescriptorKey;
import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  ChatIF serverUI;

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF ui)
  {
    super(port);
    serverUI = ui;
    try
    {
      listen(); //Start listening for connections
    }
    catch (Exception ex)
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {

    System.out.println("Message received: " + msg + " from " + client.getInfo("loginID"));

    if (msg.toString().startsWith("#login")) {
      if ((boolean) client.getInfo("haveID")) {
        try {
          client.sendToClient("#login cannot be called again.");
        } catch (IOException e) {}
        try {
          client.close();
        } catch (IOException e) {}
      } else {
        client.setInfo("loginID", msg.toString().substring(7));
        client.setInfo("haveID", true);
        System.out.println(client.getInfo("loginID") + " has logged on.");
      }
    } else {
      this.sendToAllClients(client.getInfo("loginID") + ": " + msg);
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }

  @Override
  protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
    System.out.println("Client disconnected.");
  }

  @Override
  protected synchronized void clientDisconnected(ConnectionToClient client) {
    System.out.println("Client disconnected.");
  }

  @Override
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("A new client has connected to the server.");
    client.setInfo("haveID", false);
  }

  public void handleMessageFromServerUI(String message) {
    if (message.startsWith("#")) {
      handleCommand(message);
    } else {
      sendToAllClients("SERVER MSG> " + message);
      serverUI.display("SERVER MSG> " + message);
    }
  }

  private void handleCommand(String command) {
    if (command.equals("#quit")) {
      quit();
    } else if (command.equals("#stop")) {
      stopListening();
    } else if (command.equals("#close")) {
      try {
        close();
      } catch (IOException e) {
      }
    } else if (command.startsWith("#setport")) {
      try {
        int port = Integer.parseInt(command.substring(9));
        setPort(port);
      } catch (Exception e) {
        serverUI.display("Please provide the format #setport <port>");
      }
    } else if (command.equals("#start")) {
      if (!isListening()) {
        try {
          listen();

        } catch (IOException e) {}
      } else {
        serverUI.display("Server already listening.");
      }
    } else if (command.equals("#getport")) {
      serverUI.display(getPort() + "");
    }
  }

  public void quit()
  {
    try
    {
      close();
    }
    catch(IOException e) {}
    System.exit(0);
  }

}
//End of EchoServer class
