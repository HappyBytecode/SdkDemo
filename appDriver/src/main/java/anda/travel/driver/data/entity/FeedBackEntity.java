package anda.travel.driver.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class FeedBackEntity implements Parcelable {

    /**
     * uuid : 17015da4c7704b1e9df334f6557f1343
     * content :
     * status : 1
     * result : null
     * process_time : null
     * images : ,/DRV00000070655cba04184b5b68dd184/feedback/full/202102208816a500-2a69-4b54-ae73-82f15a47beed.jpg,/DRV00000070655cba04184b5b68dd184/feedback/full/2021022068b6b947-9c0c-4ff6-bf59-9180be0aec71.png
     * paths : ["http://192.168.100.232/images//DRV00000070655cba04184b5b68dd184/feedback/full/202102208816a500-2a69-4b54-ae73-82f15a47beed.jpg","http://192.168.100.232/images//DRV00000070655cba04184b5b68dd184/feedback/full/2021022068b6b947-9c0c-4ff6-bf59-9180be0aec71.png"]
     */

    private String uuid;
    private String content;
    private int status; ////1 未回复 2 已回复
    private String result;
    private String process_time;
    private String images;
    private List<String> paths;
    private String createTime;

    public FeedBackEntity() {

    }

    protected FeedBackEntity(Parcel in) {
        uuid = in.readString();
        content = in.readString();
        status = in.readInt();
        result = in.readString();
        process_time = in.readString();
        images = in.readString();
        paths = in.createStringArrayList();
        createTime = in.readString();
    }

    public static final Creator<FeedBackEntity> CREATOR = new Creator<FeedBackEntity>() {
        @Override
        public FeedBackEntity createFromParcel(Parcel in) {
            return new FeedBackEntity(in);
        }

        @Override
        public FeedBackEntity[] newArray(int size) {
            return new FeedBackEntity[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getProcess_time() {
        return process_time;
    }

    public void setProcess_time(String process_time) {
        this.process_time = process_time;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(content);
        parcel.writeInt(status);
        parcel.writeString(result);
        parcel.writeString(process_time);
        parcel.writeString(images);
        parcel.writeStringList(paths);
        parcel.writeString(createTime);
    }
}
