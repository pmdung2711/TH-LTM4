package LTM;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {
    public static void main(String[] args) {

        // m la so phong thi
        int m = 3;

        // d la so ngay thi hay so lan phan cong
        int d = 3;

        // k la so giam thi duoc phan cong cho ki kiem tra
        int k = 13;

        // Lay ngau nhien k giam thi tu danh sach
        List<Integer> listNum = new ArrayList<Integer>();
        for (int i = 0; i < 2911; i++) {
            listNum.add(i);
        }
        List<Integer> selectedTeacherList = getRandomElement(listNum, k);
        System.out.println(selectedTeacherList);

        // So giam thi se coi thi se bang 2m, so giam thi con lai se la giam thi hanh
        // lang
        // Tong so giam thi coi thi se la n
        int n = k;
        while (n > 2 * m)
            n--;
        System.out.println("So giam thi se coi thi:" + n);

        List<List<Integer>> phanCongGiamThi = new ArrayList<>();

        phanCongGiamThi.add(selectedTeacherList);
        System.out.println(selectedTeacherList);

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

            System.out.println(selectedTeacherList);
            phanCongGiamThi.add(selectedTeacherList);

        }

    }

    public static List<Integer> getRandomElement(List<Integer> list, int totalItems) {
        Random rand = new Random();

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
}
