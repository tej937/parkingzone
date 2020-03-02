package javaFiles;

public class CheckOutDetails {

    String location;
    String start_time;
    String end_time;
    String parking_slot;
    String total_amount;
    String payment_status;
    String current_date;
    String final_time;
    String plate_no;

    public CheckOutDetails(String location, String start_time, String end_time, String parking_slot, String total_amount, String payment_status, String current_date, String final_time, String plate_no) {
        this.location = location;
        this.start_time = start_time;
        this.end_time = end_time;
        this.parking_slot = parking_slot;
        this.total_amount = total_amount;
        this.payment_status = payment_status;
        this.current_date = current_date;
        this.final_time = final_time;
        this.plate_no = plate_no;
    }

    public CheckOutDetails() {
    }

    public String getPlate_no() {
        return plate_no;
    }

    public void setPlate_no(String plate_no) {
        this.plate_no = plate_no;
    }

    public String getCurrent_date() {
        return current_date;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public String getFinal_time() {
        return final_time;
    }

    public void setFinal_time(String final_time) {
        this.final_time = final_time;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getParking_slot() {
        return parking_slot;
    }

    public void setParking_slot(String parking_slot) {
        this.parking_slot = parking_slot;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }


}
