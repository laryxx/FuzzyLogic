import java.util.ArrayList;

public class Main {
    public static double temperature;
    public static double temperatureDifference;
    public static double dewPoint;
    public static double electricVolt;
    public static Temperature temperatureData = new Temperature();
    public static TemperatureDifference temperatureDifferenceData = new TemperatureDifference();
    public static DewPoint dewPointData = new DewPoint();
    public static ElectricVolt electricVoltData = new ElectricVolt();

    public static void main(String[] args) {
        generateInput();
    }

    public static void generateInput() {
        temperature = 24.0;
        temperatureDifference = 0.75;
        dewPoint = 12;
        electricVolt = 220;
        temperatureData = defineTemperature(temperature);
        temperatureDifferenceData = defineTemperatureDifference(temperatureDifference);
        dewPointData = defineDewPoint(dewPoint);
        electricVoltData = defineElectricVolt(electricVolt);
        ArrayList<FuzzyOutput> fuzzyOutputs = new ArrayList<>();
        fuzzyOutputs = getFuzzyOutputByRules(temperatureData, temperatureDifferenceData, dewPointData, electricVoltData);
        System.out.println("temperature low: " + temperatureData.isLow + ", value: " + temperatureData.lowValue);
        System.out.println("temperature optimal: " + temperatureData.isOptimal + ", value: " + temperatureData.optimalValue);
        System.out.println("temperature high: " + temperatureData.isHigh + ", value: " + temperatureData.highValue);
        System.out.println("------------------------------------");
        System.out.println("temperature difference negative: " + temperatureDifferenceData.isNegative + ", value: " +
                temperatureDifferenceData.negativeValue);
        System.out.println("temperature difference zero: " + temperatureDifferenceData.isZero + ", value: " +
                temperatureDifferenceData.zeroValue);
        System.out.println("temperature difference positive: " + temperatureDifferenceData.isPositive + ", value: " +
                temperatureDifferenceData.positiveValue);
        System.out.println("temperature difference large: " + temperatureDifferenceData.isLarge + ", value: " +
                temperatureDifferenceData.largeValue);
        System.out.println("------------------------------------");
        System.out.println("dew point humid: " + dewPointData.isHumid + ", value: " + dewPointData.humidValue);
        System.out.println("dew point optimal: " + dewPointData.isOptimal + ", value: " + dewPointData.optimalValue);
        System.out.println("------------------------------------");
        System.out.println("electric volt low: " + electricVoltData.isLow + ", value: " + electricVoltData.lowValue);
        System.out.println("electric volt regular: " + electricVoltData.isRegular + ", value: " + electricVoltData.regularValue);
        System.out.println("------------------------------------");
        ArrayList<Calc> compressorSpeed = new ArrayList<>();
        ArrayList<Calc> fanSpeed = new ArrayList<>();
        ArrayList<Calc> modeOfOperation = new ArrayList<>();
        ArrayList<Calc> finDirection = new ArrayList<>();
        for(int i = 0; i < fuzzyOutputs.size(); i++){
            compressorSpeed.add(new Calc(fuzzyOutputs.get(i).strength, fuzzyOutputs.get(i).compressorSpeed));
            fanSpeed.add(new Calc(fuzzyOutputs.get(i).strength, fuzzyOutputs.get(i).fanSpeed));
            modeOfOperation.add(new Calc(fuzzyOutputs.get(i).strength, fuzzyOutputs.get(i).modeOfOperation));
            finDirection.add(new Calc(fuzzyOutputs.get(i).strength, fuzzyOutputs.get(i).finDirection));
            System.out.println("fuzzy compressor speed: " + fuzzyOutputs.get(i).compressorSpeed);
            System.out.println("fuzzy fan speed: " + fuzzyOutputs.get(i).fanSpeed);
            System.out.println("fuzzy mode of operation: " + fuzzyOutputs.get(i).modeOfOperation);
            System.out.println("fuzzy fin direction: " + fuzzyOutputs.get(i).finDirection);
            System.out.println("Strength: " + fuzzyOutputs.get(i).strength);
            System.out.println("------------------------------------");
        }
        double crispCompressorSpeed = calculateCrispCompressorOrFanSpeed(compressorSpeed);
        System.out.println("Crisp value for compressor speed: " + crispCompressorSpeed);
        double crispFanSpeed = calculateCrispCompressorOrFanSpeed(fanSpeed);
        System.out.println("Crisp value for fan speed: " + crispFanSpeed);
        double crispModeOfOperation = calculateCrispModeOfOperation(modeOfOperation);
        System.out.println("Crisp value for mode of operation: " + crispModeOfOperation);
        double crispFinDirection = calculateCrispFinDirection(finDirection);
        System.out.println("Crisp value for fin direction: " + crispFinDirection);
    }

