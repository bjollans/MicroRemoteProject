/*
 * Copyright (C) 2015 Bernard Jollans
 * 
 * 	This file is part of MicroRemote.
 *
 *  MicroRemote is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *   
 *  MicroRemote is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You can find a copy of the GNU General Public License along with
 *  the MicroRemote project.  If not, see <http://www.gnu.org/licenses/>.
 */

package drivers;

import global.meta.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class AndroidDriver extends global.util.Driver{

	private ListenerThread listener;
	ServerSocket serverSocket;
	Socket clientSocket;
	 BufferedReader in;
	 PrintWriter out;
	 boolean flag = true;
	
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
		            in = new BufferedReader(
		                new InputStreamReader(clientSocket.getInputStream()));
		            out =
			                new PrintWriter(clientSocket.getOutputStream(), true);  

		            String inputLine= in.readLine();

		            String[] codes = inputLine.split(Constants.QRSEPERATOR);
		            long code = 0;
		            try{
		            	code = Integer.parseInt(codes[0]);
		            }
		            catch(Exception e){
		            }
		            if(code == Constants.QRENCODER && codes.length>1){		            	
		            	omitMessage(codes[1]);
		            }
	            	end();
	            	if(flag){
		            	listener2 = new ListenerThread(portNumber);
		            	listener2.start();
	            	}
		        } catch (Exception e) {
		        	
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