package de.benitozenz.loopra.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.benitozenz.loopra.databinding.ItemBadgeBinding

class BadgeAdapter : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    private var badges: List<Badge> = emptyList()

    fun submitList(newBadges: List<Badge>) {
        badges = newBadges
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(badges[position])
    }

    override fun getItemCount() = badges.size

    inner class BadgeViewHolder(private val binding: ItemBadgeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(badge: Badge) {
            binding.textBadgeIcon.text = badge.icon
            binding.textBadgeName.text = badge.name
            val alpha = if (badge.earned) 1.0f else 0.3f
            binding.textBadgeIcon.alpha = alpha
            binding.textBadgeName.alpha = alpha
        }
    }
}
