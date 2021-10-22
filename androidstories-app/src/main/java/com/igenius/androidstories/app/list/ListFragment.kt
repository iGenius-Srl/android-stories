package com.igenius.androidstories.app.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.igenius.androidstories.app.FragmentStory
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.databinding.FragmentListBinding

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListBinding.inflate(inflater, container, false)

        val stories = (requireContext().applicationContext as StoriesApp).storiesProvider.stories
        binding.recycler.adapter = StoriesAdapter(stories) { storyId ->
            val action = ListFragmentDirections.actionListToStory(storyId)
            findNavController().navigate(action)
        }

        return binding.root
    }

}

class StoriesAdapter(
    val stories: List<FragmentStory>,
    private val onSelect: (index: Int) -> Unit
): RecyclerView.Adapter<StoriesAdapter.StoryHolder>() {

    class StoryHolder(
        val onSelect: (index: Int) -> Unit,
        parent: ViewGroup
    ): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
    ) {
        private val titleText get () = itemView.findViewById<TextView>(android.R.id.text1)
        private val descriptionText get () = itemView.findViewById<TextView>(android.R.id.text2)

        init {
            itemView.setOnClickListener {
                onSelect(adapterPosition)
            }
        }

        fun bind(story: FragmentStory) {
            titleText.text = story.title
            descriptionText.text = story.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StoryHolder(onSelect, parent)

    override fun onBindViewHolder(holder: StoryHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size
}