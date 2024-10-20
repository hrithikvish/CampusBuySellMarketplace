package com.hrithikvish.cbsm.model;

import java.io.Serializable;

public class PostModelClassForRV implements Serializable {
    String postId, title, body, user, datePosted, timePosted, postImageUri;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPostImageUri(String postImageUri) {
        this.postImageUri = postImageUri;
    }

    public String getBody() {
        return body;
    }

    public String getUser() {
        return user;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public String getPostImageUri() {
        return postImageUri;
    }

    @Override
    public String toString() {
        return "PostModalClassForRV{" +
                "postId='" + postId + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", user='" + user + '\'' +
                ", datePosted='" + datePosted + '\'' +
                ", timePosted='" + timePosted + '\'' +
                ", postImageUri='" + postImageUri + '\'' +
                '}';
    }
}
