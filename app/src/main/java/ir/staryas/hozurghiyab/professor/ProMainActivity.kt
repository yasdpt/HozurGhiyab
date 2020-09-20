package ir.staryas.hozurghiyab.professor

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import ir.staryas.doorway.utils.Tools
import ir.staryas.hozurghiyab.LoginActivity
import ir.staryas.hozurghiyab.R
import ir.staryas.hozurghiyab.UserClass
import ir.staryas.hozurghiyab.adapters.AdapterListCourses
import ir.staryas.hozurghiyab.admin.AdminClasses
import ir.staryas.hozurghiyab.model.Course
import ir.staryas.hozurghiyab.model.CourseMsg
import ir.staryas.hozurghiyab.networking.ApiClient
import ir.staryas.hozurghiyab.networking.ApiService
import ir.staryas.hozurghiyab.utils.*
import kotlinx.android.synthetic.main.activity_pro_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ProMainActivity : AppCompatActivity() {

    private lateinit var viewDialog: ViewDialog
    private lateinit var prefManage: PrefManage
    private val tools = Tools()
    private var page = 0
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private val apiClient = ApiClient()
    private lateinit var courseList: MutableList<Course>
    private var userRole = "professor"
    private var userId = 1
    private val orderBy = "course_id"
    private lateinit var menuItem: MenuItem
    private var recyclerAdapter = AdapterListCourses(this)


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_main)

        initToolbar()
        initComponent()

        swipe_container_pro.setOnRefreshListener {
            initToolbar()
            initComponent()
            swipe_container_pro.isRefreshing = false
        }

        btnProTryAgain.setOnClickListener {
            initToolbar()
            initComponent()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.proToolbar)
        setSupportActionBar(toolbar)
        title = "درس ها"
        supportActionBar!!.elevation = 0.0f
        tools.setSystemBarColor(this,
            R.color.colorPrimaryDark
        )
        tools.setSystemBarLight(this)
    }

    private fun initComponent() {
        viewDialog = ViewDialog(this)
        prefManage = PrefManage(this)

        userRole = prefManage.getUserRole()!!
        userId = prefManage.getUserId()!!

        courseList = mutableListOf()
        page = 0
        viewDialog.showDialog()
        recyclerAdapter = AdapterListCourses(this)
        val layoutManager = LinearLayoutManager(this)

        rvPro.layoutManager = layoutManager
        rvPro.adapter = recyclerAdapter

        try {
            getCourses(userRole,page,userId.toString(),orderBy)
        } catch (e: Exception){

        }

        rvPro.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
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
                    getCourses(userRole,page,userId.toString(),orderBy)
                } catch (e: Exception){

                }
            }

        })

        recyclerAdapter.setOnItemClickListener(object : AdapterListCourses.ClickListener{
            override fun onClick(pos: Int, aView: View) {
                if (isNetworkAvailable(this@ProMainActivity)) {
                    val intent = Intent(this@ProMainActivity, UserClass::class.java)
                    intent.putExtra("courseId", recyclerAdapter.courseList[pos].courseId)
                    intent.putExtra("courseName", recyclerAdapter.courseList[pos].name)
                    startActivity(intent)
                } else {
                    toast(getString(R.string.no_internet), this@ProMainActivity)
                }
            }

        })
    }

    private fun getCourses(mode:String, page:Int, userId:String, orderBy:String){
        val client = apiClient.getClient().create(ApiService::class.java)
        val call = client.getCourses(mode,page,userId,orderBy)

        call.enqueue(object : Callback<CourseMsg> {
            override fun onFailure(call: Call<CourseMsg>, t: Throwable) {
                viewDialog.hideDialog()
                llNoItemPro.visibility = View.VISIBLE
                rvPro.visibility = View.GONE
                toast(getString(R.string.network_failure_try),this@ProMainActivity)
            }

            override fun onResponse(call: Call<CourseMsg>, response: Response<CourseMsg>) {
                isLoading = false
                val success = response.body()!!.success
                if (success == 1){
                    if(response.body()?.courses!!.isNotEmpty()){
                        viewDialog.hideDialog()
                        llNoItemPro.visibility = View.GONE
                        rvPro.visibility = View.VISIBLE
                        if (response.body()?.courses!!.size < 15)
                        {
                            isLastPage = true
                        }
                        if (page==0){
                            courseList = (response.body()!!.courses as MutableList<Course>?)!!
                        } else{
                            courseList.addAll(courseList.lastIndex+1, response.body()!!.courses!!)
                        }
                        courseList.let { recyclerAdapter.setCourseListItems(it) }

                    } else {
                        viewDialog.hideDialog()
                        rvPro.visibility = View.GONE
                        llNoItemPro.visibility = View.VISIBLE
                    }
                } else {
                    viewDialog.hideDialog()
                    llNoItemPro.visibility = View.VISIBLE
                    rvPro.visibility = View.GONE
                    toast(getString(R.string.network_failure_try),this@ProMainActivity)

                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_log_out, menu)
        tools.changeMenuIconColor(menu, resources.getColor(R.color.white))
        menuItem = menu.findItem(R.id.action_logout)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            prefManage.setIsUserLoggedIn(false)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
