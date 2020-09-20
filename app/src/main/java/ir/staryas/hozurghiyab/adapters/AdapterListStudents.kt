package ir.staryas.hozurghiyab.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.staryas.doorway.utils.Tools
import ir.staryas.hozurghiyab.R
import ir.staryas.hozurghiyab.model.Class
import ir.staryas.hozurghiyab.model.Student
import saman.zamani.persiandate.PersianDate

class AdapterListStudents(val context: Context) : RecyclerView.Adapter<AdapterListStudents.MyViewHolder>() {

    var studentsList : MutableList<Student> = mutableListOf()
    var usernameList : ArrayList<String> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterListStudents.MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_check_stu,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return studentsList.size
    }

    lateinit var mClickListener: ClickListener

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(pos: Int, aView: View)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvStuName.text = studentsList[position].studentName + " " + studentsList[position].studentFamily!!
        holder.chbStu.id = position
        holder.chbStu.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                usernameList.add(studentsList[position].studentUsername!!)
            } else {
                usernameList.remove(studentsList[position].studentUsername!!)
            }
        }

    }

    fun setStudentsListItems(studentsList: MutableList<Student>){
        this.studentsList = studentsList
        notifyDataSetChanged()
    }




    inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener {

        override fun onClick(v: View) {
            mClickListener.onClick(adapterPosition, v)
        }

        var tvStuName: TextView = itemView!!.findViewById(R.id.tvStuName)
        var chbStu: CheckBox = itemView!!.findViewById(R.id.chbStudent)

        init {
            itemView!!.setOnClickListener(this)
        }
    }


}