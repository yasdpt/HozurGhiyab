package ir.staryas.hozurghiyab

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.staryas.doorway.utils.Tools
import ir.staryas.hozurghiyab.adapters.AdapterListStudents
import ir.staryas.hozurghiyab.adapters.AdapterListUc
import ir.staryas.hozurghiyab.admin.AdminClasses
import ir.staryas.hozurghiyab.model.*
import ir.staryas.hozurghiyab.networking.ApiClient
import ir.staryas.hozurghiyab.networking.ApiService
import ir.staryas.hozurghiyab.utils.*
import kotlinx.android.synthetic.main.activity_user_class.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class UserClass : AppCompatActivity() {

    private lateinit var viewDialog: ViewDialog
    private lateinit var prefManage: PrefManage
    private val tools = Tools()
    private var page = 0
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private val apiClient = ApiClient()
    private lateinit var classesList: MutableList<Class>
    private var userRole = "professor"
    private var userId = 1
    private var userName = "1"
    private val orderBy = "course_id"
    private var courseId = 1
    private var recyclerAdapter = AdapterListUc(this)
    private lateinit var stuAdapter:AdapterListStudents
    private lateinit var studentsList: MutableList<Student>
    private lateinit var bundle:Bundle
    private lateinit var checkDialog:Dialog

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_class)

        initToolbar()
        initComponent()

        swipe_container_uc.setOnRefreshListener {
            initToolbar()
            initComponent()
            swipe_container_uc.isRefreshing = false
        }

        btnUcTryAgain.setOnClickListener {
            initToolbar()
            initComponent()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initToolbar() {
        val intent = intent
        bundle = intent.extras!!

        val toolbar: Toolbar = findViewById(R.id.ucToolbar)
        setSupportActionBar(toolbar)
        title = bundle.getString("courseName")
        supportActionBar!!.elevation = 0.0f
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon!!.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)
        tools.setSystemBarColor(
            this,
            R.color.colorPrimaryDark
        )
        tools.setSystemBarLight(this)
    }

    private fun initComponent() {
        viewDialog = ViewDialog(this)
        prefManage = PrefManage(this)


        courseId = bundle.getString("courseId")!!.toInt()

        userRole = prefManage.getUserRole()!!
        userId = prefManage.getUserId()!!

        if (userRole == "student"){
            userName = prefManage.getUsername()!!
        }

        classesList = mutableListOf()
        page = 0
        viewDialog.showDialog()
        recyclerAdapter = AdapterListUc(this)
        val layoutManager = LinearLayoutManager(this)

        rvUc.layoutManager = layoutManager
        rvUc.adapter = recyclerAdapter

        try {
            if (userRole == "student"){
                getClasses(userRole, page, userName, orderBy)
            } else {
                getClasses(userRole, page, userId.toString(), orderBy)
            }

        } catch (e: Exception) {

        }

        rvUc.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                page++
                viewDialog.showDialog()
                try {
                    getClasses(userRole, page, userId.toString(), orderBy)
                } catch (e: Exception) {

                }
            }

        })

        recyclerAdapter.setOnItemClickListener(object : AdapterListUc.ClickListener {
            override fun onClick(pos: Int, aView: View) {
                if (isNetworkAvailable(this@UserClass)) {
                    if (userRole == "professor") {
                        showDialogCheckPro(classesList[pos].professorId!!.toInt(),courseId,classesList[pos].classId!!.toInt())
                    }
                } else {
                    toast(getString(R.string.no_internet), this@UserClass)
                }
            }

        })
    }

    private fun getClasses(mode: String, page: Int, userId: String, orderBy: String) {
        val client = apiClient.getClient().create(ApiService::class.java)
        val call = client.getClassesByCourse(mode, page, courseId, userId, orderBy)

        call.enqueue(object : Callback<ClassMsg> {
            override fun onFailure(call: Call<ClassMsg>, t: Throwable) {
                viewDialog.hideDialog()
                llNoItemUc.visibility = View.VISIBLE
                rvUc.visibility = View.GONE
                toast(getString(R.string.network_failure_try), this@UserClass)
            }

            override fun onResponse(call: Call<ClassMsg>, response: Response<ClassMsg>) {
                isLoading = false
                val success = response.body()!!.success
                if (success == 1) {
                    if (response.body()?.classes!!.isNotEmpty()) {
                        viewDialog.hideDialog()
                        llNoItemUc.visibility = View.GONE
                        rvUc.visibility = View.VISIBLE
                        if (response.body()?.classes!!.size < 15) {
                            isLastPage = true
                        }
                        if (page == 0) {
                            classesList = (response.body()!!.classes as MutableList<Class>?)!!
                        } else {
                            classesList.addAll(classesList.lastIndex + 1, response.body()!!.classes!!)
                        }
                        classesList.let { recyclerAdapter.setClassesListItems(it) }

                    } else {
                        viewDialog.hideDialog()
                        rvUc.visibility = View.GONE
                        llNoItemUc.visibility = View.VISIBLE
                    }
                } else {
                    viewDialog.hideDialog()
                    llNoItemUc.visibility = View.VISIBLE
                    rvUc.visibility = View.GONE
                    toast(getString(R.string.network_failure_try), this@UserClass)

                }
            }

        })
    }

    private fun showDialogCheckPro(pId:Int,cId:Int,classId:Int) {
        checkDialog = Dialog(this)
        checkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        checkDialog.setContentView(R.layout.check_stu_dialog)
        checkDialog.setCancelable(true)
        checkDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(checkDialog.window.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val rvStudents:RecyclerView = checkDialog.findViewById(R.id.rvCheckStu)
        studentsList = mutableListOf()

        stuAdapter = AdapterListStudents(this)
        val layoutManager = LinearLayoutManager(this)
        rvStudents.layoutManager = layoutManager
        rvStudents.adapter = stuAdapter

        stuAdapter.setOnItemClickListener(object : AdapterListStudents.ClickListener {
            override fun onClick(pos: Int, aView: View) {

            }

        })

        try {
            viewDialog.showDialog()
            getStudentsByCourse(courseId)
        } catch (e:Exception) {

        }


        (checkDialog.findViewById(R.id.btnConfirmStu) as View).setOnClickListener {
            if (stuAdapter.usernameList.isEmpty()){
                stuAdapter.usernameList.add("null")
            }
            try {
                viewDialog.showDialog()
                checkStudents(pId,cId,classId,stuAdapter.usernameList)
                toast(stuAdapter.usernameList[1],this)
            } catch (e:Exception){

            }
        }

        (checkDialog.findViewById(R.id.bt_close) as ImageButton).setOnClickListener {
            checkDialog.dismiss()
        }

        checkDialog.show()
        checkDialog.window.attributes = lp
    }

    private fun getStudentsByCourse(courseId:Int) {
        val stuClient = apiClient.getClient().create(ApiService::class.java)
        val stuCall = stuClient.getStudentsByCourse(courseId)

        stuCall.enqueue(object : Callback<StudentMsg>{
            override fun onFailure(call: Call<StudentMsg>, t: Throwable) {
                viewDialog.hideDialog()
                checkDialog.dismiss()
                toast(getString(R.string.network_failure_try),this@UserClass)
            }

            override fun onResponse(call: Call<StudentMsg>, response: Response<StudentMsg>) {
                val success = response.body()!!.success
                if (success == 1) {
                    if (response.body()?.students!!.isNotEmpty()) {
                        toast("success",this@UserClass)
                        viewDialog.hideDialog()
                        stuAdapter.setStudentsListItems(response.body()!!.students!!.toMutableList())
                    } else {
                        viewDialog.hideDialog()
                        checkDialog.dismiss()
                        toast("دانشجویی موجود نیست!",this@UserClass)
                    }
                } else {
                    viewDialog.hideDialog()
                    checkDialog.dismiss()
                    toast(getString(R.string.network_failure_try), this@UserClass)
            }

        }})
    }

    private fun checkStudents(pId: Int,cId: Int,classId: Int,sPresent:ArrayList<String>) {
        val csClient = apiClient.getClient().create(ApiService::class.java)
        val csCall = csClient.checkStudents(pId, cId, classId, sPresent)

        csCall.enqueue(object : Callback<Msg>{
            override fun onFailure(call: Call<Msg>, t: Throwable) {
                viewDialog.hideDialog()
                toast(getString(R.string.network_failure_try), this@UserClass)
            }

            override fun onResponse(call: Call<Msg>, response: Response<Msg>) {
                viewDialog.hideDialog()
                val message = response.body()!!.message
                toast(message!!,this@UserClass)
                checkDialog.dismiss()
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}
