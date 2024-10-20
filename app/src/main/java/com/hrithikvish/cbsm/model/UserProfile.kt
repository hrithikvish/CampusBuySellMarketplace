package com.hrithikvish.cbsm.model

//for offline usage
class UserProfile(
    var emailName: String?,
    private var email: String?,
    private var clg: String?,
    private var name: String?
) {
    fun getName(): String {
        return if (name != null) name!! else ""
    }

    fun getEmail(): String {
        return if (email != null) email!! else ""
    }

    fun getClg(): String {
        return if (clg != null) clg!! else ""
    }

    fun setName(name: String) {
        this.name = name
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setClg(clg: String) {
        this.clg = clg
    }

    override fun toString(): String {
        return "UserProfile{" +
                "emailName='" + emailName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", clg='" + clg + '\'' +
                '}'
    }
}
