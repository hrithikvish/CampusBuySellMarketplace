package com.hrithikvish.cbsm;

public class PostModalClassForRV {
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
