package com.hrithikvish.cbsm.model

class ParentItemModelClassForRV {
    var collegeName: String? = null
    var postList: ArrayList<PostModelClassForRV?>? = null

    constructor()

    constructor(collegeName: String?, postList: ArrayList<PostModelClassForRV?>?) {
        this.collegeName = collegeName
        this.postList = postList
    }

    override fun toString(): String {
        return "ParentItemModelClassForRV{" +
                "collegeName='" + collegeName + '}'
    }
}
