package ir.staryas.hozurghiyab.networking

import ir.staryas.hozurghiyab.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Login
    @FormUrlEncoded
    @POST("Login.php")
    fun login(@Field("mode") mode: String,@Field("username") username:String,
              @Field("password") password:String): Call<UsrMsg>


    @FormUrlEncoded
    @POST("ManageProfessors.php")
    fun checkProfessor(@Field("pid") pId:Int,@Field("cid") cId:Int, @Field("classid") classId:Int,
            @Field("did_attend") didAttend:Int): Call<Msg>

    @FormUrlEncoded
    @POST("ManageStudents.php")
    fun checkStudents(@Field("pid") pId:Int,@Field("cid") cId:Int, @Field("classid") classId:Int,
                      @Field("spresent[]") sPresent:ArrayList<String>): Call<Msg>

    @FormUrlEncoded
    @POST("ManageClasses.php")
    fun manageClasses(@Field("pid") pId:Int,@Field("cid") cId:Int,@Field("cdate") cDate:String): Call<Msg>

    @GET("GetCourses.php")
    fun getCourses(@Query("mode") mode:String, @Query("page") page:Int, @Query("userid") userId:String,
                   @Query("orderby") orderBy:String): Call<CourseMsg>

    @GET("GetClassesByC.php")
    fun getClassesByCourse(@Query("mode") mode:String, @Query("page") page:Int, @Query("courseid") courseId:Int, @Query("userid") userId:String,
                   @Query("orderby") orderBy:String): Call<ClassMsg>

    @GET("GetStudentsByC.php")
    fun getStudentsByCourse(@Query("courseid") courseId:Int) : Call<StudentMsg>

}