package LTM;

import java.util.ArrayList;
import java.util.List;

public class Test2 {
    public static void main(String[] args){
        List<Integer> listRoom = new ArrayList<>();
        List<Integer> listTeacherInRoom = new ArrayList<>();
        List<Integer> listTeacher = new ArrayList<>();

        int numTeacher, numRoom;

        for(int i = 0; i < 487; i++){
            listRoom.add(120 + i);
            listTeacherInRoom.add(0);
        }

        for(int i = 0; i < 2017; i++){
            listTeacher.add(0);
        }

        System.out.println("So giao vien: " + listTeacher.size());
        System.out.println("So phong thi: " + listRoom.size());
        numRoom = listRoom.size();
        numTeacher = listTeacher.size();


        if(numRoom > numTeacher){
            int roomPerTeacher = numRoom / numTeacher + 1;
            int actualRoom = numTeacher * roomPerTeacher;
            System.out.println("Moi giao vien coi  " + roomPerTeacher);
            System.out.println("So Phong thi duoc coi " + actualRoom);
            int j = 0;
            for(int i = 0; i<numRoom; i++){
                if(j==numTeacher) j = 0;
                listTeacher.set(j, listTeacher.get(j)+1);
                j++;
            }
            System.out.println(listTeacher);
        }else if(numRoom <= numTeacher){
            int j = 0;
            for(int i = 0; i<numTeacher; i++){
                if(j==numRoom) j = 0;
                listTeacherInRoom.set(j, listTeacherInRoom.get(j)+1);
                j++;
            }
            System.out.println(listTeacherInRoom);
        }



        
    }
}
