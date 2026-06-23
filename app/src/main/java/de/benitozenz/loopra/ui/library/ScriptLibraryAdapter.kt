package de.benitozenz.loopra.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.benitozenz.loopra.data.model.ScriptEntity
import de.benitozenz.loopra.databinding.ItemScriptBinding

class ScriptLibraryAdapter(
    private val onItemClick: (ScriptEntity) -> Unit,
    private val onDeleteClick: (ScriptEntity) -> Unit
) : ListAdapter<ScriptEntity, ScriptLibraryAdapter.ScriptViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScriptViewHolder {
        val binding = ItemScriptBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScriptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScriptViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScriptViewHolder(private val binding: ItemScriptBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(script: ScriptEntity) {
            binding.textTitle.text = script.title
            binding.textCode.text = script.code.take(80).replace("\n", " ")
            binding.textDate.text = formatTimestamp(script.updatedAt)
            binding.root.setOnClickListener { onItemClick(script) }
            binding.buttonDelete.setOnClickListener { onDeleteClick(script) }
        }

        private fun formatTimestamp(millis: Long): String {
            val sdf = java.text.SimpleDateFormat("dd.MM.yy HH:mm", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(millis))
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ScriptEntity>() {
        override fun areItemsTheSame(old: ScriptEntity, new: ScriptEntity) = old.id == new.id
        override fun areContentsTheSame(old: ScriptEntity, new: ScriptEntity) = old == new
    }
}
