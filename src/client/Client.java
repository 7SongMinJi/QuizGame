package client;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost";  // Default server IP
        int serverPort = 1234;  // Default server port

        // Read server info from the file
        File file = new File("server_info.dat");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                serverAddress = reader.readLine();  // Read IP address from the file
                String portString = reader.readLine();  // Read port number from the file
                if (portString != null) {
                    serverPort = Integer.parseInt(portString);  // Convert port to integer
                }
            } catch (IOException e) {
                System.out.println("Error reading server_info.dat, using default values.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid port format in server_info.dat, using default port.");
            }
        } else {
            System.out.println("server_info.dat not found, using default values.");
        }

        // Connect to the server
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server!");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {  // Read messages from the server
                System.out.println(serverMessage);  // Display message from the server

                if (serverMessage.startsWith("Question: ")) {  // If the message is a question
                    System.out.print("Your answer: ");
                    String answer = userInput.readLine();  // Get user input for the answer
                    out.println(answer);  // Send the answer to the server
                } else if (serverMessage.startsWith("Quiz finished")) {  // If the quiz is finished
                    break;  // Exit the loop
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  // Handle input/output errors
        }
    }
}
