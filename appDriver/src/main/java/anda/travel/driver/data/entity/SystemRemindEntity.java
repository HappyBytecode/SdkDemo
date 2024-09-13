package anda.travel.driver.data.entity;

public class SystemRemindEntity {
    private String notifyType; //1 对话框 2 语音 0 all
    private String title;
    private String content;
    public static final String NOTIFY_DIALOG = "1";
    public static final String NOTIFY_VOICE = "2";

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
