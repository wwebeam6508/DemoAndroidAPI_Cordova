
package example.howen_test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yho_test.R;
import jhxd.serial.serialService;

import java.io.UnsupportedEncodingException;

public class SerialPortMain extends Activity {
    private SharedPreferences preferencesValue;
    private SharedPreferences.Editor editorValue;

    private Spinner comSpinner, baudSpinner, databitsSpinner, paritySpinner, stopbitsSpinner;
    private EditText autotimeEditText, sendEditText, recvsendcountEditText, sendsendcountEditText;
    private TextView recTextView;
    private Button openComButton, sendButton, clearsendButton, clearrecvButton, clearCountButton;
    private CheckBox autosendCheckBox, recCheckBox, sendCheckBox;
    private int comInt = 0;
    private int baudInt = 0;
    private int databitsInt = 0;
    private int parityInt = 0;
    private int stopbitsInt = 0;
    private String autosendtimeString = null;

    private boolean ifopensuccess = false;
    private int fd = -1;

    private int autotime = 1000;
    private boolean isAuto = false;

    private int recDataCount = 0;
    private int readsize;
    private int sendDataCout = 0;
    private int sendSize;

    @SuppressLint("WorldReadableFiles")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com);

        preferencesValue = getSharedPreferences("feijie", MODE_WORLD_READABLE);
        editorValue = preferencesValue.edit();

        init_view();

        System.out.println("SerialPortMain-------------start");
    }

    private void init_view() {
        // TODO Auto-generated method stub
        comSpinner = (Spinner) findViewById(R.id.comSpinner);
        baudSpinner = (Spinner) findViewById(R.id.baudSpinner);
        databitsSpinner = (Spinner) findViewById(R.id.databitsSpinner);
        paritySpinner = (Spinner) findViewById(R.id.paritySpinner);
        stopbitsSpinner = (Spinner) findViewById(R.id.stopbitsSpinner);

        recTextView = (TextView) findViewById(R.id.recTextView);
        recTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        recTextView.setSelected(true);

        autotimeEditText = (EditText) findViewById(R.id.autotimeEditText);

        sendEditText = (EditText) findViewById(R.id.sendEditText);

        recvsendcountEditText = (EditText) findViewById(R.id.recvsendcountEditText);
        sendsendcountEditText = (EditText) findViewById(R.id.sendsendcountEditText);

        openComButton = (Button) findViewById(R.id.openComButton);
        openComButton.setOnClickListener(new onclick());

        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new onclick());

        clearsendButton = (Button) findViewById(R.id.clearsendButton);
        clearsendButton.setOnClickListener(new onclick());

        clearrecvButton = (Button) findViewById(R.id.clearrecvButton);
        clearrecvButton.setOnClickListener(new onclick());

        clearCountButton = (Button) findViewById(R.id.clearCountButton);
        clearCountButton.setOnClickListener(new onclick());

        autosendCheckBox = (CheckBox) findViewById(R.id.autoSendButton);
        autosendCheckBox.setOnCheckedChangeListener(new onchage());
        recCheckBox = (CheckBox) findViewById(R.id.rec_checkBox);
        sendCheckBox = (CheckBox) findViewById(R.id.send_checkBox);

        comInt = preferencesValue.getInt("com", 0);
        comSpinner.setSelection(comInt);

        baudInt = preferencesValue.getInt("baud", 7);
        baudSpinner.setSelection(baudInt);

        databitsInt = preferencesValue.getInt("databits", 1);
        databitsSpinner.setSelection(databitsInt);

        parityInt = preferencesValue.getInt("parity", 0);
        paritySpinner.setSelection(parityInt);

        stopbitsInt = preferencesValue.getInt("stopbits", 0);
        stopbitsSpinner.setSelection(stopbitsInt);

        autosendtimeString = preferencesValue.getString("time", "1000");
        autotimeEditText.setText(autosendtimeString);
    }

    class onchage implements OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // TODO Auto-generated method stub
            autosendtimeString = autotimeEditText.getText().toString();
            autotime = Integer.valueOf(autosendtimeString);

            sendDataThread dataThread = new sendDataThread();

            if (isChecked)
            {
                isAuto = true;
                dataThread.start();
            } else {
                isAuto = false;
            }
        }
    }

    class sendDataThread extends Thread {
        @Override
        public void run() {
            while (true) {
                // TODO Auto-generated method stub

                if (isAuto == false) {
                    System.out.println("------Exit Write Thread------");
                    break;
                }

                SendDataToPort();

                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);

                try {
                    sleep(autotime);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private void SendDataToPort() {
        // TODO Auto-generated method stub

        String sendString = sendEditText.getText().toString();
        if (sendString.length() <= 0)
            return;

        System.out.println("------writeSize: " + sendString.length() + "------");
        System.out.println("------writeData: " + sendString + "------");

        byte[] writedata;
        try {
            writedata = sendString.getBytes("GB2312");

            if (sendCheckBox.isChecked()) {
                byte[] tempBytes = hexStringToBytes(sendString);

                serialService.serialWrite(fd, tempBytes, tempBytes.length);

                sendDataCout = sendDataCout + tempBytes.length;
            } else {
                serialService.serialWrite(fd, writedata, writedata.length);

                sendDataCout = sendDataCout + writedata.length;
            }


        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @SuppressLint("DefaultLocale")
    public static byte[] hexStringToBytes(String string) {
        String hexString = string.replaceAll(" ", "").toUpperCase();

        if (hexString.length() <= 0)
            return null;

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));

        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    class onclick implements OnClickListener {
        boolean isopen = false;

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.openComButton:
                    if (!isopen) {
                        isopen = true;

                        editorValue.putInt("com", comSpinner.getSelectedItemPosition());
                        editorValue.putInt("baud", baudSpinner.getSelectedItemPosition());
                        editorValue.putInt("databits", databitsSpinner.getSelectedItemPosition());
                        editorValue.putInt("parity", paritySpinner.getSelectedItemPosition());
                        editorValue.putInt("stopbits", stopbitsSpinner.getSelectedItemPosition());
                        editorValue.putString("time", autotimeEditText.getText().toString());
                        editorValue.commit();

                        sendEditText
                                .setText("howen_test 79 68 6F 5F 74 65 73 74");

                        open_serialPort();


                       new recDataThread().start();
                    } else {
                        if (isAuto == true) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    SerialPortMain.this);
                            builder.setIcon(R.drawable.tools).
                                    setTitle("   警      告")
                                    .setMessage("    '自动发送'已经打开，请确认关闭！")
                                    .setPositiveButton("    确               定    ",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                        int which) {
                                                    // TODO Auto-generated
                                                    // method stub

                                                }
                                            }).create().show();
                        } else {
                            isopen = false;

                            ifopensuccess = false;
                            serialService.serialClose(fd);
                            openComButton.setText("open serialport");
                            System.out.println("------close serialPort------");
                        }
                    }
                    break;

                case R.id.sendButton:
                    SendDataToPort();
                    sendsendcountEditText.setText(String.valueOf(sendDataCout));
                    break;

                case R.id.clearsendButton:
                    sendEditText.setText(" ");
                    break;

                case R.id.clearrecvButton:
                    recTextView.setText("");
                    break;

                case R.id.clearCountButton:
                    recDataCount = 0;
                    recvsendcountEditText.setText(String.valueOf(recDataCount));
                    sendDataCout = 0;
                    sendsendcountEditText.setText(String.valueOf(sendDataCout));
                    break;

                default:
                    break;

            }
        }
    }

    class recDataThread extends Thread {
        byte[] readdata = new byte[1024];
        int readlen = 1024;

        public void run() {
            while (ifopensuccess) {
                if (ifopensuccess == false) {
                    System.out.println("------Close Rece Thread------");
                    break;
                }

                try {
                    readsize = serialService.serialRead(fd, readdata, readlen);
                    if (readsize > 0) {
                        System.out.println("------readSize:" + String.valueOf(readsize) + "------");

                        byte[] tempBytes = new byte[readsize];
                        for (int i = 0; i < tempBytes.length; i++) {
                            tempBytes[i] = readdata[i];
                        }

                        String recvdataString = new String(tempBytes, "GBK");

                        System.out.println("------recvData:" + recvdataString + "------");

                        if (recCheckBox.isChecked()) {

                            recvdataString = hex2DebugHexString(tempBytes);

                            // recvdataString = hexString(tempBytes, readsize);

                            recDataCount = recDataCount + readsize;
                        } else {
                            recDataCount = recDataCount + readsize;
                        }

                        Message msg = new Message();
                        msg.obj = recvdataString;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        };
    };

    public String hexString(byte[] dataArray, int dataLength) {
        String hexChar;
        String hexString = "";
        for (int i = 0; i < dataLength; i++) {
            hexChar = Integer.toHexString(dataArray[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = '0' + hexChar;
            }
            hexString = hexString + hexChar.toUpperCase() + " ";
        }
        return hexString;
    }

    public static String hex2DebugHexString(byte[] b) {
        int len = b.length;
        int[] x = new int[len];
        String[] y = new String[len];
        StringBuilder str = new StringBuilder();
        int j = 0;
        for (; j < len; j++) {
            x[j] = b[j] & 0xff;
            y[j] = Integer.toHexString(x[j]);
            while (y[j].length() < 2) {
                y[j] = "0" + y[j];
            }
            str.append(y[j]);
            str.append(" ");
        }
        return new String(str).toUpperCase();
    }

    Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    recTextView.append(msg.obj.toString());
                    recvsendcountEditText.setText(String.valueOf(recDataCount));
                    break;
                case 2:

                    sendsendcountEditText.setText(String.valueOf(sendDataCout));
                    break;
                default:
                    break;
            }
        };
    };

    private void open_serialPort() {
        // TODO Auto-generated method stub
        if ((fd = serialService.serialOpen(comSpinner.getSelectedItem().toString())) < 0) {
            ifopensuccess = false;
            System.out.println("----open serialPort error-----");
            Toast.makeText(SerialPortMain.this, "open serialPort fail!!!",
                    Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("------fd：" + fd + "------" + baudSpinner.getSelectedItem().toString());
            if (serialService.serialPortSetting(
            		fd,
            		Integer.parseInt((String) baudSpinner.getSelectedItem()),
            		Integer.parseInt((String) databitsSpinner.getSelectedItem()),
            		paritySpinner.getSelectedItemPosition(),
            		Integer.parseInt((String) stopbitsSpinner.getSelectedItem())
            		) < 0) {
                System.out.println("----open serialPort error-----");
                Toast.makeText(SerialPortMain.this, "open serialPort fail!!!",
                        Toast.LENGTH_SHORT).show();
                ifopensuccess = false;
            } else {
                System.out.println("----open serialPort success-----");
                openComButton.setText("close serialport");

                ifopensuccess = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (ifopensuccess = true) {
            isAuto = false;
            ifopensuccess = false;
            serialService.serialClose(fd);
        }
    }

}
