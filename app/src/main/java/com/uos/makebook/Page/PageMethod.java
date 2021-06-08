package com.uos.makebook.Page;

import android.util.Log;

import com.uos.makebook.Common.DB;

import java.util.ArrayList;

import static com.uos.makebook.Common.Constant.COLUMN_PAGE;

public class PageMethod {

    public static ArrayList<Page> setPageList(DB pageDB, long book_id){
        ArrayList<Page> pageList = getPageListFromDB(pageDB, book_id);
        //Log.d("PageMethod","setPageList");

        if(pageList.size() <  1){ // page 없으면 추가 후 다시 받아오기
            initialize(pageDB, book_id);
            pageList = getPageListFromDB(pageDB, book_id);
        }
        pageList.remove(0); // head 제거
        pageList  = sortPageList(pageDB, pageList, book_id); // page 순서 정하기
        return pageList;
    }


    public static void initialize(DB pageDB, long book_id){ // head와 첫 페이지 추가
        //Log.d("PageMethod","initialize");
        Page head = new Page(book_id, "[]", 0, 1);
        Page firstPage = new Page(book_id, "[]",0, 0);
        long head_pk = pageDB.insert(head); head.setPageId(head_pk);
        long first_pk = pageDB.insert(firstPage);
        head.setNextPage(first_pk);
        pageDB.update(head);
    }

    public static ArrayList<Page> getPageListFromDB(DB pageDB, long book_id){ // book_id에 해당하는 모든 page 가져오기
        String[] selection = {COLUMN_PAGE[1]};
        String[] selectionArgs = {Long.toString(book_id)};
        return pageDB.select(selection, selectionArgs);
    }

    public static Page getHead(DB pageDB, long book_id){
        String[] selection = {COLUMN_PAGE[1], COLUMN_PAGE[4]};
        String[] selectionArgs = {Long.toString(book_id), Integer.toString(1)};

        ArrayList<Page> headList = pageDB.select(selection, selectionArgs); // head 데려오기
        if(headList.size() == 0) {
            System.out.println("Head가 없습니다.");
            return null;
        }
        return headList.get(0);
    }

    public static ArrayList<Page> sortPageList(DB pageDB, ArrayList<Page> pageList, long book_id){
        //Log.d("PageMethod","sortPageList");
        ArrayList<Page> sortedPageList = new ArrayList<Page>();
        Page head = getHead(pageDB, book_id);
        if(head == null)
            return null;
        long nextPage = head.getNextPage();

        while(nextPage != 0 && !pageList.isEmpty()){ // nextPage 따라가면서 pageList 정렬
            for(int i=0; i<pageList.size(); i++){
                if(pageList.get(i).getPageId() != nextPage)
                    continue;
                sortedPageList.add(pageList.get(i));
                nextPage = pageList.get(i).getNextPage();
                pageList.remove(i);
                break;
            }
        }
        return sortedPageList;
    }

}
