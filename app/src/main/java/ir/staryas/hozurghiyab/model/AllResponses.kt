package ir.staryas.hozurghiyab.model

import com.google.gson.annotations.SerializedName


class Msg {
    @SerializedName("success")
    val success:Int? = null
    @SerializedName("message")
    val message:String? = null
}

class UsrMsg {
    @SerializedName("success")
    val success:Int? = null
    @SerializedName("message")
    val message:String? = null
    @SerializedName("user")
    val user:List<User>? = null
}

class User {
    @SerializedName("id")
    val userId:String? = null
    @SerializedName("name")
    val userName:String? = null
    @SerializedName("family")
    val userFamily:String? = null
    @SerializedName("username")
    val userUsername:String? = null
}

class CourseMsg {
    @SerializedName("success")
    val success:Int? = null
    @SerializedName("courses")
    val courses:List<Course>? = null
}

class ClassMsg {
    @SerializedName("success")
    val success:Int? = null
    @SerializedName("classes")
    val classes:List<Class>? = null
}

class StudentMsg {
    @SerializedName("success")
    val success:Int? = null
    @SerializedName("students")
    val students:List<Student>? = null
}

class Student {
    @SerializedName("student_id")
    val studentId:String? = null
    @SerializedName("name")
    val studentName:String? = null
    @SerializedName("family")
    val studentFamily:String? = null
    @SerializedName("username")
    val studentUsername:String? = null
}

class Class {
    @SerializedName("class_id")
    val classId:String? = null
    @SerializedName("course_id")
    val courseId:String? = null
    @SerializedName("professor_id")
    val professorId:String? = null
    @SerializedName("date")
    val classDate:String? = null
    @SerializedName("did_attend")
    val didAttend:String? = null
    @SerializedName("professor_name")
    val professorName:String? = null
    @SerializedName("course_name")
    val courseName:String? = null
}

class Course {
    @SerializedName("course_id")
    val courseId:String? = null
    @SerializedName("name")
    val name:String? = null
    @SerializedName("units")
    val units:String? = null
    @SerializedName("professor_id")
    val professorId:String? = null
    @SerializedName("day")
    val day:String? = null
    @SerializedName("time")
    val time:String? = null
    @SerializedName("professor_name")
    val professorName:String? = null
    @SerializedName("created_at")
    val createdAt:String? = null
}