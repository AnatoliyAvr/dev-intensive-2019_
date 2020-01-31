package ru.skillbranch.devintensive.ui.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_single.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem

class ChatAdapter(private val listener: (ChatItem) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.SingleViewHolder>() {
  var items: List<ChatItem> = listOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleViewHolder {
    val inflate = LayoutInflater.from(parent.context)
    val convertView = inflate.inflate(R.layout.item_chat_single, parent, false)
    Log.d("M_ChatAdapter", "onCreateViewHolder")
    return SingleViewHolder(convertView)
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: SingleViewHolder, position: Int) {
    Log.d("M_ChatAdapter", "onBindViewHolder $position")
    holder.bind(items[position], listener)
  }

  fun updateDate(data: List<ChatItem>) {

    Log.d(
        "M_ChatAdapter", "update data adapter - new data ${data.size} hash : ${data.hashCode()} " +
        "old data ${items.size} hash ${items.hashCode()}"
    )

    val diffCallback = object : DiffUtil.Callback() {
      override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
          items[oldPos].id == items[newPos].id

      override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean =
          items[oldPos].hashCode() == items[newPos].hashCode()

      override fun getOldListSize(): Int = items.size

      override fun getNewListSize(): Int = data.size
    }

    val diffResult = DiffUtil.calculateDiff(diffCallback)

    items = data
    diffResult.dispatchUpdatesTo(this)
  }

  inner class SingleViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView),
      LayoutContainer, ItemTouchViewHolder {

    override val containerView: View?
      get() = itemView

    fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
      if (item.avatar == null) {
        iv_avatar_single.initials = item.initials
      } else {
        //TODO set drawable
      }

      sv_indicator.visibility = if (item.isOnline) View.VISIBLE else View.GONE
      with(tv_date_single) {
        visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
        text = item.lastMessageDate
      }

      with(tv_counter_single) {
        visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
        text = item.messageCount.toString()
      }

      tv_title_single.text = item.shortDescription
      tv_message_single.text = item.shortDescription
      itemView.setOnClickListener {
        listener.invoke(item)
      }
    }

    override fun onItemSelected() {
      itemView.setBackgroundColor(Color.LTGRAY)
    }

    override fun onItemCleared() {
      itemView.setBackgroundColor(Color.WHITE)
    }
  }
}