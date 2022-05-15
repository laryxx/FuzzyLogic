public class ElectricVolt {
    boolean isLow;
    boolean isRegular;

    double lowValue;
    double regularValue;

    public ElectricVolt(){

    }

    public ElectricVolt(boolean isLow, boolean isRegular, double lowValue, double regularValue){
        this.isLow = isLow;
        this.isRegular = isRegular;
        this.lowValue = lowValue;
        this.regularValue = regularValue;
    }
}
