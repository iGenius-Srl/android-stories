package com.igenius.androidstories.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.igenius.androidstories.app.models.StoriesFolder
import com.igenius.androidstories.app.models.StoryNode
import com.igenius.androidstories.app.databinding.StoryListItemBinding
import com.igenius.androidstories.app.models.AndroidStory
import com.igenius.androidstories.app.models.StoryNode.Companion.DIVIDER
import com.igenius.androidstories.app.utils.themeColors

data class NodeItemModel (
    val title: String,
    val node: StoryNode,
    val padding: Int,
    @DrawableRes val icon: Int,
    val accentIcon: Boolean,
    var selected: Boolean,
) {
    companion object {
        fun node(
            node: StoryNode,
            rootDistance: Int,
            isOpen: Boolean,
            selected: Boolean = false
        ) = NodeItemModel(
            title = node.title,
            node = node,
            padding = rootDistance - 1,
            icon = when(node) {
                is StoriesFolder ->
                    if(isOpen) R.drawable.ic_baseline_folder_open_24
                    else R.drawable.ic_baseline_folder_24
                else -> R.drawable.ic_baseline_history_edu_24
            },
            accentIcon = node !is StoriesFolder,
            selected = selected,
        )

        fun favourite(
            node: AndroidStory,
            selected: Boolean = false,
        ) = NodeItemModel(
            title = node.cleanPath.replace(DIVIDER, " $DIVIDER "),
            node = node,
            padding = 0,
            icon = R.drawable.ic_baseline_star_rate_24,
            accentIcon = true,
            selected = selected,
        )
    }
}

class StoriesAdapter(
    private val onSelect: (story: StoryNode, longPress: Boolean) -> Unit
) : ListAdapter<NodeItemModel, NodeItem>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NodeItem(parent, onSelect)

    override fun onBindViewHolder(holder: NodeItem, position: Int) {
        holder.bind(getItem(position))
    }
}

private class DiffCallback : DiffUtil.ItemCallback<NodeItemModel>() {

    override fun areItemsTheSame(oldItem: NodeItemModel, newItem: NodeItemModel) =
        oldItem.node == newItem.node
                && oldItem.title == newItem.title

    override fun areContentsTheSame(oldItem: NodeItemModel, newItem: NodeItemModel) =
        oldItem.node == newItem.node
                && newItem.title == oldItem.title
                && newItem.padding == oldItem.padding
                && newItem.icon == oldItem.icon
                && newItem.accentIcon == oldItem.accentIcon
                && newItem.selected == oldItem.selected
}

class NodeItem(
    parent: ViewGroup,
    val onSelect: (story: StoryNode, longPress: Boolean) -> Unit,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.story_list_item, parent, false)
) {
    private val binding by lazy { StoryListItemBinding.bind(itemView) }

    fun bind(model: NodeItemModel) = binding.run {
        val theme = itemView.context.themeColors

        (if(model.selected) theme.colorPrimary else theme.textColorPrimary)
            .let(title::setTextColor)
        title.text = model.title

        itemView.run {
            updatePaddingRelative(
                start = resources.getDimensionPixelSize(R.dimen.item_small_margin)
                        + resources.getDimensionPixelSize(R.dimen.folder_distance) * model.padding
            )
            setOnClickListener { onSelect(model.node, false) }
            setOnLongClickListener {
                onSelect(model.node, true)
                true
            }
        }
        icon.setImageResource(model.icon)
        icon.setColorFilter(if(model.accentIcon) theme.colorPrimary else theme.colorSecondary)

        selectedStatusView.isVisible = model.selected
    }
}