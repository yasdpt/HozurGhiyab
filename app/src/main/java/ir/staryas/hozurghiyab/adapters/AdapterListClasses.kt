package ir.staryas.hozurghiyab.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.staryas.doorway.utils.Tools
import ir.staryas.hozurghiyab.R
import ir.staryas.hozurghiyab.model.Class
import saman.zamani.persiandate.PersianDate

class AdapterListClasses(val context: Context) : RecyclerView.Adapter<AdapterListClasses.MyViewHolder>() {

    var classesList : MutableList<Class> = mutableListOf()
    private var classCount:Int = 1
    private val tools = Tools()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterListClasses.MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_class,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return classesList.size
    }

    lateinit var mClickListener: ClickListener

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(pos: Int, aView: View)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvClassTitle.text = "جلسه $classCount"
        val pdate = PersianDate()
        holder.tvClassStatus.text = "وضعیت کلاس: " + tools.getTimeDifference(pdate,classesList[position].classDate!!)
        val classDate = classesList[position].classDate!!.split(" ")
        val cDate = classDate[0].split("-")
        val cTime = classDate[1].split(":")
        holder.tvClassDate.text = "تاریخ کلاس: " + cDate[0] + "/" + cDate[1] + "/" + cDate[2]
        holder.tvClassTime.text = "ساعت کلاس: " + cTime[0] + ":" + cTime[1]
        classCount++
    }

    fun setClassesListItems(classesList: MutableList<Class>){
        this.classesList = classesList
        notifyDataSetChanged()
    }



    inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener {

        override fun onClick(v: View) {
            mClickListener.onClick(adapterPosition, v)
        }

        var tvClassTitle: TextView = itemView!!.findViewById(R.id.tvClassTitle)
        var tvClassDate: TextView = itemView!!.findViewById(R.id.tvClassDate)
        var tvClassStatus: TextView = itemView!!.findViewById(R.id.tvClassStatus)
        var tvClassTime: TextView = itemView!!.findViewById(R.id.tvClassTime)

        init {
            itemView!!.setOnClickListener(this)
        }
    }


}