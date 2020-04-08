package bogomolov.aa.wordstrainer.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bogomolov.aa.wordstrainer.databinding.SheetLayoutBinding
import bogomolov.aa.wordstrainer.model.GoogleSheet

class GoogleSheetsAdapter(private val helper: AdapterHelper<GoogleSheet, SheetLayoutBinding> = AdapterHelper()) :
    RecyclerView.Adapter<AdapterHelper<GoogleSheet, SheetLayoutBinding>.VH>(),
    AdapterSelectable<GoogleSheet, SheetLayoutBinding> {
    private val sheets = ArrayList<GoogleSheet>()

    init {
        helper.adapter = this
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterHelper<GoogleSheet, SheetLayoutBinding>.VH {
        val binding =
            SheetLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val cv = binding.cardView
        return helper.VH(cv, cv, binding)
    }

    override fun onBindViewHolder(
        holder: AdapterHelper<GoogleSheet, SheetLayoutBinding>.VH,
        position: Int
    ) = helper.onBindViewHolder(holder, position)

    override fun getItem(position: Int) = sheets[position]

    override fun bind(item: GoogleSheet?, binding: SheetLayoutBinding) {
        binding.sheetName.text = item?.name ?: ""
    }

    override fun getItemCount() = sheets.size

    fun submitList(list: List<GoogleSheet>) {
        sheets.clear()
        sheets.addAll(list)
        notifyDataSetChanged()
    }

    override fun getId(item: GoogleSheet): Long = 0

}