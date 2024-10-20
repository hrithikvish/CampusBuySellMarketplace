package com.hrithikvish.cbsm.model

import java.io.Serializable

class PostModelClassForRV : Serializable {
    @JvmField
    var postId: String? = null
    @JvmField
    var title: String? = null
    @JvmField
    var body: String? = null
    @JvmField
    var user: String? = null
    @JvmField
    var datePosted: String? = null
    @JvmField
    var timePosted: String? = null
    @JvmField
    var postImageUri: String? = null

    override fun toString(): String {
        return "PostModalClassForRV{" +
                "postId='" + postId + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", user='" + user + '\'' +
                ", datePosted='" + datePosted + '\'' +
                ", timePosted='" + timePosted + '\'' +
                ", postImageUri='" + postImageUri + '\'' +
                '}'
    }
}
