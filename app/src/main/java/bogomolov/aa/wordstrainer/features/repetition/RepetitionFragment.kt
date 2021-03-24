package bogomolov.aa.wordstrainer.features.repetition

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.dagger.ViewModelFactory
import bogomolov.aa.wordstrainer.databinding.FragmentRepetitionBinding
import bogomolov.aa.wordstrainer.features.translation.getTranslationText
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class RepetitionFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: RepetitionViewModel by viewModels { viewModelFactory }
    private lateinit var navController: NavController
    private lateinit var binding: FragmentRepetitionBinding
    private var swipeBlocked = false

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_repetition,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        binding.cardView.setOnClickListener { showTranslation() }
        binding.leftArrow.setOnClickListener { doWrong() }
        binding.rightArrow.setOnClickListener { doRight() }
        val gestureDetector = createGestureDetector()
        binding.cardView.setOnTouchListener { _, event ->
            if (event != null) gestureDetector.onTouchEvent(event)
            false
        }

        setColor(R.color.neutral)

        viewModel.nextWordLiveData.observe(viewLifecycleOwner) { word ->
            binding.translationText.text = ""
            if (word != null) {
                binding.wordText.text = word.word
                binding.wordMainText.text = word.word
            } else {
                binding.wordText.text = ""
                binding.wordMainText.text = ""
            }
        }

        return binding.root
    }

    private fun createGestureDetector() =
        GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(event: MotionEvent) = true

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (!swipeBlocked) {
                    if (distanceX < -20) doRight()
                    if (distanceX > 20) doWrong()
                }
                return true
            }
        })

    private fun getScreenWidth(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    private fun doRight() {
        swipeBlocked = true
        setColor(R.color.right_color)
        binding.cardView.animate().translationX(getScreenWidth().toFloat()).setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(var1: Animator) {
                    setColor(R.color.neutral)
                    binding.cardView.translationX = 0f
                    viewModel.right()
                    swipeBlocked = false
                }
            }).setInterpolator(AccelerateInterpolator()).start()
    }

    private fun doWrong() {
        swipeBlocked = true
        setColor(R.color.wrong_color)
        binding.cardView.animate().translationX(-getScreenWidth().toFloat()).setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(var1: Animator) {
                    setColor(R.color.neutral)
                    binding.cardView.translationX = 0f
                    viewModel.wrong()
                    swipeBlocked = false
                }
            }).setInterpolator(AccelerateInterpolator()).start()
    }

    private fun setColor(color: Int) {
        binding.cardView.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    private fun showTranslation() {
        val word = viewModel.nextWordLiveData.value
        binding.translationText.text = getTranslationText(word) ?: ""
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.repetition_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.translationFragmentAction) navController.navigate(R.id.translationFragment)
        if (item.itemId == R.id.settingsFragmentAction) navController.navigate(R.id.settingsFragment)
        return true
    }
}