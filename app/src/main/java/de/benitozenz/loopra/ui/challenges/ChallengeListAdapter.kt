package de.benitozenz.loopra.ui.challenges

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.benitozenz.loopra.R
import de.benitozenz.loopra.databinding.ItemChallengeBinding

class ChallengeListAdapter(
    private val onItemClick: (ChallengeWithProgress) -> Unit
) : ListAdapter<ChallengeWithProgress, ChallengeListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChallengeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemChallengeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChallengeWithProgress) {
            val challenge = item.challenge
            binding.textTitle.text = challenge.title
            binding.textDescription.text = challenge.description.take(120) + "…"

            val difficultyBg = when (challenge.difficulty) {
                "EASY" -> ContextCompat.getColor(binding.root.context, R.color.success)
                "MEDIUM" -> ContextCompat.getColor(binding.root.context, R.color.neon_amber)
                "HARD" -> ContextCompat.getColor(binding.root.context, R.color.error)
                else -> Color.GRAY
            }
            binding.textDifficulty.text = challenge.difficulty
            binding.textDifficulty.setBackgroundColor(difficultyBg)

            val stars = item.progress?.stars ?: 0
            binding.textStars.text = "\u2605".repeat(stars).padEnd(3, '\u2606')

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ChallengeWithProgress>() {
        override fun areItemsTheSame(old: ChallengeWithProgress, new: ChallengeWithProgress) =
            old.challenge.id == new.challenge.id

        override fun areContentsTheSame(old: ChallengeWithProgress, new: ChallengeWithProgress) =
            old == new
    }
}
