// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI)
    throws IOException 
  {
    super(host, port); //Call the superclass constructor

    this.clientUI = clientUI;
    this.loginID = loginID;

    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    if (message.startsWith("#")) {
      handleCommand(message);
    } else {
      try
      {
        sendToServer(message);
      }
      catch(IOException e)
      {
        clientUI.display
                ("Could not send message to server.  Terminating client.");
        quit();
      }
    }

  }

  private void handleCommand(String command) {
    if (command.equals("#quit")) {
      if (isConnected()) {
        quit();
      } else {
        System.exit(0);
      }
    } else if (command.equals("#logoff")) {
      try {
        closeConnection();
        clientUI.display("Logged off.");
      } catch (IOException e) {
        clientUI.display("Something went wrong when trying to log off.");
      }
    } else if (command.equals("#login")) {
      if (isConnected()) {
        clientUI.display("Already logged in.");
      } else {
        try {
          openConnection();
          clientUI.display("Logged in.");
        } catch (IOException e) {
          clientUI.display("Something went wrong when trying to log in.");
        }
      }
    } else if (command.equals("#gethost")) {
        clientUI.display(getHost());
    } else if (command.equals("#getport")) {
        clientUI.display(getPort() + "");
    } else if (command.startsWith("#sethost")) {
        try {
          String host = command.substring(9);
          if (host.isEmpty()) {
            throw new Exception();
          }
          setHost(host);
        } catch (Exception e) {
          clientUI.display("Please provide the format #sethost <host>");
        }
    } else if (command.startsWith("#setport")) {
      try {
        int port = Integer.parseInt(command.substring(9));
        setPort(port);
      } catch (Exception e) {
        clientUI.display("Please provide the format #setport <port>");
      }
    } else {
      clientUI.display("Unrecognized command.");
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

  @Override
  public void connectionException(Exception exception) {
    clientUI.display("The server has shut down.");
    System.exit(0);
  }

  @Override
  protected void connectionClosed() {
    clientUI.display("Connection closed.");
  }

  @Override
  protected void connectionEstablished() {
    try {
      sendToServer("#login " + loginID);
      System.out.println(loginID + " has logged on.");

    }catch (IOException e){
    }
  }
}
//End of ChatClient class
