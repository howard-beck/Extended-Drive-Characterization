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

    }
}
