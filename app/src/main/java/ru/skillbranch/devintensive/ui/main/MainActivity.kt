package ru.skillbranch.devintensive.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.ui.adapters.ChatAdapter
import ru.skillbranch.devintensive.ui.adapters.ChatItemTouchHelperCallback
import ru.skillbranch.devintensive.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {

  private lateinit var chatAdapter: ChatAdapter
  private lateinit var viewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initToolbar()
    initViews()
    initViewModel()
  }

  private fun initToolbar() {
    setSupportActionBar(toolbar)
  }

  private fun initViews() {

    chatAdapter = ChatAdapter {
      Snackbar.make(rv_chat_list, "Click on ${it.title}", Snackbar.LENGTH_LONG).show()
    }

    val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
    val touchCallback = ChatItemTouchHelperCallback(chatAdapter) {
      viewModel.addToArchive(it.id)
      Snackbar.make(rv_chat_list, "Вы точно хотите добывить ${it.title} в архив?", Snackbar.LENGTH_LONG).show()
    }

    val touchHelper = ItemTouchHelper(touchCallback)
    touchHelper.attachToRecyclerView(rv_chat_list)

    with(rv_chat_list) {
      adapter = chatAdapter
      layoutManager = LinearLayoutManager(this@MainActivity)
      addItemDecoration(divider)
    }

    fab.setOnClickListener {
      viewModel.addItems()
    }
  }

  private fun initViewModel() {
    viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    viewModel.getChatData().observe(this, Observer { chatAdapter.updateDate(it) })
  }
}