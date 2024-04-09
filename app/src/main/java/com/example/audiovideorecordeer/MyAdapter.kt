package com.example.audiovideorecordeer

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovideorecordeer.databinding.AudioVideoItemBinding
import java.io.IOException

class MyAdapter(private val myList: List<MyItem>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    inner class MyViewHolder(val binding: AudioVideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AudioVideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = myList[position]

        if (currentItem.type == "audio") {
            holder.binding.videoView.visibility = View.GONE
            holder.binding.musicNote.visibility = View.VISIBLE
        } else if (currentItem.type == "video") {
            holder.binding.videoView.visibility = View.VISIBLE
            holder.binding.musicNote.visibility = View.GONE
            holder.binding.videoView.setVideoPath(currentItem.filePath)
            holder.binding.videoView.start()
        }

        holder.binding.playButton.setOnClickListener {
            holder.mediaPlayer = MediaPlayer().apply {
                try {
                    setDataSource(currentItem.filePath)
                    prepare()
                    start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        holder.binding.stopButton.setOnClickListener {
            holder.mediaPlayer?.apply {
                stop()
                release()
            }
            holder.mediaPlayer = null
        }

        holder.binding.pauseButton.setOnClickListener {
            holder.mediaPlayer?.pause()
        }
    }

    override fun getItemCount() = myList.size
}
