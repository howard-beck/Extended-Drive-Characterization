package analysis;



public class CharacterizationData {
    public double t;

    public double yaw;
    public double yawRate_raw;
    public double yawRate_processed;

    public double accX_raw;
    public double accX_processed;
    public double accY_raw;
    public double accY_processed;


    public double[] leftPositions;
    public double[] leftVelocities;
    public double[] leftVolts;
    public double[] leftStatorCurrents;
    public double[] leftOutputCurrents;

    public double[] rightPositions;
    public double[] rightVelocities;
    public double[] rightVolts;
    public double[] rightStatorCurrents;
    public double[] rightOutputCurrents;



    public CharacterizationData(double[] data) {
        t = data[0];

        yaw = data[3];
        yawRate_raw = data[4];
        yawRate_processed = data[5];

        accX_raw = data[6];
        accX_processed = data[7];
        accY_raw = data[8];
        accY_processed = data[9];

        double numMotors = (data.length - 10)/5.0;

        if (numMotors != (int) numMotors) {
            return;
        }

        if (numMotors % 2 != 0) {
            return;
        }

        int motorsPerSide = (int) numMotors / 2;

        for (int i = 0; i < motorsPerSide; i++) {
            leftPositions[i]       = data[10 + 5*i];
            leftVelocities[i]      = data[10 + 5*i + 1];
            leftVolts[i]           = data[10 + 5*i + 2];
            leftStatorCurrents[i]  = data[10 + 5*i + 3];
            leftOutputCurrents[i]  = data[10 + 5*i + 4];

            rightPositions[i]      = data[10 + 5*i + 5*motorsPerSide];
            rightVelocities[i]     = data[10 + 5*i + 5*motorsPerSide + 1];
            rightVolts[i]          = data[10 + 5*i + 5*motorsPerSide + 2];
            rightStatorCurrents[i] = data[10 + 5*i + 5*motorsPerSide + 3];
            rightOutputCurrents[i] = data[10 + 5*i + 5*motorsPerSide + 4];
        }
    }
}