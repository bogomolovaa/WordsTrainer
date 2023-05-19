package bogomolov.aa.wordstrainer.features.repetition

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.WordsTrainerApplication
import bogomolov.aa.wordstrainer.dagger.ViewModelFactory
import bogomolov.aa.wordstrainer.databinding.FragmentRepetitionBinding
import javax.inject.Inject

class RepetitionFragment : Fragment() {
    @Inject
    internal lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: RepetitionViewModel by viewModels { viewModelFactory }
    private lateinit var navController: NavController
    private lateinit var binding: FragmentRepetitionBinding
    private var swipeBlocked = false

    override fun onAttach(context: Context) {
        (requireActivity().application as WordsTrainerApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRepetitionBinding.inflate(inflater, container, false).also { binding = it }.root

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        navController = findNavController()
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
        viewModel.counterLiveData.observe(viewLifecycleOwner) {
            binding.counterText.text = it.toString()
        }
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
        binding.translationText.text =
            if (word?.translation != null) SpannableString(word.translation) else ""
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