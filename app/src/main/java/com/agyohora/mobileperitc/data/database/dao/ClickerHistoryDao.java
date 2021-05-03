package com.agyohora.mobileperitc.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.agyohora.mobileperitc.data.database.entity.ClickerHistory;

import java.util.List;

@Dao
public interface ClickerHistoryDao {

    @Insert
    long[] insertAll(ClickerHistory... histories);

    @Query("SELECT * FROM ClickerHistory")
    List<ClickerHistory> getClickerHistory();

    @Query("SELECT * FROM ClickerHistory order by id DESC LIMIT 1")
    List<ClickerHistory> getClickerCountLatest();
}
