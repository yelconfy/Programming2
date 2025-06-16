package Objects.models;

public class AddressInfo extends BaseEmployeeInfo {

    private String HouseBlkLotNo;
    private String Street;
    private String Barangay;
    private String CityMunicipality;
    private String Province;
    private String ZipCode;

    public AddressInfo(String empNo) {
        super(empNo);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Getters">
    public String GetHouseBlkLotNo() {
        return HouseBlkLotNo;
    }

    public String GetStreet() {
        return Street;
    }

    public String GetBarangay() {
        return Barangay;
    }

    public String GetCityMunicipality() {
        return CityMunicipality;
    }

    public String GetProvince() {
        return Province;
    }

    public String GetZipCode() {
        return ZipCode;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">
    public void SetHouseBlkLotNo(String houseBlkLotNo) {
        this.HouseBlkLotNo = houseBlkLotNo;
    }

    public void SetStreet(String street) {
        this.Street = street;
    }

    public void SetBarangay(String barangay) {
        this.Barangay = barangay;
    }

    public void SetCityMunicipality(String cityMunicipality) {
        this.CityMunicipality = cityMunicipality;
    }

    public void SetProvince(String province) {
        this.Province = province;
    }

    public void SetZipCode(String zipCode) {
        this.ZipCode = zipCode;
    }
    // </editor-fold>
}
