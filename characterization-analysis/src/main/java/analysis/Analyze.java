package analysis;

import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.ejml.simple.SimpleMatrix;

public class Analyze {
    public static void main(String[] args) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
        Gson gson = new Gson();
        // read the data from the specified JSON file
        CharacterizationDataSet dataSet = gson.fromJson(new FileReader(Config.CHARACTERIZATION_FILE), CharacterizationDataSet.class);

        ArrayList<CharacterizationData> data = new ArrayList<CharacterizationData>();

        add(dataSet.fastBackward, data);
        add(dataSet.fastForward, data);
        add(dataSet.slowBackward, data);
        add(dataSet.slowForward, data);
        add(dataSet.trackWidth, data);



        // new speeds are a linear combination of previous speeds and voltages
        // variables = vLs, VLs, vRs, VRs, + kinetic friction from each motor
        int motorsPerSide = data.get(0).leftPositions.length;
        SimpleMatrix Xs_v = new SimpleMatrix(data.size() - 1, 6*motorsPerSide);
        SimpleMatrix[] Y_left = new SimpleMatrix[motorsPerSide];
        SimpleMatrix[] Y_right = new SimpleMatrix[motorsPerSide];

        for (int j = 0; j < motorsPerSide; j++) {
            Y_left[j]  = new SimpleMatrix(data.size() - 1, 1);
            Y_right[j] = new SimpleMatrix(data.size() - 1, 1);
        }

        for (int i = 0; i < data.size() - 1; i++) {
            for (int j = 0; j < motorsPerSide; j++) {
                Xs_v.set(i, 3*j + 1, data.get(i).leftVelocities[j]);
                Xs_v.set(i, 3*j + 1, data.get(i).leftVolts[j]);
                Xs_v.set(i, 3*j + 2, Math.signum(data.get(i).leftVelocities[i]));

                Xs_v.set(i, 3*(j + motorsPerSide) + 1, data.get(i).rightVelocities[j]);
                Xs_v.set(i, 3*(j + motorsPerSide) + 1, data.get(i).rightVolts[j]);
                Xs_v.set(i, 3*(j + motorsPerSide) + 2, Math.signum(data.get(i).rightVelocities[i]));

                Y_left[j] .set(i, 0, data.get(i + 1).leftVelocities[j]);
                Y_right[j].set(i, 0, data.get(i + 1).rightVelocities[j]);
            }
        }

        SimpleMatrix[] leftSolutions  = new SimpleMatrix[motorsPerSide];
        SimpleMatrix[] rightSolutions = new SimpleMatrix[motorsPerSide];

        double[] leftR2s  = new double[motorsPerSide];
        double[] rightR2s = new double[motorsPerSide];

        for (int j = 0; j < motorsPerSide; j++) {
            LinearRegression left  = new LinearRegression(Xs_v, Y_left[j]);
            LinearRegression right = new LinearRegression(Xs_v, Y_right[j]);

            leftSolutions[j]  = left. getSolution();
            rightSolutions[j] = right.getSolution();

            leftR2s[j]  = left. getR2();
            rightR2s[j] = right.getR2();
        }
    }

    public static void add(double[][] raw, ArrayList<CharacterizationData> data) {
        for (int i = 0; i < raw.length; i++) {
            data.add(new CharacterizationData(raw[i]));
        }
    }
}
