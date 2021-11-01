package com.igenius.androidstories.app

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.igenius.androidstories.app.data.StoriesFolder
import com.igenius.androidstories.app.data.ViewStory
import com.igenius.androidstories.app.list.StoriesAdapter
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.igenius.androidstories.app.databinding.StoriesActivityBinding
import com.igenius.androidstories.app.story.StoryDetailsConfiguration
import com.igenius.androidstories.app.story.StoryDetailsFragment

private const val DETAILS_FRAGMENT_TAG = "details_fragment"

class StoriesActivity : AppCompatActivity() {

    private lateinit var binding: StoriesActivityBinding
    private val viewModel: StoriesActivityViewModel by viewModels()

    private var detailsFragment: StoryDetailsFragment?
        get() = supportFragmentManager.retrieveFragment(DETAILS_FRAGMENT_TAG)
        set(value) = setCardVisibility(value?.let { true } ?: false) {
            if (detailsFragment?.storyId != value?.storyId)
                supportFragmentManager.commitFragment(
                    R.id.story_details_placeholder,
                    DETAILS_FRAGMENT_TAG,
                    value
                )
        }

    private val canChangeFullView
        get() =
            resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                    resources.getBoolean(R.bool.isTablet)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StoriesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as StoriesApp).let {
            binding.toolbar.title = it.appName
            viewModel.fetchRootFolder(it.storiesProvider)
        }

        supportActionBar?.hide()

        val adapter = StoriesAdapter {
            when (it) {
                is ViewStory -> viewModel.toggleStory(it)
                is StoriesFolder -> viewModel.toggleFolder(it)
            }
        }
        binding.storiesList.adapter = adapter

        viewModel.showingListLiveData.observe(this) {
            adapter.submitList(it)
        }

        viewModel.selectedStoryLiveData.observe(this) {
            detailsFragment = it?.let {
                StoryDetailsFragment.getInstance(
                    it.id,
                    StoryDetailsConfiguration(
                        viewModel.fullViewLiveData.value ?: false,
                        canChangeFullView
                    )
                )
            }
        }

        if (!canChangeFullView)
            viewModel.setFullView(true)

        detailsFragment?.updateConfiguration(canChangeFullView = canChangeFullView)

        viewModel.fullViewLiveData.value.let {
            binding.root.progress = if (it == true) 1f else 0f
        }

        viewModel.fullViewLiveData.observe(this) {
            if (it) binding.root.transitionToEnd()
            else binding.root.transitionToStart()

            detailsFragment?.updateConfiguration(isFullView = it)
        }
    }

    private fun setCardVisibility(value: Boolean, onCardInvisible: () -> Unit) {
        if (value != binding.storyCard.isVisible)
            binding.backgroundShadow.animate(
                if (value) android.R.anim.fade_in else android.R.anim.fade_out,
                fillAfter = true
            )

        if (value) onCardInvisible()
        val animId = if (value) R.anim.story_slide_in_top else R.anim.story_slide_out_bottom
        binding.storyCard.let {
            it.animate(animId, onEnd = { if (!value) onCardInvisible() })
            it.visibility = if (value) View.VISIBLE else View.INVISIBLE
        }
    }

    fun closeStory() = viewModel.toggleStory(null)
    fun toggleFullView() = viewModel.toggleFullView()
}