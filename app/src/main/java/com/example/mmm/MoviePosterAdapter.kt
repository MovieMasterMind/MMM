import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.R
import com.bumptech.glide.request.target.Target

class MoviePosterAdapter(private val posterUrls: List<String>) :
    //This is a subclass of RecyclerView.Adapter, called PosterViewHolder
    RecyclerView.Adapter<MoviePosterAdapter.PosterViewHolder>() {

        //now we override these functions because we need to,
        //get use these with the item_movie_poster.xml file that is used instead of Recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_poster, parent, false)
        return PosterViewHolder(view)
    }

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(posterUrls[position])
            //.override(0,0) // Specify the desired size for loading the original image
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return posterUrls.size
    }

    inner class PosterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewPoster)
    }
}
