package ir.taxi1880.operatormanagement.model;

public class PendingComplaintModel {

    private int id;
    private String date;
    private String time;
    private String description;
    private String city;
    private String address;
    private int stationCode;
    private String passengerVoice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStationCode() {
        return stationCode;
    }

    public void setStationCode(Integer stationCode) {
        this.stationCode = stationCode;
    }

    public String getPassengerVoice() {
        return passengerVoice;
    }

    public void setPassengerVoice(String passengerVoice) {
        this.passengerVoice = passengerVoice;
    }
}
