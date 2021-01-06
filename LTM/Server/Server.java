package LTM.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;


import LTM.Model.FileModel;
import LTM.Model.RoomModel;
import LTM.Model.TeacherModel;

public class Server extends Thread {
    // create serverSocket object
    private ServerSocket serverSocket;
    private int port = 9900;
    List<TeacherModel> listOfTeachers;
    List<RoomModel> listOfRooms;
    

    public static void main(String[] args) {
        Server tcpServer = new Server();
        tcpServer.open();
        tcpServer.start();
    }

    public void open() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server is open on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            Socket server = null;
            DataInputStream inFromClient = null;
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
            ServerService serverService;
            try {
                // accept connect from client and create Socket object
                server = serverSocket.accept();
                System.out.println("connected to " + server.getRemoteSocketAddress());

                // get greeting from client
                inFromClient = new DataInputStream(server.getInputStream());
                System.out.println(inFromClient.readUTF());

                // receive file info
                ois = new ObjectInputStream(server.getInputStream());
                FileModel fileInfo = (FileModel) ois.readObject();
                if (fileInfo != null) {
                    createFile(fileInfo);
                }

                serverService = new ServerService(listOfTeachers, listOfRooms);
                serverService.onService(fileInfo.getFilename(), 1);

                
                oos = new ObjectOutputStream(server.getOutputStream());
                String path = new File("").getAbsolutePath();
                path = path + "/LTM/Server/ServerStorage/Output/output.xlsx";
                FileModel sentFile1 = getFileInfo(path);
                sentFile1.setStatus("True");
                oos.writeObject(sentFile1);

                oos = new ObjectOutputStream(server.getOutputStream());
                path = new File("").getAbsolutePath();
                path = path + "/LTM/Server/ServerStorage/Output/output2.xlsx";
                sentFile1 = getFileInfo(path);
                sentFile1.setStatus("True");
                oos.writeObject(sentFile1);
                
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                // close all stream
                closeStream(ois);
                closeStream(oos);
                closeStream(inFromClient);
                // close session
                closeSocket(server);
            }
        }
    }

    private boolean createFile(FileModel fileInfo) {
        BufferedOutputStream bos = null;

        try {
            if (fileInfo != null) {
                String path = new File("").getAbsolutePath();
                path = path + "/LTM/Server/ServerStorage/Input/" + fileInfo.getFilename();
                File fileReceive = new File(path);
                bos = new BufferedOutputStream(new FileOutputStream(fileReceive));
                // write file content
                bos.write(fileInfo.getDataBytes());
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeStream(bos);
        }
        return true;
    }

    public void closeSocket(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void closeStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

   
    private FileModel getFileInfo(String sourceFilePath) {
        FileModel fileInfo = null;
        BufferedInputStream bis = null;
        try {
            File sourceFile = new File(sourceFilePath);
            bis = new BufferedInputStream(new FileInputStream(sourceFile));
            fileInfo = new FileModel();
            byte[] fileBytes = new byte[(int) sourceFile.length()];
            // get file info
            bis.read(fileBytes, 0, fileBytes.length);
            fileInfo.setFilename(sourceFile.getName());
            fileInfo.setDataBytes(fileBytes);
            fileInfo.setStatus("False");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            closeStream(bis);
        }
        return fileInfo;
    }
 

}

/// Co 500 phong và 1000 nguoi --> 2 moi nguoi giam sat 1 phong
/// Co 500 phong va 100 nguoi --> mot nguoi giam sat 5 phong