package com.igenius.androidstories.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePaddingRelative
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.igenius.androidstories.app.*
import com.igenius.androidstories.app.data.StoriesFolder
import com.igenius.androidstories.app.data.StoryNode
import com.igenius.androidstories.app.data.ViewStory
import com.igenius.androidstories.app.databinding.StoryListItemBinding

data class NodeItemModel (
    val node: StoryNode,
    val rootDistance: Int,
    val isOpen: Boolean,
    val selected: Boolean
)

class StoriesAdapter(
    private val onSelect: (story: StoryNode) -> Unit
) : ListAdapter<NodeItemModel, NodeItem>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NodeItem(parent, onSelect)

    override fun onBindViewHolder(holder: NodeItem, position: Int) {
        holder.bind(getItem(position))
    }
}

private class DiffCallback : DiffUtil.ItemCallback<NodeItemModel>() {

    override fun areItemsTheSame(oldItem: NodeItemModel, newItem: NodeItemModel) =
        oldItem.node.completePath == newItem.node.completePath

    override fun areContentsTheSame(oldItem: NodeItemModel, newItem: NodeItemModel) =
        oldItem.node == newItem.node
                && newItem.isOpen == oldItem.isOpen
                && newItem.selected == oldItem.selected
}

class NodeItem(
    parent: ViewGroup,
    val onSelect: (story: StoryNode) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.story_list_item, parent, false)
) {
    private val binding by lazy { StoryListItemBinding.bind(itemView) }

    fun bind(model: NodeItemModel) = binding.run {
        title.text = model.node.title

        itemView.run {
            updatePaddingRelative(
                start = resources.getDimensionPixelSize(R.dimen.item_small_margin)
                        + resources.getDimensionPixelSize(R.dimen.folder_distance) * (model.rootDistance - 1)
            )
            setOnClickListener { onSelect(model.node) }
        }

        selectedStatusView.visibility = if(model.selected) View.VISIBLE else View.GONE

        when(model.node) {
            is ViewStory -> {
                iconStory.visibility = View.VISIBLE
                iconFolder.visibility = View.INVISIBLE
            }
            is StoriesFolder -> {
                iconFolder.setImageResource(
                    if(model.isOpen) R.drawable.ic_baseline_folder_open_24 else R.drawable.ic_baseline_folder_24
                )
                iconFolder.visibility = View.VISIBLE
                iconStory.visibility = View.INVISIBLE
            }
        }
    }
}