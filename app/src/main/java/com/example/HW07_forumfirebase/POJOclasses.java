/*InClass 08
        Grouping3 - 18
        Name: Rahul Govindkumar
        Name: Amruth Nag
        */


package com.example.HW07_forumfirebase;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class POJOclasses {

    public static class Forum {
        String userName;
        String title;
        String content;
        String time;
        String Uid;
        String docId;

        public Forum(String[] contents) {
            this.userName = contents[0];
            this.title = contents[1];
            this.content = contents[2];
            this.time = contents[3];
            this.Uid = contents[4];
            try {
                this.docId = contents[5];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getUid() {
            return Uid;
        }

        public void setUid(String uid) {
            Uid = uid;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public HashMap<String, String> toHashmap() {
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("userName", this.userName);
            dataMap.put("title", this.title);
            dataMap.put("content", this.content);
            dataMap.put("time", this.time);
            dataMap.put("Uid", this.Uid);
            return dataMap;
        }
    }

    public static class Route {
        ArrayList<GeoPoint> points;
        String dateTime;

        public Route(ArrayList<GeoPoint> points, String dateTime) {
            this.points = points;
            this.dateTime = dateTime;
        }

        public ArrayList<GeoPoint> getPoints() {
            return points;
        }

        public void setPoints(ArrayList<GeoPoint> points) {
            this.points = points;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        @Override
        public String toString() {
            return dateTime;
        }
    }
}
