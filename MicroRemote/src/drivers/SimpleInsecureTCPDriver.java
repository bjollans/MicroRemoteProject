package drivers;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import global.meta.Constants;

public class SimpleInsecureTCPDriver extends global.util.Driver{

	private ListenerThread listener;
	ServerSocket serverSocket;
	Socket clientSocket;
	 DataInputStream in;
	 PrintWriter out;
	 boolean flag = true;
	 private String acceptedIP; 
	
	private class ListenerThread extends Thread{
		private int portNumber;
		private ListenerThread listener2;
		
		public ListenerThread(int portNumber){
			this.portNumber = portNumber;
		}
		public void run(){
			try {
					listener = this;
		            serverSocket =
		                new ServerSocket(portNumber);
		            clientSocket = serverSocket.accept();
		            System.out.println(clientSocket.getInetAddress().toString());
		            if(acceptedIP == null){
		            	acceptedIP=clientSocket.getInetAddress().toString();
		            }
		            if(acceptedIP.equals(clientSocket.getInetAddress().toString())){
			            in = new DataInputStream(clientSocket.getInputStream());
			            out =
				                new PrintWriter(clientSocket.getOutputStream(), true);  
	
			            String msg = ""+(char)in.readByte();
			            try{
			            	@SuppressWarnings("unused")
							int x = Integer.parseInt(msg);
			            	omitMessage(msg);
			            }
			            catch(Exception e){
			            	//TODO Blacklist IP
			            }
		            }
	            	end();
	            	if(flag){
		            	listener2 = new ListenerThread(portNumber);
		            	listener2.start();
	            	}
		        } catch (Exception e) {
		        	e.printStackTrace();
		        }
			return;
		}
		public void end(){
			
			try{
	        	in.close();
			}
			catch(Exception e){
				
			}
			try{
	        	out.close();
			}
			catch(Exception e){
				
			}		
			try{
	        	clientSocket.close();
	        	clientSocket = null;
			}
			catch(Exception e){
			}
			try{
	        	serverSocket.close();
	        	serverSocket = null;
			}
			catch(Exception e){
				
			}
			try{
				listener2.interrupt();
				listener2 = null;
			}
			catch(NullPointerException npe){
			}
		}
	}
	
	@Override
	public void initialize(){
		int portNumber = Constants.PORTNUMBER;
		listener = new ListenerThread(portNumber);
		listener.start();
	}
	

	@Override
	public void dispose(){
		flag = false;
		listener.end();
		listener.interrupt();
	}

}
