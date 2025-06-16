package Objects.models;

public class BaseEmployeeInfo extends BaseObject {

    private String EmpNo;
    
    public BaseEmployeeInfo(String empNo){
        this.EmpNo = empNo;
    }

    public String GetEmpNo() {
        return EmpNo;
    }

    public void SetEmpNo(String empNo) {
        this.EmpNo = empNo;
    }
}
