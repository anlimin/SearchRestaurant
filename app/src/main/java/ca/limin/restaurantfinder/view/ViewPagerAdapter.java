package ca.limin.restaurantfinder.view;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import ca.limin.restaurantfinder.R;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private final List<String> photoLinks;;
    private final Context context;

    public ViewPagerAdapter(Context context, List<String> data) {
        this.context = context;
        this.photoLinks = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        String baseURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference=";
        Glide.with(context).load(baseURL + photoLinks.get(position)
                + "&key=" + context.getResources().getString(R.string.google_maps_key)).apply(options).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return photoLinks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_pager);
            linearLayout = itemView.findViewById(R.id.container1);
        }
    }
}
