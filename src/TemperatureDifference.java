public class TemperatureDifference {
    public boolean isNegative;
    public boolean isZero;
    public boolean isPositive;
    public boolean isLarge;

    public double negativeValue;
    public double zeroValue;
    public double positiveValue;
    public double largeValue;

    public TemperatureDifference(){

    }

    public TemperatureDifference(boolean isNegative, boolean isZero, boolean isPositive, boolean isLarge,
                                 double negativeValue, double zeroValue, double positiveValue, double largeValue){
        this.isNegative = isNegative;
        this.isZero = isZero;
        this.isPositive = isPositive;
        this.isLarge = isLarge;
        this.negativeValue = negativeValue;
        this.zeroValue = zeroValue;
        this.positiveValue = positiveValue;
        this.largeValue = largeValue;
    }
}
