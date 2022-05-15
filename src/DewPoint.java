public class DewPoint {
    boolean isOptimal;
    boolean isHumid;

    double optimalValue;
    double humidValue;

    public DewPoint(){

    }

    public DewPoint(boolean isOptimal, boolean isHumid, double optimalValue, double humidValue){
        this.isOptimal = isOptimal;
        this.isHumid = isHumid;
        this.optimalValue = optimalValue;
        this.humidValue = humidValue;
    }
}
