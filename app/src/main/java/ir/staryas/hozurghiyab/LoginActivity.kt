package ir.staryas.hozurghiyab


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import ir.staryas.hozurghiyab.admin.AdminMainActivity
import ir.staryas.hozurghiyab.model.UsrMsg
import ir.staryas.hozurghiyab.networking.ApiClient
import ir.staryas.hozurghiyab.networking.ApiService
import ir.staryas.hozurghiyab.professor.ProMainActivity
import ir.staryas.hozurghiyab.student.StudentMainActivity
import ir.staryas.hozurghiyab.utils.PrefManage
import ir.staryas.hozurghiyab.utils.ViewDialog
import ir.staryas.hozurghiyab.utils.isNetworkAvailable
import ir.staryas.hozurghiyab.utils.toast
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private val apiClient = ApiClient()
    private lateinit var viewDialog:ViewDialog
    private lateinit var prefManage:PrefManage
    private var rText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initComponent()

    }

    private fun initComponent() {
        viewDialog = ViewDialog(this)
        prefManage = PrefManage(this)

        // button onClickListener for login
        btnLogin.setOnClickListener {
            if (isNetworkAvailable(this)) {
                val username = tieUsername.text.toString().trim()
                val password = tiePassword.text.toString().trim()
                val selectedRadio = radioGroup.checkedRadioButtonId
                val radioBtn = findViewById<RadioButton>(selectedRadio)

                when(radioBtn.text.toString()){
                    "مدیر" -> {
                        rText = "admin"
                    }
                    "استاد" -> {
                        rText = "professor"
                    }
                    "دانشجو" -> {
                        rText = "student"
                    }
                }
                try {
                    if(validate(username,password))
                    {
                        btnLogin.isEnabled = false
                        viewDialog.showDialog()
                        login(rText,username,password)
                    }
                } catch (e:Exception){

                }
            } else {
                toast(getString(R.string.no_internet), this)
            }
        }
    }

    private fun login(mode:String,username:String,password:String) {
        val client = apiClient.getClient().create(ApiService::class.java)
        val call = client.login(mode,username,password)

        call.enqueue(object : Callback<UsrMsg>{
            override fun onFailure(call: Call<UsrMsg>, t: Throwable) {
                viewDialog.hideDialog()
                btnLogin.isEnabled = true
                toast(getString(R.string.network_failure),this@LoginActivity)
            }

            override fun onResponse(call: Call<UsrMsg>, response: Response<UsrMsg>) {
                viewDialog.hideDialog()
                btnLogin.isEnabled = true

                // get response values
                val success = response.body()!!.success
                val message = response.body()!!.message

                // check if operation was successful
                if (success == 1) {

                    // save user credentials in sharedPreferences
                    prefManage.setIsUserLoggedIn(true)
                    prefManage.setUserId(response.body()!!.user!![0].userId!!.toInt())
                    val fullname = response.body()!!.user!![0].userName + " " + response.body()!!.user!![0].userFamily
                    prefManage.setFullName(fullname)
                    prefManage.setUsername(response.body()!!.user!![0].userUsername!!)
                    prefManage.setUserRole(rText)
                    toast(message!!,this@LoginActivity)

                    // go to next activity if successful
                    when(rText){
                        "admin" -> {

                            val intent = Intent(this@LoginActivity,AdminMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "professor" -> {
                            val intent = Intent(this@LoginActivity, ProMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "student" -> {
                            val intent = Intent(this@LoginActivity, StudentMainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                } else {
                    toast(message!!,this@LoginActivity)
                }
            }

        })
    }

    private fun validate(username: String,password: String):Boolean {
        var valid = true

        val pattern: Pattern = Pattern.compile("^[A-Za-z0-9._-]{2,25}\$")

        if (username.isEmpty() || !pattern.matcher(username).matches())
        {
            tieUsername.error = "لطفا یک نام کاربری معتبر وارد کنید"
            valid = false
        } else {
            tieUsername.error = null
        }

        if (password.isEmpty())
        {
            tiePassword.error = "کلمه عبور خالی است"
            valid = false
        } else {
            tiePassword.error = null
        }

        return valid
    }



}
