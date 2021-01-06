package LTM.Server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

                readExcelFile(fileInfo.getFilename());

                List<List<Integer>> listPhanCong = ArrangeTeacher(1, listOfRooms.size(), listOfTeachers.size());

                writeExcelFile(listPhanCong);

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

    public void readExcelFile(String filename) {
        try {
            // Doc file tu dia
            String path = new File("").getAbsolutePath();
            path = path + "/LTM/Server/ServerStorage/Input/" + filename;
            FileInputStream excelFile = new FileInputStream(new File(path));
            System.out.println(filename);
            Workbook workbook = new XSSFWorkbook(excelFile);

            Sheet datatypeSheet = workbook.getSheetAt(0);
            System.out.println(workbook.getNumberOfSheets());
            Iterator<Row> iterator = datatypeSheet.iterator();
            Row curRow = iterator.next();
            Cell cell = curRow.getCell(1);
            System.out.println(cell.getStringCellValue());

            listOfTeachers = new ArrayList<TeacherModel>();

            while (iterator.hasNext()) {
                curRow = iterator.next();
                TeacherModel teacher = new TeacherModel();
                cell = curRow.getCell(1);
                if (cell == null)
                    break;
                teacher.setId((int) curRow.getCell(1).getNumericCellValue() + "");
                teacher.setName(curRow.getCell(2).getStringCellValue());
                teacher.setDateOfBirth(curRow.getCell(3).getNumericCellValue() + "");
                teacher.setUniversity(curRow.getCell(4).getStringCellValue());
                listOfTeachers.add(teacher);
            }
            datatypeSheet = workbook.getSheetAt(1);
            listOfRooms = new ArrayList<RoomModel>();
            iterator = datatypeSheet.iterator();
            curRow = iterator.next();
            cell = curRow.getCell(0);
            System.out.println(cell.getStringCellValue());

            while (iterator.hasNext()) {
                curRow = iterator.next();
                RoomModel room = new RoomModel();
                cell = curRow.getCell(1);
                if (cell == null)
                    break;
                room.setRoomNumber((int) curRow.getCell(1).getNumericCellValue() + "");
                listOfRooms.add(room);
            }

            System.out.println("Number of teachers:" + listOfTeachers.size());
            System.out.println("Number of rooms:" + listOfRooms.size());

            workbook.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO Exception");
            ;
        }
    }

    public List<List<Integer>> ArrangeTeacher(int d, int m, int k) {

        // Lay ngau nhien k giam thi tu danh sach
        List<Integer> listNum = new ArrayList<Integer>();
        for (int i = 0; i < k; i++) {
            listNum.add(i);
        }
        System.out.println("List has " + listNum.size());
        List<Integer> initTeacherList = getRandomElement(listNum, k);

        // So giam thi se coi thi se bang 2m, so giam thi con lai se la giam thi hanh
        // lang
        // Tong so giam thi coi thi se la n
        int n = k;
        while (n > 2 * m)
            n--;
        System.out.println("So giam thi se coi thi:" + n);

        List<List<Integer>> phanCongGiamThi = new ArrayList<>();

        phanCongGiamThi.add(initTeacherList);

        List<Integer> selectedTeacherList = new ArrayList<>();
        selectedTeacherList.addAll(initTeacherList);

        int n1, n2;

        for (int j = 0; j < d - 1; j++) {

            // ========== Đổi vị trị các giám thị 1===================
            // Lưu giám thị đầu tiên
            n1 = selectedTeacherList.get(0);
            for (int i = 2; i < n; i += 2) {
                selectedTeacherList.set(i - 2, selectedTeacherList.get(i));
            }
            // Đưa giám thị 1 đầu tiên vào vị trí thứ 2 tính từ cuối
            selectedTeacherList.set(n - 2, n1);

            // ==============Đổi vị trí các giám thị 2===============

            n1 = selectedTeacherList.get(1);
            n2 = selectedTeacherList.get(3);
            for (int i = 5; i < n; i += 2) {
                selectedTeacherList.set(i - 4, selectedTeacherList.get(i));
            }

            // Đưa giám thị 2 đầu tiên vào vị trí thứ 3 tính từ cuối
            selectedTeacherList.set(n - 3, n1);
            // Đưa giám thị 2 thứ 2 vào vị trí cuối cùng
            selectedTeacherList.set(n - 1, n2);

            List<Integer> newTeacherList = new ArrayList<>();
            newTeacherList.addAll(selectedTeacherList);
            phanCongGiamThi.add(newTeacherList);

        }

        return phanCongGiamThi;

    }

    public static List<Integer> getRandomElement(List<Integer> list, int totalItems) {
        Random rand = new Random();

        System.out.println(list.size());

        // create a temporary list for storing
        // selected element
        List<Integer> newList = new ArrayList<>();
        for (int i = 0; i < totalItems; i++) {

            // take a raundom index between 0 to size
            // of given List
            int randomIndex = rand.nextInt(list.size());

            // add element in temporary list
            newList.add(list.get(randomIndex));

            // Remove selected element from orginal list
            list.remove(randomIndex);
        }
        return newList;
    }

    public String writeExcelFile(List<List<Integer>> listPhanCong) {
        System.out.println("Create file excel");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFWorkbook workbook2 = new XSSFWorkbook();

        int numCaThi = listPhanCong.size();

        for (int j = 0; j < numCaThi; j++) {

            // Tao sheet moi
            XSSFSheet sheet = workbook.createSheet("Ngày thi thứ " + (j + 1));

            // Chi muc hang va o
            Row row;
            Cell cell;

            XSSFDataFormat format = workbook.createDataFormat();

            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFCellStyle cellStyleName = workbook.createCellStyle();
            cellStyleName.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFCellStyle cellStyleNumber = workbook.createCellStyle();
            cellStyleNumber.setAlignment(HorizontalAlignment.CENTER);
            cellStyleNumber.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleNumber.setDataFormat(format.getFormat("0"));

            // Tao tieu de o row 0, cell 0
            int rowNum = 0;
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("Danh sách phân công giám thị ngày thứ " + (j + 1));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // Tao danh muc o row 1, cell 1->5
            row = sheet.createRow(rowNum++);
            row.setHeight((short) -1);
            cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue("Mã GV");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue("Họ và tên");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue("Giám thị");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(5);
            cell.setCellValue("Phòng thi");
            cell.setCellStyle(cellStyle);
            row = sheet.createRow(rowNum++);
            row.setHeight((short) -1);
            cell = row.createCell(3);
            cell.setCellValue("Giám thị 1");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(4);
            cell.setCellValue("Giám thị 2");
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 2, 2));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 4));
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 5, 5));

            sheet.setColumnWidth(0, 10 * 256);
            sheet.setColumnWidth(1, 20 * 256);
            sheet.setColumnWidth(2, 25 * 256);
            sheet.setColumnWidth(3, 10 * 256);
            sheet.setColumnWidth(4, 10 * 256);
            sheet.setColumnWidth(5, 10 * 256);

            List<Integer> listCaThi = listPhanCong.get(j);

            for (int i = 0; i < listOfRooms.size() * 2; i++) {
                row = sheet.createRow(rowNum++);
                TeacherModel teacher = listOfTeachers.get(listCaThi.get(i));
                cell = row.createCell(0);
                cell.setCellValue(i + 1);
                cell.setCellStyle(cellStyle);
                cell = row.createCell(1);
                cell.setCellValue(Double.parseDouble(teacher.getId()));
                cell.setCellStyle(cellStyleNumber);
                cell = row.createCell(2);
                cell.setCellValue(teacher.getName());
                cell.setCellStyle(cellStyleName);
                if (i % 2 == 0) {
                    cell = row.createCell(3);
                    cell.setCellValue("X");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    cell.setCellValue(Double.parseDouble(listOfRooms.get(i / 2).getRoomNumber()));
                    cell.setCellStyle(cellStyleNumber);
                } else {
                    cell = row.createCell(4);
                    cell.setCellValue("X");
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(5);
                    if (i == 1)
                        cell.setCellValue(Double.parseDouble(listOfRooms.get(0).getRoomNumber()));
                    else
                        cell.setCellValue(Double.parseDouble(listOfRooms.get(i / 2).getRoomNumber()));

                    cell.setCellStyle(cellStyleNumber);

                }

            }

        }

        try {
            String path = new File("").getAbsolutePath();
            FileOutputStream outputStream = new FileOutputStream(path + "/LTM/Server/ServerStorage/Output/output.xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set so phong
        int soPhongGiamSat = (listOfTeachers.size() - listOfRooms.size() * 2);
        soPhongGiamSat = soPhongGiamSat / listOfRooms.size();
        
        System.out.println("So PHong giam sat: " + soPhongGiamSat);

        for (int j = 0; j < numCaThi; j++) {

            // Tao sheet moi
            XSSFSheet sheet = workbook2.createSheet("Ngày thi thứ " + (j + 1));

            // Chi muc hang va o
            Row row;
            Cell cell;

            XSSFDataFormat format = workbook2.createDataFormat();

            XSSFCellStyle cellStyle = workbook2.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFCellStyle cellStyleName = workbook2.createCellStyle();
            cellStyleName.setAlignment(HorizontalAlignment.LEFT);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            XSSFCellStyle cellStyleNumber = workbook2.createCellStyle();
            cellStyleNumber.setAlignment(HorizontalAlignment.CENTER);
            cellStyleNumber.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleNumber.setDataFormat(format.getFormat("0"));

            // Tao tieu de o row 0, cell 0
            int rowNum = 0;
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("Danh sách phân công giám thị giám sát ngày thứ " + (j + 1));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // Tao danh muc o row 1, cell 1->5
            row = sheet.createRow(rowNum++);
            row.setHeight((short) -1);
            cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(1);
            cell.setCellValue("Mã GV");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(2);
            cell.setCellValue("Họ và tên");
            cell.setCellStyle(cellStyle);
            cell = row.createCell(3);
            cell.setCellValue("Phòng thi được giám sát");
            cell.setCellStyle(cellStyle);

            sheet.setColumnWidth(0, 10 * 256);
            sheet.setColumnWidth(1, 20 * 256);
            sheet.setColumnWidth(2, 25 * 256);
            sheet.setColumnWidth(3, 10 * 256);

            List<Integer> listCaThi = listPhanCong.get(j);

            int count = 0;
            int count2 = 0;


            for (int i = listOfRooms.size() * 2; i < listCaThi.size(); i++) {
                row = sheet.createRow(rowNum++);
                TeacherModel teacher = listOfTeachers.get(listCaThi.get(i));
                cell = row.createCell(0);
                cell.setCellValue(count2 + 1);
                cell.setCellStyle(cellStyle);
                cell = row.createCell(1);
                cell.setCellValue(Double.parseDouble(teacher.getId()));
                cell.setCellStyle(cellStyleNumber);
                cell = row.createCell(2);
                cell.setCellValue(teacher.getName());
                cell.setCellStyle(cellStyleName);
                /*
                if (count < listOfRooms.size() - 1) {
                    cell = row.createCell(3);
                    cell.setCellValue("Từ " + listOfRooms.get(i).getRoomNumber() + " đến "
                            + listOfRooms.get(i + soPhongGiamSat - 1).getRoomNumber());
                    cell.setCellStyle(cellStyleNumber);
                    count+=soPhongGiamSat;
                }else{
                    cell = row.createCell(3);
                    cell.setCellValue("Từ " + listOfRooms.get(i).getRoomNumber() + " đến "
                            + listOfRooms.get(i + listOfRooms.size() -count -1).getRoomNumber());
                    cell.setCellStyle(cellStyleNumber);
                }*/
                
                
                if (count2 % soPhongGiamSat != 0) {
                    cell = row.createCell(3);
                    cell.setCellValue("Phòng " + listOfRooms.get(count).getRoomNumber());
                    cell.setCellStyle(cellStyle);
                    count2++;
                } else {
                    cell = row.createCell(3);
                    count+=1;
                    if(count == listOfRooms.size())
                        break;
                    count2++;
                    cell.setCellValue("Phòng " + listOfRooms.get(count).getRoomNumber());
                    cell.setCellStyle(cellStyle);
                }
                

            }
            sheet.removeRow(row);

        }

        try {
            String path = new File("").getAbsolutePath();
            FileOutputStream outputStream = new FileOutputStream(path + "/LTM/Server/ServerStorage/Output/output2.xlsx");
            workbook2.write(outputStream);
            workbook2.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");

        return "";
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