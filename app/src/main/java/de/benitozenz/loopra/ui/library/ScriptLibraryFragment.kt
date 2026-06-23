package de.benitozenz.loopra.ui.library

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
import de.benitozenz.loopra.databinding.FragmentScriptLibraryBinding
import kotlinx.coroutines.launch

class ScriptLibraryFragment : Fragment() {

    private var _binding: FragmentScriptLibraryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScriptLibraryViewModel by viewModels()
    private lateinit var adapter: ScriptLibraryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScriptLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeScripts()
    }

    private fun setupRecyclerView() {
        adapter = ScriptLibraryAdapter(
            onItemClick = { script ->
                val bundle = Bundle().apply { putLong("scriptId", script.id) }
                findNavController().navigate(R.id.EditorFragment, bundle)
            },
            onDeleteClick = { script ->
                viewModel.deleteScript(script)
            }
        )
        binding.recyclerScripts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerScripts.adapter = adapter
    }

    private fun observeScripts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.scripts.collect { scripts ->
                    adapter.submitList(scripts)
                    binding.textEmpty.visibility = if (scripts.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