    public static double calculateCrispFinDirection(ArrayList<Calc> finDirection){
        ArrayList<AreaAndCenter> areaAndCenters = new ArrayList<>();
        for(int i = 0; i < finDirection.size(); i++){
            if(finDirection.get(i).desc.equals("Toward")){
                if(finDirection.get(i).value < 1){
                    double trapezoid_h = finDirection.get(i).value;
                    double trapezoid_b = 75;
                    double trapezoid_a = 75 - 50*finDirection.get(i).value;
                    double trapezoid_area = 0.5 * ((trapezoid_a + trapezoid_b)*trapezoid_h);
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 37.5));
                }
                else if(finDirection.get(i).value == 1){
                    double trapezoid_h = 1;
                    double trapezoid_b = 75;
                    double trapezoid_a = 25;
                    double trapezoid_area = 0.5 * ((trapezoid_a + trapezoid_b)*trapezoid_h);
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 37.5));
                }
            }
            else if(finDirection.get(i).desc.equals("Away")){
                if(finDirection.get(i).value < 1 ){
                    double trapezoid_h = finDirection.get(i).value;
                    double trapezoid_b = 75;
                    double trapezoid_a = 25 + 50*finDirection.get(i).value;
                    double trapezoid_area = 0.5 * ((trapezoid_a + trapezoid_b)*trapezoid_h);
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 62.5));
                }
                else if(finDirection.get(i).value == 1){
                    //May be inaccurate due to the nature of membership function
                    double trapezoid_h = 1;
                    double trapezoid_b = 75;
                    double trapezoid_a = 25;
                    double trapezoid_area = 0.5 * ((trapezoid_a + trapezoid_b)*trapezoid_h);
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 62.5));
                }
            }
        }
        return calculateCrispCOS(areaAndCenters);
    }

    public static double calculateCrispModeOfOperation(ArrayList<Calc> modeOfOperation){
        ArrayList<AreaAndCenter> areaAndCenters = new ArrayList<>();
        for(int i = 0; i < modeOfOperation.size(); i++){
            if(modeOfOperation.get(i).desc.equals("ac")){
                double val = modeOfOperation.get(i).value;
                areaAndCenters.add(new AreaAndCenter(  (val*val)/2, val/2 ));
            }
            else if(modeOfOperation.get(i).desc.equals("de")){
                double val = modeOfOperation.get(i).value;
                areaAndCenters.add(new AreaAndCenter(  (val*val)/2, 1-(val/2) ));
            }
        }
        return calculateCrispCOS(areaAndCenters);
    }

    public static double calculateCrispCompressorOrFanSpeed(ArrayList<Calc> compressorSpeed){
        ArrayList<AreaAndCenter> areaAndCenters = new ArrayList<>();
        for(int i = 0; i < compressorSpeed.size(); i++){
            if(compressorSpeed.get(i).desc.equals("Low")){
                if(compressorSpeed.get(i).value < 1){
                    double trapezoid_b = 50;
                    double trapezoid_a = 50-compressorSpeed.get(i).value*20;
                    double trapezoid_h = compressorSpeed.get(i).value;
                    double trapezoid_area = ((trapezoid_a+trapezoid_b)*trapezoid_h)/2;
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 25.0));
                }
                else if(compressorSpeed.get(i).value == 1){
                    //Right trapezoid
                    double trapezoid_b = 50;
                    double trapezoid_a = 30;
                    double trapezoid_h = 1;
                    double trapezoid_area = ((trapezoid_a+trapezoid_b)*trapezoid_h)/2;
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 25.0));
                }
            }
            else if(compressorSpeed.get(i).desc.equals("Medium")){
                if(compressorSpeed.get(i).value < 1 ){

                    double trapezoid_b = 40;

                    double leftX = 20*compressorSpeed.get(i).value + 40;
                    double rightX = 80 - 20*compressorSpeed.get(i).value;
                    double trapezoid_a = rightX - leftX;
                    double trapezoid_h = compressorSpeed.get(i).value;
                    double trapezoid_area = ((trapezoid_a + trapezoid_b)/2)*trapezoid_h;
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 60.0));
                }
                else if(compressorSpeed.get(i).value == 1){
                    double triangle_c = 40;
                    double triangle_h = 1;
                    double triangle_area = 0.5*(triangle_c*triangle_h);
                    areaAndCenters.add(new AreaAndCenter(triangle_area, 60.0));
                }
            }
            else if(compressorSpeed.get(i).desc.equals("Fast")){
                if(compressorSpeed.get(i).value < 1){
                    double trapezoid_b = 30;
                    double trapezoid_a = 20*compressorSpeed.get(i).value + 70;
                    double trapezoid_h = compressorSpeed.get(i).value;
                    double trapezoid_area = ((trapezoid_a+trapezoid_b)*trapezoid_h)/2;
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 85.0));
                }
                else if(compressorSpeed.get(i).value == 1){
                    double trapezoid_b = 30;
                    double trapezoid_a = 10;
                    double trapezoid_h = 1;
                    double trapezoid_area = ((trapezoid_a+trapezoid_b)*trapezoid_h)/2;
                    areaAndCenters.add(new AreaAndCenter(trapezoid_area, 85.0));
                }
            }

        }
        return calculateCrispCOS(areaAndCenters);
    }

    public static double calculateCrispCOS(ArrayList<AreaAndCenter> areaAndCenters){
        double up = 0.0;
        double down = 0.0;
        //System.out.println("Size: " + areaAndCenters.size());
        for(int i = 0; i < areaAndCenters.size(); i++){
            up = up+(areaAndCenters.get(i).area*areaAndCenters.get(i).center);
            down = down + areaAndCenters.get(i).area;
        }
        return up/down;
    }

    public static Temperature defineTemperature(double temperature) {
        boolean isLow = false;
        boolean isOptimal = false;
        boolean isHigh = false;
        double lowValue = 0.0;
        double optimalValue = 0.0;
        double highValue = 0.0;
        if(temperature <= 22){
            lowValue = 1;
        }
        if(temperature >= 22 && temperature <= 25){
            lowValue = (25 - temperature)/3;
            optimalValue = (temperature - 22)/3;
        }
        if(temperature >= 25 && temperature <= 28){
            optimalValue = (28 - temperature)/3;
            highValue = (temperature - 25)/3;
        }
        if(temperature >= 28 && temperature <= 30){
            highValue = 1;
        }

        if(lowValue > 0){
            isLow = true;
        }
        if(optimalValue > 0){
            isOptimal = true;
        }
        if(highValue > 0){
            isHigh = true;
        }

        return new Temperature(isLow, isOptimal, isHigh, lowValue, optimalValue, highValue);
    }

    public static TemperatureDifference defineTemperatureDifference(double temperatureDifference) {
        boolean isNegative = false;
        boolean isZero = false;
        boolean isPositive = false;
        boolean isLarge = false;
        double negativeValue = 0.0;
        double zeroValue = 0.0;
        double positiveValue = 0.0;
        double largeValue = 0.0;

        if(temperatureDifference >= -1 && temperatureDifference <= -0.9){
            negativeValue = 1;
        }
        if(temperatureDifference >= -0.9 && temperatureDifference <= 0){
            negativeValue = -0.9*temperatureDifference;
        }
        if(temperatureDifference >= -0.5 && temperatureDifference <= 0){
            zeroValue = 2*(temperatureDifference + 0.5);
        }
        if(temperatureDifference >= 0 && temperatureDifference <= 0.5){
            zeroValue = 2*(0.5 - temperatureDifference);
        }
        if(temperatureDifference >= 0 && temperatureDifference <= 1){
            positiveValue = temperatureDifference;
        }
        if(temperatureDifference >= 1 && temperatureDifference <= 2){
            positiveValue = 2 - temperatureDifference;
            largeValue = 1 - temperatureDifference;
        }
        if(temperatureDifference >= 2 && temperatureDifference <= 3){
            largeValue = 1;
        }

        if(negativeValue != 0){
            isNegative = true;
        }
        if(zeroValue != 0){
            isZero = true;
        }
        if(positiveValue != 0){
            isPositive = true;
        }
        if(largeValue != 0){
            isLarge = true;
        }

        return new TemperatureDifference(isNegative, isZero, isPositive, isLarge, negativeValue, zeroValue,
                positiveValue, largeValue);
    }

    public static DewPoint defineDewPoint(double dewPoint) {
        boolean isOptimal = false;
        boolean isHumid = false;
        double optimalValue = 0.0;
        double humidValue = 0.0;

        if(dewPoint >= 10 && dewPoint <= 11){
            optimalValue = 1;
        }
        if(dewPoint >= 11 && dewPoint <= 14){
            optimalValue = (14 - dewPoint)/3;
        }
        if(dewPoint >= 12 && dewPoint <= 15){
            humidValue = (dewPoint - 12)/3;
        }
        if(dewPoint >= 15 && dewPoint <= 18){
            humidValue = 1;
        }

        if(optimalValue > 0){
            isOptimal = true;
        }
        if(humidValue > 0){
            isHumid = true;
        }

        return new DewPoint(isOptimal, isHumid, optimalValue, humidValue);
    }

    public static ElectricVolt defineElectricVolt(double electricVolt) {
        boolean isLow = false;
        boolean isRegular = false;
        double lowValue = 0.0;
        double regularValue = 0.0;
        if(electricVolt >= 130 && electricVolt <= 160){
            lowValue = 1;
        }
        if(electricVolt >= 160 && electricVolt <= 180){
            lowValue = (180 - electricVolt)/20;
        }
        if(electricVolt >= 170 && electricVolt <= 190){
            regularValue = (electricVolt-170)/20;
        }
        if(electricVolt >= 190 && electricVolt <=220){
            regularValue = 1;
        }

        if(lowValue > 0){
            isLow = true;
        }
        if(regularValue > 0){
            isRegular = true;
        }

        return new ElectricVolt(isLow, isRegular, lowValue, regularValue);
    }

    public static double getMin(double a, double b, double c, double d) {
        ArrayList<Double> values = new ArrayList<>();
        values.add(a);
        values.add(b);
        values.add(c);
        values.add(d);
        double minValue = Integer.MAX_VALUE;
        for(int i = 0; i < values.size(); i++){
            if(values.get(i) < minValue){
                minValue = values.get(i);
            }
        }
        return minValue;
    }

    public static ArrayList<FuzzyOutput> getFuzzyOutputByRules(Temperature temperatureData, TemperatureDifference temperatureDifferenceData,
                                                    DewPoint dewPointData, ElectricVolt electricVoltData) {
        ArrayList<FuzzyOutput> fuzzyOutputs = new ArrayList<>();
        //1
        if( (temperatureData.isLow)
           && temperatureDifferenceData.isNegative && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.negativeValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //2
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isNegative && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.negativeValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //3
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isNegative && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.negativeValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //4
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isZero && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.zeroValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //5
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isZero && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.zeroValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //6
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isZero && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.zeroValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //7
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isPositive && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.positiveValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //8
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isPositive && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.positiveValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //9
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isPositive && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.positiveValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //10
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isLarge && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.largeValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //11
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isLarge && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.largeValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //12
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isLarge && dewPointData.isOptimal && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.largeValue, dewPointData.optimalValue,
                            electricVoltData.lowValue)));
        }

        //13
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isNegative && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.negativeValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //14
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isNegative && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.negativeValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //15
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isNegative && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.negativeValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //16
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isZero && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Fast", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.zeroValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //17
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isZero && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Medium", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.zeroValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //18
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isZero && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.zeroValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //19
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isPositive && dewPointData.isOptimal && electricVoltData.isRegular) {
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.positiveValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //20
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isPositive && dewPointData.isOptimal && electricVoltData.isRegular) {
            fuzzyOutputs.add(new FuzzyOutput("Medium", "Medium", "ac", "Toward",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.positiveValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //21
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isPositive && dewPointData.isOptimal && electricVoltData.isRegular) {
            fuzzyOutputs.add(new FuzzyOutput("Medium", "Medium", "ac", "Toward",
                    getMin(temperatureData.highValue, temperatureDifferenceData.positiveValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //24
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isLarge && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.largeValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //23
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isLarge && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.largeValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //24
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isLarge && dewPointData.isOptimal && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.highValue, temperatureDifferenceData.largeValue, dewPointData.optimalValue,
                            electricVoltData.regularValue)));
        }

        //25
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isNegative && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.negativeValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //26
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isNegative && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.negativeValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //27
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isNegative && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.negativeValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //28
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isZero && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.zeroValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //29
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isZero && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.zeroValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //30
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isZero && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.zeroValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //31
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isPositive && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.positiveValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //32
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isPositive && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.positiveValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //33
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isPositive && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.positiveValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //34
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isLarge && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.largeValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //35
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isLarge && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.largeValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //36
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isLarge && dewPointData.isHumid && electricVoltData.isLow){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "ac", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.largeValue, dewPointData.humidValue,
                            electricVoltData.lowValue)));
        }

        //37
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isNegative && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "de", "Toward",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.negativeValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //38
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isNegative && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "de", "Away",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.negativeValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //39
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isNegative && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Low", "Low", "de", "Away",
                    getMin(temperatureData.highValue, temperatureDifferenceData.negativeValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //40
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isZero && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "de", "Toward",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.zeroValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //41
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isZero && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Medium", "Fast", "de", "Toward",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.zeroValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //42
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isZero && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Medium", "Medium", "de", "Toward",
                    getMin(temperatureData.highValue, temperatureDifferenceData.zeroValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //43
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isPositive && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.positiveValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //44
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isPositive && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.optimalValue, temperatureDifferenceData.positiveValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //45
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isPositive && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Medium", "Fast", "ac", "Toward",
                    getMin(temperatureData.highValue, temperatureDifferenceData.positiveValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //46
        if( (temperatureData.isLow)
                && temperatureDifferenceData.isLarge && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.lowValue, temperatureDifferenceData.largeValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //47
        if( (temperatureData.isOptimal)
                && temperatureDifferenceData.isLarge && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.optimalValue,temperatureDifferenceData.largeValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        //48
        if( (temperatureData.isHigh)
                && temperatureDifferenceData.isLarge && dewPointData.isHumid && electricVoltData.isRegular){
            fuzzyOutputs.add(new FuzzyOutput("Fast", "Fast", "ac", "Toward",
                    getMin(temperatureData.highValue, temperatureDifferenceData.largeValue, dewPointData.humidValue,
                            electricVoltData.regularValue)));
        }

        return fuzzyOutputs;
    }

}
