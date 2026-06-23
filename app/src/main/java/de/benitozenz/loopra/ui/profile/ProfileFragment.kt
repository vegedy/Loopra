package de.benitozenz.loopra.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import de.benitozenz.loopra.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var badgeAdapter: BadgeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        badgeAdapter = BadgeAdapter()
        binding.recyclerBadges.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.recyclerBadges.adapter = badgeAdapter

        observeProfile()
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileState.collect { state ->
                    binding.textLevelBadge.text = state.level.toString()
                    binding.seekXp.progress = state.xp
                    binding.textXpDetail.text = "${state.xp} / ${state.xpForNextLevel} XP"
                    binding.textStreakValue.text = "${state.streak}"
                    binding.textScriptsCount.text = "\uD83D\uDCC4  Scripts: ${state.scriptsCreated}"
                    binding.textChallengesCount.text = "\uD83C\uDF1F  Challenges: ${state.challengesCompleted}"
                    binding.textDebugCount.text = "\uD83D\uDD0D  Steps: ${state.debugSteps}"
                    badgeAdapter.submitList(state.badges)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
