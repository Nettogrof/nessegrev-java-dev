package ai.nettogrof.battlesnake.snakes.training;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class Train {

	public Train() {
		// TODO Auto-generated constructor stub
	}

	
	
	public static void startTraining() {};
	//public abstract static void showScore();
	
	public static void postStart(String url) throws MalformedURLException, IOException {
		
		 String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true); // Triggers POST.
		  connection.setRequestProperty("Accept-Charset", charset);
		  connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
		  try (OutputStream output = connection.getOutputStream()) {
			    output.write("".getBytes(charset));
			  }
		 


		 // InputStream response = connection.getInputStream();
		  BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
		  String inputLine;
		  while ((inputLine = in.readLine()) != null) 
			  	System.out.println(inputLine);
		  in.close();
	}
	
	public static String getStatus( String url) throws MalformedURLException, IOException {
		 String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(false); // Triggers POST.
		  connection.setRequestProperty("Accept-Charset", charset);
		  connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);
/*
		  try (OutputStream output = connection.getOutputStream()) {
		    output.write("".getBytes(charset));
		  }
*/
		 // InputStream response = connection.getInputStream();
		  BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
		  String inputLine;
		  String t = "";
		  while ((inputLine = in.readLine()) != null) 
			  	t= new String(inputLine);
		  in.close();
		  
		  return t;
		 
	}
	
	public static String post(String url, String param ) throws Exception{
		  String charset = "UTF-8"; 
		  URLConnection connection = new URL(url).openConnection();
		  connection.setDoOutput(true); // Triggers POST.
		  connection.setRequestProperty("Accept-Charset", charset);
		  connection.setRequestProperty("Content-Type", "application/json;charset=" + charset);

		  try (OutputStream output = connection.getOutputStream()) {
		    output.write(param.getBytes(charset));
		  }

		 // InputStream response = connection.getInputStream();
		  BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
		  String inputLine;
		  String t = "";
		  while ((inputLine = in.readLine()) != null) 
			  	t= new String(inputLine);
		  in.close();
		 
		 return t.substring(7,43);
		
		  
		}


	public static void main(String[] args) {
		startTraining();
	}
	

}
