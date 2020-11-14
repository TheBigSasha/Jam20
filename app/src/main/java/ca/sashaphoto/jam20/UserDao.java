package ca.sashaphoto.jam20;

import androidx.room.*;
import java.util.*;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();


    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}