package com.example.howen_test;

import android.widget.Spinner;
import android.widget.Toast;

public class SerialPortClass {
  private Spinner comSpinner, baudSpinner, databitsSpinner, paritySpinner, stopbitsSpinner;
  private boolean ifopensuccess = false;
  private int fd = -1;
  public void open_serialPort() {
    // TODO Auto-generated method stub
    if ((fd = jhxd.serial.serialService.serialOpen("/dev/ttyS4")) < 0) {
      ifopensuccess = false;
      System.out.println("----open serialPort error-----");

    } else {
      System.out.println("------fd：" + fd + "------" + baudSpinner.getSelectedItem().toString());
      if (jhxd.serial.serialService.serialPortSetting(
        fd,
        Integer.parseInt((String) baudSpinner.getSelectedItem()),
        Integer.parseInt((String) databitsSpinner.getSelectedItem()),
        paritySpinner.getSelectedItemPosition(),
        Integer.parseInt((String) stopbitsSpinner.getSelectedItem())
      ) < 0) {
        System.out.println("----open serialPort error-----");

        ifopensuccess = false;
      } else {
        System.out.println("----open serialPort success-----");


        ifopensuccess = true;
      }
    }
  }
}
