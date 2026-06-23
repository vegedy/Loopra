package de.benitozenz.loopra.ui.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import de.benitozenz.loopra.R
import de.benitozenz.loopra.databinding.FragmentChallengeListBinding
import kotlinx.coroutines.launch

class ChallengeListFragment : Fragment() {

    private var _binding: FragmentChallengeListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChallengeListViewModel by viewModels()
    private lateinit var adapter: ChallengeListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChallengeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeChallenges()
    }

    private fun setupRecyclerView() {
        adapter = ChallengeListAdapter { item ->
            val bundle = Bundle().apply { putLong("challengeId", item.challenge.id) }
            findNavController().navigate(R.id.action_challenge_list_to_detail, bundle)
        }
        binding.recyclerChallenges.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerChallenges.adapter = adapter
    }

    private fun observeChallenges() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.challengesWithProgress.collect { list ->
                    adapter.submitList(list)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
