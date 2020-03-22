package analysis;

import com.google.gson.annotations.SerializedName;

public class CharacterizationDataSet {
    @SerializedName("slow-forward")
    public double[][] slowForward;
    
    @SerializedName("slow-backward")
    public double[][] slowBackward;
    
    @SerializedName("fast-forward")
    public double[][] fastForward;
    
    @SerializedName("fast-backward")
    public double[][] fastBackward;
    
    @SerializedName("track-width")
    public double[][] trackWidth;
}