package anda.travel.driver.data.entity;

import java.util.List;

/**
 * @Author: Jotyy
 * @Company: HeXing
 * @Date: 2019/5/28
 * @Desc:
 */
public class InviteCountEntity {

    private String inviteCount;
    private String firstRideCount;
    private List<InviteCountEntity.InvitedInfo> invitedInfos;
    private List<InvitedInfo> firstRideInfos;

    public InviteCountEntity() {
    }

    public InviteCountEntity(String inviteCount, String firstRideCount, List<InviteCountEntity.InvitedInfo> invitedInfos, List<InvitedInfo> firstRideInfos) {
        this.inviteCount = inviteCount;
        this.firstRideCount = firstRideCount;
        this.invitedInfos = invitedInfos;
        this.firstRideInfos = firstRideInfos;
    }

    public List<InviteCountEntity.InvitedInfo> getInvitedInfos() {
        return invitedInfos;
    }

    public void setInvitedInfos(List<InviteCountEntity.InvitedInfo> invitedInfos) {
        this.invitedInfos = invitedInfos;
    }

    public List<InvitedInfo> getFirstRideInfos() {
        return firstRideInfos;
    }

    public void setFirstRideInfos(List<InvitedInfo> firstRideInfos) {
        this.firstRideInfos = firstRideInfos;
    }

    public String getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(String inviteCount) {
        this.inviteCount = inviteCount;
    }

    public String getFirstRideCount() {
        return firstRideCount;
    }

    public void setFirstRideCount(String firstRideCount) {
        this.firstRideCount = firstRideCount;
    }

    @Override
    public String toString() {
        return "InviteCountEntity{" +
                "inviteCount='" + inviteCount + '\'' +
                ", firstRideCount='" + firstRideCount + '\'' +
                ", invitedInfos=" + invitedInfos +
                ", firstRideInfos=" + firstRideInfos +
                '}';
    }

    public static class InvitedInfo {
        private String passMobile;
        private String passName;

        public InvitedInfo() {

        }

        public InvitedInfo(String passMobile, String passName) {
            this.passMobile = passMobile;
            this.passName = passName;
        }

        public String getPassMobile() {
            return passMobile;
        }

        public void setPassMobile(String passMobile) {
            this.passMobile = passMobile;
        }

        public String getPassName() {
            return passName;
        }

        public void setPassName(String passName) {
            this.passName = passName;
        }
    }

}
