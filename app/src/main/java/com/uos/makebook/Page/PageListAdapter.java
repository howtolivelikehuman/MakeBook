package com.uos.makebook.Page;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
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


public class PageListAdapter extends RecyclerView.Adapter<PageListAdapter.ViewHolder>{

    private ArrayList<Page> pageItems = new ArrayList<Page>(); //adapter에 들어갈 list
    private OnItemClickListener listener = null ;
    private DisplayMetrics screenSize = null; // ViewHolder에서 비트맵의 사이즈를 정하는데 사용.

    @NonNull
    @Override
    //page_pagelist_item.xml을 inflate 시킴킴
    public PageListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.page_pagelist_item,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    // Item을 하나 하나 보여주는(bind 되는) 함수
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Page item = pageItems.get(position);
        holder.setItem(item, position);
    }

    @Override
    // RecyclerView의 총 개수
    public int getItemCount() {
        return pageItems.size();
    }

    // 외부에서 item을 추가시킬 함수
    public void addItem(Page item) { pageItems.add(item); }

    public void setItems(ArrayList<Page> personItems){
        this.pageItems=personItems;
    }

    public Page getItem(int position){
        return pageItems.get(position);
    }

    public void setItem(int position, Page item){
        pageItems.set(position,item);
    }

    public void setScreenSize(DisplayMetrics info) {
        screenSize = info;
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
        ImageView pageView;
        TextView pageNum;
        //private ImageView page_image; //페이지 이미지

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition(); //클릭한 아이템의 position
                    if (pos != RecyclerView.NO_POSITION){
                        //리스너 메소드 호출
                        if(listener != null){
                            listener.onItemClick(v,pos);
                        }
                    }
                }
            });

            pageView = itemView.findViewById(R.id.pageView);
            pageNum = itemView.findViewById(R.id.textNum);
        }

        //pageItem 구성
        public void setItem(Page item, int position){
            Bitmap pageBitmap = Bitmap.createBitmap(screenSize.widthPixels, screenSize.heightPixels, Bitmap.Config.ARGB_8888);
            Canvas pageCanvas = new Canvas(pageBitmap);
            item.drawElements(pageCanvas);

            pageView.setImageBitmap(pageBitmap);
            pageNum.setText(Integer.toString(position+1));
        }
    }
}
