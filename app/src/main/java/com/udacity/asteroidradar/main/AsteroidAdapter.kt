package com.udacity.asteroidradar.main

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidRowBinding


class AsteroidAdapter(private val clickListener: (asteroid: Asteroid) -> Unit) :
    RecyclerView.Adapter<AsteroidAdapter.ViewHolder>() {


    private var asteroids: List<Asteroid> = emptyList()
    private lateinit var binding: AsteroidRowBinding

    fun setAsteroids(data: List<Asteroid>) {
        this.asteroids = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        binding =
            AsteroidRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroid = asteroids[position]
        holder.bindAsteroid(clickListener, asteroid)
    }

    override fun getItemCount() = asteroids.size

    inner class ViewHolder(private val binding: AsteroidRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindAsteroid(clickListener: (asteroid: Asteroid) -> Unit, asteroid: Asteroid) {
            binding.ast = asteroid

            binding.root.setOnClickListener {
                clickListener(asteroid)
            }
            binding.executePendingBindings()
        }
    }
}

