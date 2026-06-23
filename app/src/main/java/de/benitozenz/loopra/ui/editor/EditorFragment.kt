package de.benitozenz.loopra.ui.editor

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
import androidx.navigation.fragment.findNavController
import de.benitozenz.loopra.databinding.FragmentEditorBinding
import kotlinx.coroutines.launch

class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditorViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
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
        binding.buttonRun.setOnClickListener { viewModel.runCode() }
        binding.buttonClear.setOnClickListener { viewModel.clearOutput() }
        binding.buttonDebug.setOnClickListener {
            val scriptId = viewModel.script.value?.id ?: return@setOnClickListener
            val bundle = Bundle().apply { putLong("scriptId", scriptId) }
            findNavController().navigate(
                de.benitozenz.loopra.R.id.action_editor_to_debug,
                bundle
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.code.collect { code ->
                        if (binding.editCode.text.toString() != code) {
                            binding.editCode.setText(code)
                            binding.editCode.setSelection(code.length.coerceAtLeast(0))
                        }
                    }
                }
                launch {
                    viewModel.output.collect { output ->
                        binding.textOutputContent.text = output
                    }
                }
                launch {
                    viewModel.isRunning.collect { running ->
                        binding.buttonRun.isEnabled = !running
                        binding.buttonRun.text =
                            if (running) "…" else getString(de.benitozenz.loopra.R.string.action_run)
                    }
                }
                launch {
                    viewModel.error.collect { error ->
                        if (error != null) {
                            binding.textOutputContent.text = "Error: $error"
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
