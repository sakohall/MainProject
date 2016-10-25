import java.util.ArrayList;

/**
 * Created by zqian on 25/10/2016.
 */
public class ColorManagerModel {
    ArrayList<ColorMixerModel> set;
    public ColorManagerModel(){
        set = new ArrayList<>();
    }
    public int saveColorSet(ColorMixerModel m){
        set.add(new ColorMixerModel(m));
        return set.size()-1;
    }

    public ColorMixerModel getColorSet(int idx){
        return new ColorMixerModel(set.get(idx));
    }

    public void replaceColorSet(int idx, ColorMixerModel m){
        set.set(idx, new ColorMixerModel(m));
    }
}
