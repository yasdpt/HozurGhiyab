package ir.staryas.hozurghiyab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.staryas.hozurghiyab.R
import ir.staryas.hozurghiyab.model.Course

class AdapterListCourses(val context: Context) : RecyclerView.Adapter<AdapterListCourses.MyViewHolder>() {

    var courseList : MutableList<Course> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_course,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return courseList.size
    }

    lateinit var mClickListener: ClickListener

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(pos: Int, aView: View)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvCourseTitle.text = courseList[position].name
        holder.tvCourseUnit.text = context.getString(R.string.course_unit) + " " + courseList[position].units
        holder.tvCoursePro.text = context.getString(R.string.course_pro) +" "+ courseList[position].professorName
        holder.tvCourseDay.text = context.getString(R.string.course_day) +" "+ courseList[position].day
        holder.tvCourseTime.text = context.getString(R.string.course_time) +" "+ courseList[position].time

    }

    fun setCourseListItems(courseList: MutableList<Course>){
        this.courseList = courseList
        notifyDataSetChanged()
    }



    inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener {

        override fun onClick(v: View) {
            mClickListener.onClick(adapterPosition, v)
        }

        var tvCourseTitle: TextView = itemView!!.findViewById(R.id.tvCourseTitle)
        var tvCourseUnit: TextView = itemView!!.findViewById(R.id.tvCourseUnit)
        var tvCoursePro: TextView = itemView!!.findViewById(R.id.tvCoursePro)
        var tvCourseDay: TextView = itemView!!.findViewById(R.id.tvCourseDay)
        var tvCourseTime: TextView = itemView!!.findViewById(R.id.tvCourseTime)

        init {
            itemView!!.setOnClickListener(this)
        }
    }


}