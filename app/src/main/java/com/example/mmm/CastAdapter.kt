
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mmm.R

class CastAdapter(private var castList: List<CastMember>) :
    RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.cast_member_image)
        val nameTextView: TextView = view.findViewById(R.id.cast_member_name)
        val characterTextView: TextView = view.findViewById(R.id.cast_member_character) // Add this line
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cast_member, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val castMember = castList[position]
        Glide.with(holder.imageView.context)
            .load(castMember.imageUrl)
            .into(holder.imageView)
        holder.nameTextView.text = castMember.name
        holder.characterTextView.text = castMember.character // Set the character text here
    }

    override fun getItemCount() = castList.size

    fun updateCastList(newCastList: List<CastMember>) {
        castList = newCastList
        notifyDataSetChanged()
    }
}

