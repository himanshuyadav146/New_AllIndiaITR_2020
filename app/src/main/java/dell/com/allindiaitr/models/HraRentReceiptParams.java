package dell.com.allindiaitr.models;

public class HraRentReceiptParams {

    private String Name;
    private String Email;
    private String LandlordName;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getLandlordName() {
        return LandlordName;
    }

    public void setLandlordName(String landlordName) {
        LandlordName = landlordName;
    }

    public String getPanNumber() {
        return PanNumber;
    }

    public void setPanNumber(String panNumber) {
        PanNumber = panNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getRentPaid() {
        return RentPaid;
    }

    public void setRentPaid(int rentPaid) {
        RentPaid = rentPaid;
    }

    public String getSDate() {
        return SDate;
    }

    public void setSDate(String SDate) {
        this.SDate = SDate;
    }

    public String getEDate() {
        return EDate;
    }

    public void setEDate(String EDate) {
        this.EDate = EDate;
    }

    public String getMonths() {
        return Months;
    }

    public void setMonths(String months) {
        Months = months;
    }

    private String PanNumber;
    private String Address;

//    public HraRentReceiptParams(String name, String email, String landlordName, String panNumber, String address, String rentPaid,
//                                 String SDate, String EDate, String months) {
//        Name = name;
//        Email = email;
//        LandlordName = landlordName;
//        PanNumber = panNumber;
//        Address = address;
//        RentPaid = rentPaid;
//
//        this.SDate = SDate;
//        this.EDate = EDate;
//        Months = months;
//    }

    private int RentPaid;
   ;
    private String SDate;
    private String EDate;
    private String Months;

    public HraRentReceiptParams(String name,
                                String email,
                                String landlordName,
                                String panNumber,
                                String address,
                                int rentPaid,
                                String SDate,
                                String EDate,
                                String months) {
        Name = name;
        Email = email;
        LandlordName = landlordName;
        PanNumber = panNumber;
        Address = address;
        RentPaid = rentPaid;
        this.SDate = SDate;
        this.EDate = EDate;
        Months = months;
    }
}
