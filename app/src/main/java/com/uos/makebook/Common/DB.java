package com.uos.makebook.Common;

import java.util.ArrayList;

public interface DB<T>{
    long insert(DTO o);
    int delete(long pk);
    int update(DTO o);
    DTO selectById(Long id);
    ArrayList<T> selectAll();
    ArrayList<T> select(String[] column, String[] data);
}
