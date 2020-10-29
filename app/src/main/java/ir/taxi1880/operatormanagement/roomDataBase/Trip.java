package ir.taxi1880.operatormanagement.roomDataBase;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
class Trip implements Serializable {

  @PrimaryKey()
  private int tripId;



}
