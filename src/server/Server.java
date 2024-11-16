package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 1234;  // The port number for the server
    private static final int MAX_CLIENTS = 10;  // Maximum number of concurrent clients
    private static final ExecutorService executorService = Executors.newFixedThreadPool(MAX_CLIENTS);  // Thread pool to handle multiple clients

    // List of quiz questions and their correct answers
    public static final List<Question> quiz = Arrays.asList(
        new Question("What is the capital of France?", "Paris"),
        new Question("What is 5 + 7?", "12"),
        new Question("Who wrote 'Romeo and Juliet'?", "Shakespeare"),
        new Question("What is the largest ocean on Earth?", "Pacific"),
        new Question("Who won the FIFA World Cup in 2022?", "Argentina")
    );

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            // Continuously accept client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Accept client connection
                System.out.println("Client connected!");
                // Assign a new thread from the thread pool to handle the client
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();  // Print errors related to server setup or connection issues
        }
    }

    // Class representing a quiz question and its correct answer
    static class Question {
        private String question;
        private String answer;

        public Question(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }
    }

    // Class responsible for handling communication with a single client
    static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                int score = 0;  // Initialize client's score
                for (Question q : quiz) {  // Loop through the quiz questions
                    out.println("Question: " + q.getQuestion());  // Send question to the client
                    String clientAnswer = in.readLine();  // Receive the client's answer

                    if (clientAnswer == null) {  // If the client disconnects unexpectedly
                        System.out.println("Client disconnected.");
                        break;
                    }

                    // Check the client's answer and send feedback
                    if (clientAnswer.equalsIgnoreCase(q.getAnswer())) {
                        out.println("Correct!");  // Correct answer
                        score++;
                    } else {
                        out.println("Incorrect! The correct answer is: " + q.getAnswer());  // Incorrect answer
                    }
                }
                out.println("Quiz finished! Your final score is: " + score);  // Send final score to the client
            } catch (IOException e) {
                e.printStackTrace();  // Handle communication errors
            } finally {
                try {
                    socket.close();  // Close the client socket when done
                } catch (IOException e) {
                    e.printStackTrace();  // Handle errors when closing the socket
                }
            }
        }
    }
}
