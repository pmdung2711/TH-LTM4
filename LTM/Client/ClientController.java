package LTM.Client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.Desktop;
import javax.swing.JTextArea;

import LTM.Model.FileModel;
 
public class ClientController {
    // create Socket object
    private Socket client;
    private String host;
    private int port;
    private JTextArea textAreaLog;
 
    public ClientController(String host, int port, JTextArea textAreaLog) {
        this.host = host;
        this.port = port;
        this.textAreaLog = textAreaLog;
    }
     
    /**
     * connect to server
     * 
     * @author viettuts.vn
     */
    public void connectServer() {
        try {
            client = new Socket(host, port);
            textAreaLog.append("Đã kết nối đến server.\n");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 

    public void sendFile(String sourceFilePath) {
        DataOutputStream outToServer = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null, ois2 = null;
        textAreaLog.append("Đang chờ kết quả xử lý từ Server.\n");

        try{
            try{
                outToServer = new DataOutputStream(client.getOutputStream());
                outToServer.writeUTF("Hello from " + client.getLocalSocketAddress());
       
                // get file info
                FileModel fileInfo = getFileInfo(sourceFilePath);
       
                // send file
                oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(fileInfo);
            }catch (IOException ex) {
                ex.printStackTrace();
            } 
             // make greeting
             
             
            while(true){
                try {
                    ois = new ObjectInputStream(client.getInputStream());
                    FileModel receivedFile = (FileModel) ois.readObject();
                    if (receivedFile != null) {
                        String path = new File("").getAbsolutePath();
                        path = path + "/LTM/Client/DANHSACHPHANCONG.XLSX";
                        createFile(receivedFile, path);
                        break;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } 
            }
    
            while(true){
                try {
                    ois2 = new ObjectInputStream(client.getInputStream());
                    FileModel receivedFile = (FileModel) ois2.readObject();
                    if (receivedFile != null) {
                        String path = new File("").getAbsolutePath();
                        path = path + "/LTM/Client/DANHSACHGIAMSAT.XLSX";
                        createFile(receivedFile, path);
                        break;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } 
            }
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }finally{
            textAreaLog.append("Đã nhận được kết quả từ Server.\n");
            closeStream(ois);
            closeStream(ois2);
            closeStream(oos);
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
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            closeStream(bis);
        }
        return fileInfo;
    }
 

    public void closeSocket() {
        try {
            if (client != null) {
                client.close();
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

    public boolean createFile(FileModel fileInfo, String path) {
        BufferedOutputStream bos = null;

        try {
            if (fileInfo != null) {
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

    public void openFile(){
        String path = new File("").getAbsolutePath();
        path = path + "/LTM/Client/DANHSACHPHANCONG.XLSX";
        File file = new File(path);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        path = new File("").getAbsolutePath();
        path = path + "/LTM/Client/DANHSACHGIAMSAT.XLSX";
        file = new File(path);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}