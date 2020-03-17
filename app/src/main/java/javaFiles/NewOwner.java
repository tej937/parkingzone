package javaFiles;

public class NewOwner {
    String parking_name;
    String area;
    String address;
    String stiming;
    String etiming;
    String slots;
    String layout1;
    String parking_Status;

    public String getParking_Status() {
        return parking_Status;
    }

    public void setParking_Status(String parking_Status) {
        this.parking_Status = parking_Status;
    }

    public NewOwner(String parking_name, String area, String address, String stiming, String etiming, String slots, String layout1) {
        this.parking_name = parking_name;
        this.area = area;
        this.address = address;
        this.stiming = stiming;
        this.etiming = etiming;
        this.slots = slots;
        this.layout1 = layout1;
    }

    public NewOwner() {
    }

    public String getLayout1() {
        return layout1;
    }

    public void setLayout1(String layout1) {
        this.layout1 = layout1;
    }

    public String getParking_name() {
        return parking_name;
    }

    public void setParking_name(String parking_name) {
        this.parking_name = parking_name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStiming() {
        return stiming;
    }

    public void setStiming(String stiming) {
        this.stiming = stiming;
    }

    public String getEtiming() {
        return etiming;
    }

    public void setEtiming(String etiming) {
        this.etiming = etiming;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }
}
