package ir.staryas.hozurghiyab.admin

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar
import ir.staryas.doorway.utils.Tools
import ir.staryas.hozurghiyab.BuildConfig
import ir.staryas.hozurghiyab.R
import ir.staryas.hozurghiyab.adapters.AdapterListClasses
import ir.staryas.hozurghiyab.model.Class
import ir.staryas.hozurghiyab.model.ClassMsg
import ir.staryas.hozurghiyab.model.Msg
import ir.staryas.hozurghiyab.networking.ApiClient
import ir.staryas.hozurghiyab.networking.ApiService
import ir.staryas.hozurghiyab.utils.*

import kotlinx.android.synthetic.main.activity_admin_classes.*
import kotlinx.android.synthetic.main.check_pro_dialog.*
import kotlinx.android.synthetic.main.content_admin_classes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class AdminClasses : AppCompatActivity(), DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {


    private lateinit var viewDialog: ViewDialog
    private lateinit var prefManage: PrefManage
    private val tools = Tools()
    private var page = 0
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false
    private val apiClient = ApiClient()
    private lateinit var classesList: MutableList<Class>
    private var userRole = "admin"
    private var userId = 1
    private val orderBy = "course_id"
    private var courseId = 1
    private var pId = 1
    private var dateSet = false
    private var timeSet = false
    private var didAttend = 0
    private lateinit var btnSelectDate:Button
    private lateinit var btnSelectTime:Button
    private lateinit var checkDialog:Dialog
    private lateinit var createClassDialog:Dialog
    private var recyclerAdapter = AdapterListClasses(this)
    private lateinit var bundle:Bundle

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_classes)

        initToolbar()
        initComponent()

        swipe_container_c.setOnRefreshListener {
            initToolbar()
            initComponent()
            swipe_container_c.isRefreshing = false
        }

        btnCTryAgain.setOnClickListener {
            initToolbar()
            initComponent()
        }

        fab.setOnClickListener { view ->
            showDialogNewClass(pId,courseId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initToolbar() {
        val intent = intent
        bundle = intent.extras!!

        val toolbar: Toolbar = findViewById(R.id.cToolbar)
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
        pId = bundle.getString("proId")!!.toInt()
        userRole = prefManage.getUserRole()!!
        userId = prefManage.getUserId()!!

        classesList = mutableListOf()
        page = 0
        viewDialog.showDialog()
        recyclerAdapter = AdapterListClasses(this)
        val layoutManager = LinearLayoutManager(this)

        rvClass.layoutManager = layoutManager
        rvClass.adapter = recyclerAdapter

        try {
            getClasses(userRole, page, userId.toString(), orderBy)
        } catch (e: Exception) {

        }

        rvClass.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
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

        recyclerAdapter.setOnItemClickListener(object : AdapterListClasses.ClickListener {
            override fun onClick(pos: Int, aView: View) {
                if (isNetworkAvailable(this@AdminClasses)) {
                    showDialogCheckPro(pId,recyclerAdapter.classesList[pos].courseId!!.toInt(),recyclerAdapter.classesList[pos].classId!!.toInt())

                } else {
                    toast(getString(R.string.no_internet), this@AdminClasses)
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
                llNoItemC.visibility = View.VISIBLE
                rvClass.visibility = View.GONE
                toast(getString(R.string.network_failure_try), this@AdminClasses)
            }

            override fun onResponse(call: Call<ClassMsg>, response: Response<ClassMsg>) {
                isLoading = false
                val success = response.body()!!.success
                if (success == 1) {
                    if (response!!.body()?.classes!!.size!! != 0) {
                        viewDialog.hideDialog()
                        llNoItemC.visibility = View.GONE
                        rvClass.visibility = View.VISIBLE
                        if (response.body()?.classes!!.size!! < 15) {
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
                        rvClass.visibility = View.GONE
                        llNoItemC.visibility = View.VISIBLE
                    }
                } else {
                    viewDialog.hideDialog()
                    llNoItemC.visibility = View.VISIBLE
                    rvClass.visibility = View.GONE
                    toast(getString(R.string.network_failure_try), this@AdminClasses)

                }
            }

        })
    }


    private fun showDialogCheckPro(pId:Int,cId:Int,classId:Int) {
        checkDialog = Dialog(this)
        checkDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        checkDialog.setContentView(R.layout.check_pro_dialog)
        checkDialog.setCancelable(true)
        checkDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(checkDialog.window.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val chbProStatus = checkDialog.findViewById(R.id.chbProStatus) as CheckBox

        (checkDialog.findViewById(R.id.btnConfirmPro) as View).setOnClickListener {
            didAttend = if (chbProStatus.isChecked) 1 else 0
            try {
                viewDialog.showDialog()
                checkProfessor(pId, cId, classId, didAttend)
            } catch (e:Exception){
                checkDialog.dismiss()
            }
        }

        (checkDialog.findViewById(R.id.bt_close) as ImageButton).setOnClickListener {
            checkDialog.dismiss()
        }

        checkDialog.show()
        checkDialog.window.attributes = lp
    }

    private fun showDialogNewClass(pId:Int,cId:Int) {
        createClassDialog = Dialog(this)
        createClassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        createClassDialog.setContentView(R.layout.create_class_dialog)
        createClassDialog.setCancelable(true)
        createClassDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(createClassDialog.window.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        btnSelectDate = createClassDialog.findViewById(R.id.btnSelectDate)

        btnSelectTime = createClassDialog.findViewById(R.id.btnSelectTime)

        btnSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        btnSelectTime.setOnClickListener {
            showTimePickerDialog()
        }


        (createClassDialog.findViewById(R.id.btnCreateClass) as View).setOnClickListener {
            if (dateSet && timeSet){
                val cDate = btnSelectDate.text.toString().replace("/","-") + " " + btnSelectTime.text.toString()
                try {
                    viewDialog.showDialog()
                    createClass(pId,cId,cDate)
                } catch (e:Exception){
                    toast(e.message!!,this)
                }
            } else {
                toast("لطفا تاریخ و ساعت را انتخاب کنید",this)
            }

        }

        (createClassDialog.findViewById(R.id.bt_close) as ImageButton).setOnClickListener {
            createClassDialog.dismiss()
        }

        createClassDialog.show()
        createClassDialog.window.attributes = lp
    }

    private fun checkProfessor(pId: Int,cId: Int,classId: Int,didAttend:Int){
        val proClient = apiClient.getClient().create(ApiService::class.java)
        val proCall = proClient.checkProfessor(pId,cId, classId, didAttend)

        proCall.enqueue(object : Callback<Msg>{
            override fun onFailure(call: Call<Msg>, t: Throwable) {
                viewDialog.hideDialog()
                toast(getString(R.string.network_failure_try), this@AdminClasses)
            }

            override fun onResponse(call: Call<Msg>, response: Response<Msg>) {
                viewDialog.hideDialog()
                val message = response.body()!!.message
                toast(message!!,this@AdminClasses)
                checkDialog.dismiss()
            }

        })
    }

    private fun createClass(pId: Int,cId: Int,cDate:String){
        val cClient = apiClient.getClient().create(ApiService::class.java)
        val cCall = cClient.manageClasses(pId,cId,cDate)

        cCall.enqueue(object : Callback<Msg>{
            override fun onFailure(call: Call<Msg>, t: Throwable) {
                viewDialog.hideDialog()
                toast(getString(R.string.network_failure_try), this@AdminClasses)
            }

            override fun onResponse(call: Call<Msg>, response: Response<Msg>) {
                viewDialog.hideDialog()
                val message = response.body()!!.message
                toast(message!!,this@AdminClasses)
                createClassDialog.dismiss()
                initComponent()
            }

        })
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        dateSet = true
        var month = monthOfYear
        month++
        val mo = if (month<10) "0$month" else month.toString()
        val day = if (dayOfMonth<10) "0$dayOfMonth" else dayOfMonth.toString()


        btnSelectDate.text = "$year/$mo/$day"
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int) {
        timeSet = true
        var min:String = "00"
        var hour:String = "00"
        min = if (minute<10) "0$minute" else minute.toString()
        hour = if (hourOfDay<10) "0$hourOfDay" else hourOfDay.toString()


        btnSelectTime.text = "$hour:$min:00"
    }

    private fun showDatePickerDialog(){
        val persianCalendar = PersianCalendar()
        val datePickerDialog:DatePickerDialog = DatePickerDialog.newInstance(this
            ,persianCalendar.persianYear,persianCalendar.persianMonth,persianCalendar.persianDay)
        datePickerDialog.show(fragmentManager,"Datepickerdialog")
    }

    private fun showTimePickerDialog(){
        val timePickerDialog:TimePickerDialog = TimePickerDialog.newInstance(this,12,0,true)
        timePickerDialog.show(fragmentManager,"Timepickerdialog")
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
