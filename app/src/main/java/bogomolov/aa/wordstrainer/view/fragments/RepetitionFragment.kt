package bogomolov.aa.wordstrainer.view.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected

import bogomolov.aa.wordstrainer.R
import bogomolov.aa.wordstrainer.dagger.ViewModelFactory
import bogomolov.aa.wordstrainer.databinding.FragmentRepetitionBinding
import bogomolov.aa.wordstrainer.databinding.FragmentTranslationBinding
import bogomolov.aa.wordstrainer.viewmodel.RepetitionViewModel
import bogomolov.aa.wordstrainer.viewmodel.TranslationViewModel
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
    ): View? {
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

        Log.i("test", "RepetitionFragment onCreateView")

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.toolbar, navController)

        binding.cardView.setOnClickListener {
            showTranslation()
        }

        binding.leftArrow.setOnClickListener {
            doWrong()
        }

        binding.rightArrow.setOnClickListener {
            doRight()
        }

        val detector =
            GestureDetectorCompat(activity, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(event: MotionEvent): Boolean {
                    return true
                }

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

        binding.cardView.setOnTouchListener { v, event ->
            if (event != null) detector.onTouchEvent(event)
            false
        }

        binding.deletWordIcon.setOnClickListener {
            viewModel.deleteWord()
            showNextWord()
        }

        setColor(R.color.neutral)
        showNextWord()

        return binding.root
    }

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
                    showNextWord()
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
                    showNextWord()
                    swipeBlocked = false
                }
            }).setInterpolator(AccelerateInterpolator()).start()
    }

    private fun setColor(color: Int) {
        binding.cardView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                color
            )
        )
    }

    private fun showNextWord() {
        binding.translationText.text = ""
        val word = viewModel.nextWord()
        if (word != null) {
            binding.wordText.text = word.word
            binding.wordMainText.text = word.word
        }else{
            binding.wordText.text = ""
            binding.wordMainText.text = ""
        }
    }

    private fun showTranslation() {
        val word = viewModel.lastWord
        if (word != null) binding.translationText.text = getTranslation(word)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.repetition_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.translationFragmentAction) navController.navigate(R.id.translationFragment)
        if (item.itemId == R.id.settingsFragmentAction) navController.navigate(R.id.settingsFragment)
        return true
        //return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}
