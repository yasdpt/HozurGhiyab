package ir.staryas.hozurghiyab.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PrefManage @SuppressLint("CommitPrefEdits") constructor(context: Context) {
    var pref:SharedPreferences? = null
    var editor:SharedPreferences.Editor? = null
    var _context:Context? = context

    var PRIVATE_MODE = 0
    private val PREF_NAME = "hozurghiyab-user"
    private val prefUsername = "username"
    private val prefUserId = "id"
    private val prefFullName = "fullname"
    private val prefisUserLoggedIn = "isUserLoggedIn"
    private val prefUserRole = "userRole"


    init {
        pref = _context!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref!!.edit()
    }

    fun setUsername(username: String) {
        editor!!.putString(prefUsername, username)
        editor!!.commit()
    }

    fun getUsername(): String? {
        return pref!!.getString(prefUsername, "null")
    }

    fun setIsUserLoggedIn(isUserLoggedIn:Boolean) {
        editor!!.putBoolean(prefisUserLoggedIn,isUserLoggedIn)
        editor!!.commit()
    }
    fun getIsUserLoggedIn():Boolean? {
        return pref!!.getBoolean(prefisUserLoggedIn,false)
    }

    fun setUserId(userId: Int) {
        editor!!.putInt(prefUserId, userId)
        editor!!.commit()
    }

    fun getUserId(): Int? {
        return pref!!.getInt(prefUserId, 0)
    }



    fun setFullName(fullName: String) {
        editor!!.putString(prefFullName, fullName)
        editor!!.commit()
    }

    fun getFullName(): String? {
        return pref!!.getString(prefFullName, "null")
    }

    fun setUserRole(userRole:String) {
        editor!!.putString(prefUserRole,userRole)
        editor!!.commit()
    }

    fun getUserRole(): String? {
        return pref!!.getString(prefUserRole, "admin")
    }

}