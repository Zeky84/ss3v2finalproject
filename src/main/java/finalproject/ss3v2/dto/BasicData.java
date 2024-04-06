package finalproject.ss3v2.dto;

public class BasicData {

    private String zip_code;
    private int Efficiency;
    private int One_Bedroom;
    private int Two_Bedroom;
    private int Three_Bedroom;
    private int Four_Bedroom;
    private String year;

    public int getEfficiency() {
        return Efficiency;
    }

    public void setEfficiency(int efficiency) {
        Efficiency = efficiency;
    }

    public int getOne_Bedroom() {
        return One_Bedroom;
    }

    public void setOne_Bedroom(int one_Bedroom) {
        One_Bedroom = one_Bedroom;
    }

    public int getTwo_Bedroom() {
        return Two_Bedroom;
    }

    public void setTwo_Bedroom(int two_Bedroom) {
        Two_Bedroom = two_Bedroom;
    }

    public int getThree_Bedroom() {
        return Three_Bedroom;
    }

    public void setThree_Bedroom(int three_Bedroom) {
        Three_Bedroom = three_Bedroom;
    }

    public int getFour_Bedroom() {
        return Four_Bedroom;
    }

    public void setFour_Bedroom(int four_Bedroom) {
        Four_Bedroom = four_Bedroom;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
