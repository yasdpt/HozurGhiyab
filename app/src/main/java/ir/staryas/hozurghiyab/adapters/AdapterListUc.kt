package ir.staryas.hozurghiyab.adapters

import android.annotation.SuppressLint
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

class AdapterListUc(val context: Context) : RecyclerView.Adapter<AdapterListUc.MyViewHolder>() {

    var classesList : MutableList<Class> = mutableListOf()
    private var classCount:Int = 1
    private val tools = Tools()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterListUc.MyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_class,parent,false)
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


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.tvUCTitle.text = "جلسه $classCount"
        val pdate = PersianDate()
        holder.tvUCStatus.text = "وضعیت کلاس: " + tools.getTimeDifference(pdate,classesList[position].classDate!!)
        val classDate = classesList[position].classDate!!.split(" ")
        val cDate = classDate[0].split("-")
        val cTime = classDate[1].split(":")
        holder.tvUCDate.text = "تاریخ کلاس: " + cDate[0] + "/" + cDate[1] + "/" + cDate[2]
        holder.tvUCTime.text = "ساعت کلاس: " + cTime[0] + ":" + cTime[1]
        val type = if (classesList[position].didAttend=="1") "حاضر" else "غایب"
        holder.tvUCUStatus.text = "وضعیت حضور: $type"
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

        var tvUCTitle: TextView = itemView!!.findViewById(R.id.tvUClassTitle)
        var tvUCDate: TextView = itemView!!.findViewById(R.id.tvUCDate)
        var tvUCStatus: TextView = itemView!!.findViewById(R.id.tvUCStatus)
        var tvUCTime: TextView = itemView!!.findViewById(R.id.tvUCTime)
        var tvUCUStatus: TextView = itemView!!.findViewById(R.id.tvUCUStatus)

        init {
            itemView!!.setOnClickListener(this)
        }
    }


}