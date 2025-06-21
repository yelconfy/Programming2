package Helper;

public class GuidUtil {

    public static String GenerateEmpNo(String lastEmpNo) {
        int newEmpNo = Integer.parseInt(lastEmpNo) + 1;
        return String.valueOf(newEmpNo);
    }
}
