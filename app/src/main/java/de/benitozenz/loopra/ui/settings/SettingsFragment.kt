package de.benitozenz.loopra.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import de.benitozenz.loopra.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSliders()
        setupButtons()
        observeState()
    }

    private fun setupSliders() {
        binding.sliderMaxSteps.addOnChangeListener { _, value, _ ->
            viewModel.updateMaxSteps(value.toInt())
        }
        binding.sliderTapeSize.addOnChangeListener { _, value, _ ->
            viewModel.updateTapeSize(value.toInt())
        }
    }

    private fun setupButtons() {
        binding.buttonClearData.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear All Data?")
                .setMessage("This will delete all scripts, challenge progress, and XP. This cannot be undone.")
                .setPositiveButton("Clear") { _, _ -> viewModel.clearAllData() }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.maxSteps.collect { steps ->
                        binding.textMaxSteps.text = "Max steps: ${String.format("%,d", steps)}"
                        binding.sliderMaxSteps.value = steps.toFloat()
                    }
                }
                launch {
                    viewModel.tapeSize.collect { size ->
                        binding.textTapeSize.text = "Tape size: $size cells"
                        binding.sliderTapeSize.value = size.toFloat()
                    }
                }
                launch {
                    viewModel.clearDataResult.collect { result ->
                        if (result != null) {
                            binding.textClearResult.text = result
                            binding.textClearResult.visibility = View.VISIBLE
                            Snackbar.make(binding.root, result, Snackbar.LENGTH_LONG).show()
                            viewModel.dismissResult()
                        }
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
