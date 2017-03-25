package com.speakame.Beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MMFA-YOGESH on 6/13/2016.
 */
public class AllBeans implements Parcelable {


    String Friendimage;
    String Friendname;
    String Friendmobile;
    String Message;
    String Minute;
    String Friendid;
    String Groupid;
    String FriendStatus;
    String GroupName;
    String GroupImage;
    String GroupCreateDate;
    String GroupCreateTime;
    String GroupAdminStatus;
    String FavriouteFriend;
    String Languages;
    boolean isSelected;
    String BlockedStatus;
    String CountryName;
    String Countrycode;


    public AllBeans() {

    }

    protected AllBeans(Parcel in) {
        Friendimage = in.readString();
        Friendname = in.readString();
        Friendmobile = in.readString();
        Message = in.readString();
        Minute = in.readString();
        Friendid = in.readString();
        Groupid = in.readString();
        FriendStatus = in.readString();
        GroupName = in.readString();
        GroupImage = in.readString();
        GroupCreateDate = in.readString();
        GroupCreateTime = in.readString();
        GroupAdminStatus = in.readString();
        FavriouteFriend = in.readString();
        Languages = in.readString();
        BlockedStatus = in.readString();
        CountryName = in.readString();
        Countrycode = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<AllBeans> CREATOR = new Creator<AllBeans>() {
        @Override
        public AllBeans createFromParcel(Parcel in) {
            return new AllBeans(in);
        }

        @Override
        public AllBeans[] newArray(int size) {
            return new AllBeans[size];
        }
    };

    public String getFriendimage() {
        return Friendimage;
    }

    public void setFriendimage(String friendimage) {
        Friendimage = friendimage;
    }

    public String getFriendname() {
        return Friendname;
    }

    public void setFriendname(String friendname) {
        Friendname = friendname;
    }

    public String getFriendmobile() {
        return Friendmobile;
    }

    public void setFriendmobile(String friendmobile) {
        Friendmobile = friendmobile;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getMinute() {
        return Minute;
    }

    public void setMinute(String minute) {
        Minute = minute;
    }

    public String getFriendid() {
        return Friendid;
    }

    public void setFriendid(String friendid) {
        Friendid = friendid;
    }

    public String getFriendStatus() {
        return FriendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        FriendStatus = friendStatus;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public String getGroupImage() {
        return GroupImage;
    }

    public void setGroupImage(String groupImage) {
        GroupImage = groupImage;
    }

    public String getGroupCreateDate() {
        return GroupCreateDate;
    }

    public void setGroupCreateDate(String groupCreateDate) {
        GroupCreateDate = groupCreateDate;
    }

    public String getGroupid() {
        return Groupid;
    }

    public void setGroupid(String groupid) {
        Groupid = groupid;
    }

    public String getGroupCreateTime() {
        return GroupCreateTime;
    }

    public void setGroupCreateTime(String groupCreateTime) {
        GroupCreateTime = groupCreateTime;
    }

    public String getFavriouteFriend() {
        return FavriouteFriend;
    }

    public void setFavriouteFriend(String favriouteFriend) {
        FavriouteFriend = favriouteFriend;
    }

    public String getLanguages() {
        return Languages;
    }

    public void setLanguages(String languages) {
        Languages = languages;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
    public String getBlockedStatus() {
        return BlockedStatus;
    }

    public void setBlockedStatus(String blockedStatus) {
        BlockedStatus = blockedStatus;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getCountrycode() {
        return Countrycode;
    }

    public void setCountrycode(String countrycode) {
        Countrycode = countrycode;
    }

    public String getGroupAdminStatus() {
        return GroupAdminStatus;
    }

    public void setGroupAdminStatus(String groupAdminStatus) {
        GroupAdminStatus = groupAdminStatus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Friendimage);
        dest.writeString(Friendname);
        dest.writeString(Friendmobile);
        dest.writeString(Message);
        dest.writeString(Minute);
        dest.writeString(Friendid);
        dest.writeString(Groupid);
        dest.writeString(FriendStatus);
        dest.writeString(GroupName);
        dest.writeString(GroupImage);
        dest.writeString(GroupCreateDate);
        dest.writeString(GroupCreateTime);
        dest.writeString(GroupAdminStatus);
        dest.writeString(FavriouteFriend);
        dest.writeString(Languages);
        dest.writeString(BlockedStatus);
        dest.writeString(CountryName);
        dest.writeString(Countrycode);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
