package de.benitozenz.loopra.ui.debug

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.benitozenz.loopra.databinding.ItemTapeCellBinding

class TapeAdapter : RecyclerView.Adapter<TapeAdapter.TapeViewHolder>() {

    private var cells: List<TapeCell> = emptyList()

    fun submitList(newCells: List<TapeCell>) {
        cells = newCells
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TapeViewHolder {
        val binding = ItemTapeCellBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TapeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TapeViewHolder, position: Int) {
        holder.bind(cells[position])
    }

    override fun getItemCount() = cells.size

    inner class TapeViewHolder(private val binding: ItemTapeCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cell: TapeCell) {
            binding.textIndex.text = cell.index.toString()
            binding.textValue.text = cell.value.toString()
            binding.textChar.text = if (cell.value in 32..126) cell.value.toChar().toString() else ""
            binding.root.isSelected = cell.isDataPointer
        }
    }
}
