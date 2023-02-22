package www.iesmurgi.valorantretrofitsamir

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class AgentsAdapter(private val agents: List<Agent>) : RecyclerView.Adapter<AgentsAdapter.ViewHolder>() {
    companion object {
        var currentPlayingHolder: ViewHolder? = null
    }
    private var filteredAgents = agents

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnPlay: ImageButton = itemView.findViewById(R.id.btnPlay)
        val ivFoto: ImageView = itemView.findViewById(R.id.ivFoto)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val sbSound: SeekBar = itemView.findViewById(R.id.sbSound)
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.agent_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = filteredAgents.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val agent = filteredAgents[position]
        val handler = Handler()
        var currentPosition = 0
        holder.tvNombre.text = agent.displayName
        holder.tvDescripcion.text = agent.description

        Glide.with(holder.ivFoto.context)
            .load(agent.displayIcon)
            .into(holder.ivFoto)

        val runnable = object :Runnable{
            override fun run() {
                if (holder.mediaPlayer != null) {
                    holder.sbSound?.progress = holder.mediaPlayer?.currentPosition!!
                }
                handler.postDelayed(this, 50)
            }

        }

        holder.btnPlay.setOnClickListener {
            currentPlayingHolder = holder
            if (holder.mediaPlayer != null && holder.mediaPlayer!!.isPlaying) {
                handler.removeCallbacks(runnable)
                holder.btnPlay.setImageResource(R.drawable.ic_play)
                currentPosition = holder.mediaPlayer!!.currentPosition
                holder.mediaPlayer!!.stop()
                holder.mediaPlayer!!.release()
                holder.mediaPlayer = null
                handler.removeCallbacksAndMessages(null)
                return@setOnClickListener
            }
            holder.btnPlay.setImageResource(R.drawable.ic_pause)
            holder.mediaPlayer = MediaPlayer()
            holder.mediaPlayer!!.setOnCompletionListener {
                currentPosition =0
                holder.sbSound?.progress = 0
                handler!!.removeCallbacks(runnable!!)
                holder.btnPlay.setImageResource(R.drawable.ic_play)
            }
            holder.mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            holder.mediaPlayer!!.setDataSource(agent.voiceLine.mediaList[0].wave)
            holder.mediaPlayer!!.prepare()
            holder.sbSound?.max = holder.mediaPlayer?.duration!!
            holder.sbSound?.progress = currentPosition
            holder.mediaPlayer!!.seekTo(currentPosition)
            handler.post(runnable)
            holder.mediaPlayer!!.start()
        }





        holder.sbSound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    holder.mediaPlayer?.seekTo(progress)
                    currentPosition = progress
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })



    }

    fun filterAgents(query: String) {
        filteredAgents = agents.filter { agent ->
            agent.displayName.contains(query, true)
        }
        notifyDataSetChanged()
    }

}