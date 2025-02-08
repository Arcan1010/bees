package srebnikm.bee.model;

import java.util.ArrayList;
import java.util.List;

public record Input(List<Double> interior, List<Double> exterior) {

    public List<Double> getCollected() {
        List<Double> result = new ArrayList<>();
        result.addAll(interior);
        result.addAll(exterior);
        return result;
    }

    public List<Double> getCollectedCut(int halfSize) {
        List<Double> result = new ArrayList<>();
        result.addAll(interior.subList(0, halfSize));
        result.addAll(exterior.subList(0, halfSize));
        return result;
    }
}
