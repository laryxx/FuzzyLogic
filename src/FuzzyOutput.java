public class FuzzyOutput {

    String compressorSpeed;
    String fanSpeed;
    String modeOfOperation;
    String finDirection;

    double strength;

    public FuzzyOutput(){

    }

    public FuzzyOutput(String compressorSpeed, String fanSpeed, String modeOfOperation, String finDirection, double strength){
        this.compressorSpeed = compressorSpeed;
        this.fanSpeed = fanSpeed;
        this.modeOfOperation = modeOfOperation;
        this.finDirection = finDirection;
        this.strength = strength;
    }
}
