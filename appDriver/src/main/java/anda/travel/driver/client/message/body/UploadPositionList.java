package anda.travel.driver.client.message.body;

import java.util.ArrayList;
import java.util.List;

import anda.travel.driver.client.message.Body;

public class UploadPositionList implements Body {

    public static UploadPositionList createFrom(UploadPosition position) {
        ArrayList<UploadPosition> list = new ArrayList<>();
        position.isPresent = true; //是当前点
        list.add(position);
        return new UploadPositionList(list);
    }

    public UploadPositionList() {

    }

    public UploadPositionList(List<UploadPosition> positions) {
        this.positions = positions;
    }

    private List<UploadPosition> positions;

    public List<UploadPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<UploadPosition> positions) {
        this.positions = positions;
    }
}
