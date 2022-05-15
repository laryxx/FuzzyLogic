public class Temperature {
    public boolean isLow;
    public boolean isOptimal;
    public boolean isHigh;

    public double lowValue;
    public double optimalValue;
    public double highValue;

    public Temperature(){

    }

    public Temperature(boolean isLow, boolean isOptimal, boolean isHigh, double lowValue, double optimalValue,
                       double highValue){
        this.isLow = isLow;
        this.isOptimal = isOptimal;
        this.isHigh = isHigh;
        this.lowValue = lowValue;
        this.optimalValue = optimalValue;
        this.highValue = highValue;
    }
}
