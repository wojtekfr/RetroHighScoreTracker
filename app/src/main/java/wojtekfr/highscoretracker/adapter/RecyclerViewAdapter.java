package wojtekfr.highscoretracker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import wojtekfr.highscoretracker.R;
import wojtekfr.highscoretracker.model.Game;
import wojtekfr.highscoretracker.util.Converters;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Game> gameList;
    private Context context;
    private OnGameClickListener GameClickListener;


    public RecyclerViewAdapter(List<Game> gameList, Context context, OnGameClickListener onGameClickListener) {
        this.gameList = gameList;
        this.context = context;
        this.GameClickListener = onGameClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Game game = Objects.requireNonNull(gameList.get(position));
        holder.game.setText(game.getGameName());
        holder.score.setText(String.valueOf(game.getHighScore()));
        holder.note.setText(game.getNote());
        holder.image.setImageBitmap(game.getImage());
    }

    @Override
    public int getItemCount() {
        return Objects.requireNonNull(gameList.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnGameClickListener onGameClickListener;
        public TextView game;
        public TextView score;
        public TextView note;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            game = itemView.findViewById(R.id.textViewRowGame);
            score = itemView.findViewById(R.id.textViewRowScore);
            note = itemView.findViewById(R.id.textViewRowNote);
            image = itemView.findViewById(R.id.imageViewGame);
            this.onGameClickListener = GameClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onGameClickListener.onGameClick(getAdapterPosition());
        }
    }

    public interface OnGameClickListener {
        void onGameClick(int position);
    }
}
