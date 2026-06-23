package de.benitozenz.loopra.ui.debug

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import de.benitozenz.loopra.R
import de.benitozenz.loopra.databinding.FragmentDebugBinding
import kotlinx.coroutines.launch

class DebugFragment : Fragment() {

    private var _binding: FragmentDebugBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DebugViewModel by viewModels()
    private lateinit var tapeAdapter: TapeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDebugBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTape()
        setupControls()
        observeState()
    }

    private fun setupTape() {
        tapeAdapter = TapeAdapter()
        binding.recyclerTape.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerTape.adapter = tapeAdapter
    }

    private fun setupControls() {
        binding.buttonStep.setOnClickListener { viewModel.step() }
        binding.buttonContinue.setOnClickListener { viewModel.continueExecution() }
        binding.buttonStop.setOnClickListener { viewModel.stop() }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tapeCells.collect { cells ->
                        tapeAdapter.submitList(cells)
                        if (cells.isNotEmpty()) {
                            val dpIndex = cells.indexOfFirst { it.isDataPointer }
                            if (dpIndex >= 0) {
                                binding.recyclerTape.smoothScrollToPosition(dpIndex)
                            }
                        }
                    }
                }
                launch {
                    viewModel.code.collect { code ->
                        val ip = viewModel.currentIp.value
                        if (ip >= 0 && ip < code.length) {
                            val marked = buildString {
                                append(code.take(ip))
                                append("👉")
                                append(if (ip < code.length) code[ip] else ' ')
                                append("👈")
                                append(code.drop(ip + 1))
                            }
                            binding.textCodePreview.text = marked
                        } else {
                            binding.textCodePreview.text = code
                        }
                    }
                }
                launch {
                    viewModel.output.collect { output ->
                        binding.textOutput.text = output
                    }
                }
                launch {
                    viewModel.error.collect { error ->
                        if (error != null) {
                            binding.textOutput.text = "Error: $error"
                        }
                    }
                }
                launch {
                    viewModel.isRunning.collect { running ->
                        binding.buttonStep.isEnabled = !running
                        binding.buttonContinue.isEnabled = !running
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
