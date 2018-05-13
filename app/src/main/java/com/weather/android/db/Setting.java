package com.weather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by syy on 2018/5/12.
 */

public class Setting extends DataSupport {

   private int id;

   private boolean isChecked;

   private int radioButtonId;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public boolean isChecked() {
      return isChecked;
   }

   public void setChecked(boolean checked) {
      isChecked = checked;
   }

   public int getRadioButtonId() {
      return radioButtonId;
   }

   public void setRadioButtonId(int radioButtonId) {
      this.radioButtonId = radioButtonId;
   }
}
