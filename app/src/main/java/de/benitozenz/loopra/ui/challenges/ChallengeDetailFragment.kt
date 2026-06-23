package de.benitozenz.loopra.ui.challenges

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import de.benitozenz.loopra.databinding.FragmentChallengeDetailBinding
import kotlinx.coroutines.launch

class ChallengeDetailFragment : Fragment() {

    private var _binding: FragmentChallengeDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChallengeDetailViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChallengeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditor()
        setupButtons()
        observeState()
    }

    private fun setupEditor() {
        binding.editCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateCode(s?.toString() ?: "")
            }
        })
    }

    private fun setupButtons() {
        binding.buttonRunTest.setOnClickListener { viewModel.runTest() }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    state.challenge?.let { challenge ->
                        binding.textDescription.text = buildString {
                            appendLine(challenge.description)
                            appendLine()
                            append("Difficulty: ${challenge.difficulty}")
                            append("  •  Best: ≤${challenge.optimalSteps} steps")
                        }
                        if (binding.editCode.text.toString() != challenge.code) {
                            binding.editCode.setText(challenge.code)
                            binding.editCode.setSelection(challenge.code.length.coerceAtLeast(0))
                        }
                    }
                    state.testResult?.let {
                        binding.textTestResult.text = it
                        binding.textTestResult.setTextColor(
                            if (state.testPassed)
                                resources.getColor(android.R.color.holo_green_light, null)
                            else
                                resources.getColor(android.R.color.holo_red_light, null)
                        )
                    }
                    binding.buttonRunTest.isEnabled = !state.isRunning
                    binding.buttonRunTest.text = if (state.isRunning) "…" else "Run Test"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
