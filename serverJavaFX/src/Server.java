import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import Models.Users;

import static Services.UserService.*;

public class Server {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5000)) {
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected");

                ObjectInputStream objectInput = new ObjectInputStream(socket.getInputStream());
                String clientInput = (String) objectInput.readObject();
                String input = null;

                if (clientInput.indexOf("/") != -1) {
                    input = clientInput.split("/")[0];
                } else {
                    input = clientInput;
                }

                switch (input) {
                    case "checkToken": {
                        String token = clientInput.split("/")[1];
                        Users result = checkToken(token);

                        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(result);
                    }
                    break;
                    case "showUsers": {
                        ArrayList<Users> usersList = getUsersList();

                        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(usersList);
                    }
                    break;
                    case "inputUser": {
                        String tableToken = clientInput.split("/")[1];
                        String tableUsername = clientInput.split("/")[2];
                        String tableAdmin = clientInput.split("/")[3];
                        String tableUpload = clientInput.split("/")[4];
                        String tableDownload = clientInput.split("/")[5];
                        String tableVisualization = clientInput.split("/")[6];
                        addUser(tableToken, tableUsername, tableAdmin, tableUpload, tableDownload, tableVisualization);
                    }
                    break;
                    case "getFiles": {
                        File currDir = new File("");
                        String path = currDir.getAbsolutePath();
                        File destination = new File(path + "\\src\\Files");
                        ArrayList<String> allFiles = new ArrayList<>();
                        for (final File fileEntry : destination.listFiles()) {
                            if (!fileEntry.isDirectory()) {
                                allFiles.add(fileEntry.getName());
                            }
                        }
                        if(!allFiles.isEmpty()){
                            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                            objectOutput.writeObject(allFiles);
                        }
                    }
                    break;
                    case "viewFile": {
                        String fileName = clientInput.split("/")[1];
                        File currDir = new File("");
                        String selectedFile = currDir.getAbsolutePath() + "\\src\\Files\\" + fileName;

                        File fileToSend = new File(selectedFile);

                        FileInputStream fis = new FileInputStream(fileToSend);
                        byte[] data = new byte[(int) fileToSend.length()];
                        fis.read(data);
                        fis.close();
                        String content = new String(data, "UTF-8");

                        ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                        objectOutput.writeObject(content);

                    }
                    break;
                    case "uploadFile": {
                        String fileName = clientInput.split("/", 3)[1];
                        String content = clientInput.split("/", 3)[2];
                        try {
                            File currDir = new File("");
                            String path = currDir.getAbsolutePath();
                            String fullFilePath = path + "\\src\\Files\\" + fileName;
                            File myObj = new File(fullFilePath);
                            if (myObj.createNewFile()) {
                                System.out.println("File created: " + myObj.getName());
                                try {
                                    FileWriter myWriter = new FileWriter(fullFilePath);
                                    myWriter.write(content);
                                    myWriter.close();
                                    ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                                    objectOutput.writeObject(true);
                                    System.out.println("Successfully wrote to the file.");
                                } catch (IOException e) {
                                    ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                                    objectOutput.writeObject(false);
                                    System.out.println("An error occurred.");
                                    e.printStackTrace();
                                }

                            } else {
                                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                                objectOutput.writeObject(false);
                                System.out.println("File already exists.");
                            }
                        } catch (IOException e) {
                            ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                            objectOutput.writeObject(false);
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }

        } catch(IOException | ClassNotFoundException e) {
            System.out.println("Server exception " + e.getMessage());
        }
    }

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                System.out.println(fileEntry.getName());
            }
        }
    }
}
