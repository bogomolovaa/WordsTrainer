package bogomolov.aa.wordstrainer.features.google_sheets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bogomolov.aa.wordstrainer.databinding.SheetLayoutBinding
import bogomolov.aa.wordstrainer.domain.GoogleSheet

class GoogleSheetsAdapter(private val onClick: (GoogleSheet) -> Unit) :
    RecyclerView.Adapter<GoogleSheetsAdapter.VH>() {
    private val sheets = ArrayList<GoogleSheet>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            SheetLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding.root, binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(sheets[position])
    }

    override fun getItemCount() = sheets.size

    fun submitList(list: List<GoogleSheet>) {
        sheets.clear()
        sheets.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(viewHolder: View, private val binding: SheetLayoutBinding) :
        RecyclerView.ViewHolder(viewHolder), View.OnClickListener {

        init {
            viewHolder.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (adapterPosition == RecyclerView.NO_POSITION) return
            val item = sheets[adapterPosition]
            onClick.invoke(item)
        }

        fun bind(item: GoogleSheet?){
            binding.sheetName.text = item?.name ?: ""
        }
    }
}