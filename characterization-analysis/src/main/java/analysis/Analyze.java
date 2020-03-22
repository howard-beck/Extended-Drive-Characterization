package analysis;

import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

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


    }

    public static void add(double[][] raw, ArrayList<CharacterizationData> data) {
        for (int i = 0; i < raw.length; i++) {
            data.add(new CharacterizationData(raw[i]));
        }
    }
}
