package com.uos.makebook.MainList;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uos.makebook.R;

import java.util.ArrayList;
import java.util.Random;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    static ArrayList<Book> bookItems = new ArrayList<Book>();
    private OnItemClickListener listener = null;
    private int[] defaultList = {R.drawable.default_cover, R.drawable.default_cover2, R.drawable.default_cover3, R.drawable.default_cover4};
    private Random random = new Random();

    @NonNull
    @Override
    public BookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.mainlist_booklist_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book item = bookItems.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return bookItems.size();
    }
    public void addItem(Book item) { bookItems.add(item); }
    public void setItems(ArrayList<Book> personItems){
        this.bookItems=personItems;
    }
    public Book getItem(int position){
        return bookItems.get(position);
    }
    public void setItem(int position, Book item){
        bookItems.set(position,item);
    }

    //인터페이스에 리스너 연결결
   public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


    //클릭 이벤트
    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text_name, text_writer, text_createDate;
        ImageView image_cover;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){

                        //리스너 메소드 호출
                        if(listener != null){
                            listener.onItemClick(v,pos);
                        }
                    }
                }
            });
            text_name = itemView.findViewById(R.id.textViewBookTitle);
            text_writer = itemView.findViewById(R.id.textViewBookWriter);
            text_createDate = itemView.findViewById(R.id.textViewBookCreateDate);
            image_cover = itemView.findViewById(R.id.imageBookcover);
        }

        public void setItem(Book item){
            text_name.setText(item.getTitle());
            text_writer.setText(item.getWriter());
            text_createDate.setText(item.getCreatedate());
            //커버사진
            Glide.with(itemView)
                    .load(item.getCover()).thumbnail(0.5f)
                    .fitCenter()
                    .placeholder(defaultList[random.nextInt(4)])
                    .error(defaultList[random.nextInt(4)])
                    .fallback(defaultList[random.nextInt(4)])
                    .into(image_cover);
        }
    }
}
