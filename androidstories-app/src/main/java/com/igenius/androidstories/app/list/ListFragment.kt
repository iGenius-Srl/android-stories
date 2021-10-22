package com.igenius.androidstories.app.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.igenius.androidstories.app.FragmentStory
import com.igenius.androidstories.app.R
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private val stories by lazy {
        (requireContext().applicationContext as StoriesApp).storiesProvider.stories.sortedBy { it.title }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListBinding.inflate(inflater, container, false)

        binding.recycler.adapter = StoriesAdapter(stories) { story ->
            val action = ListFragmentDirections.actionListToStory(story.id, story.title)
            findNavController().navigate(action)
        }
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )

        return binding.root
    }

}

class StoriesAdapter(
    val stories: List<FragmentStory>,
    private val onSelect: (story: FragmentStory) -> Unit
) : RecyclerView.Adapter<StoriesAdapter.StoryHolder>() {

    class StoryHolder(
        parent: ViewGroup,
        val onSelect: (index: Int) -> Unit,
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.story_list_item, parent, false)
    ) {
        private val titleText get() = itemView.findViewById<TextView>(R.id.title)
        private val descriptionText get() = itemView.findViewById<TextView>(R.id.subtitle)

        init {
            itemView.setOnClickListener {
                onSelect(adapterPosition)
            }
        }

        fun bind(story: FragmentStory) {
            titleText.text = story.title
            descriptionText.text = story.description.takeIf { !it.isNullOrBlank() } ?: " - "
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StoryHolder(parent) { position ->
            onSelect(stories[position])
        }

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size
}