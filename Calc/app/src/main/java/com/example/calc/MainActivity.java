package com.example.calc;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @Package:        com.example.calc
 * @ClassName:      MainActivity
 * @Description:    用于对布局进行显示，对按钮事件进行处理
 * @Author:         CLJZ
 * @CreateDate:     2019/11/11 11:06
 * @UpdateUser:     CLJZ
 * @UpdateDate:     2019/11/11 11:06
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * 显示算式的区域TextView
     * 每一个数字按钮Button
     */
    private static TextView displayPanel;
    private static Button btnAc;
    private static Button btnPercentage;
    private static Button btnDiv;
    private static Button btnSeven;
    private static Button btnEight;
    private static Button btnNine;
    private static Button btnMult;
    private static Button btnFour;
    private static Button btnFive;
    private static Button btnSix;
    private static Button btnSub;
    private static Button btnOne;
    private static Button btnTwo;
    private static Button btnThree;
    private static Button btnAdd;
    private static Button btnJump;
    private static Button btnZero;
    private static Button btnPoint;
    private static Button btnEquals;
    private static Button btnDelete;
    private static RelativeLayout relativeLayout;
    private static TextView timer;
    private static Thread c_trd = null;
    private Handler childHandler;
    private static MinerThread minerThread = null;
    /**
     * result:用于记录算式
     * resultShow:用于显示算是
     * regular：正则匹配的规则，用于找出运算符
     * sum：运算结果
     * colorBackground:显示面板的背景颜色
     * colorFont:倒计时器的字体颜色
     */
    private static String result ="";
    private static String resultShow = "";
    private static String regular = "[\\+\\-\\*//%]";
    private static String sum = "";
    private static int time = 0;
    private static int msgParent = 0x001;
    private static int msgChild = 0x002;
    private static int msgChildComplete = 0x003;
    private static Handler minHandler;
    private static String[] colorBackground = {"#FFFF66","#FFCC00","#FF33FF","#66FF33","#66CCFF"};
    private static String[] colorFont = {"#FF0000","#FFFF00","#00CC00","#FFFFFF","#000000"};
    @SuppressLint("HandlerLeak")
    private Handler parentHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Message msg_ui = Message.obtain();
            msg_ui.what = msgParent;
            if (msg.what == msgChild) {
                String color = colorBackground[(int)(Math.random()*colorBackground.length)];
                String colorText = colorFont[(int)(Math.random()*colorFont.length)];relativeLayout.setBackgroundColor(Color.parseColor(color));
                displayPanel.setBackgroundColor(Color.parseColor(color));
                timer.setTextColor(Color.parseColor(colorText));
                timer.setVisibility(View.VISIBLE);
                timer.setText(time+"s");
                childHandler.sendMessage(msg_ui);
            } else if (msg.what == msgChildComplete) {
                timer.setVisibility(View.INVISIBLE);
            } else if (msg.what == MinerThread.msgMiner) {
                Log.d("LINJUNFENG", "shushu: "+msg.arg1);
            }
            else {
                displayPanel.setBackgroundColor(Color.parseColor("#F4F4F4"));
                relativeLayout.setBackgroundColor(Color.parseColor("#F4F4F4"));
                childHandler.removeMessages(msgChild);
                parentHandler.removeMessages(msgParent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if (minerThread == null || ( minerThread != null   && !minerThread.isAlive())) {
            Log.d("LINJUNFENG", "MinerThread:创建 ");
            minerThread = new MinerThread(3);
        }
        init();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("result",resultShow);
        //outState.putString("thread",m_trd.);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        result = savedInstanceState.getString("result");
    }

    /**
     * @description:当发生屏幕旋转时显示当前屏幕的状态
     * @author: CLJZ
     * @date: 2019/11/18  13:45
     * @param:
     * @return:
     */
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        //Toast.makeText(this, "onConfigurationChanged", Toast.LENGTH_SHORT).show();
//        setContentView(R.layout.activity_main);
//        init();
//        String screen = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "横向屏幕": "竖向屏幕";
//        Toast.makeText(this, "屏幕方向：" + screen, Toast.LENGTH_SHORT).show();
//    }
    /**
     * @description:对控件进行初始化
     * @author: CLJZ
     * @date: 2019/11/11  11:15
     * @param: []
     * @return: void
     */
    public void init() {
        //展示部分
        displayPanel = findViewById(R.id.displayPanel);
        //数字部分
        btnZero = findViewById(R.id.btn_zero);
        btnOne = findViewById(R.id.btn_one);
        btnTwo = findViewById(R.id.btn_two);
        btnThree = findViewById(R.id.btn_three);
        btnFour = findViewById(R.id.btn_four);
        btnFive = findViewById(R.id.btn_five);
        btnSix = findViewById(R.id.btn_six);
        btnSeven = findViewById(R.id.btn_seven);
        btnEight = findViewById(R.id.btn_eight);
        btnNine = findViewById(R.id.btn_nine);
        btnPoint = findViewById(R.id.btn_point);

        //基本的运算
        btnDiv = findViewById(R.id.btn_div);
        btnMult = findViewById(R.id.btn_mult);
        btnSub = findViewById(R.id.btn_sub);
        btnAdd = findViewById(R.id.btn_add);
        btnPercentage = findViewById(R.id.btn_percentage);

        //基本的操作
        btnAc = findViewById(R.id.btn_ac);
        btnDelete = findViewById(R.id.btn_delete);
        btnJump = findViewById(R.id.btn_jump);
        btnEquals = findViewById(R.id.btn_equals);
        relativeLayout = findViewById(R.id.bg);
        timer = findViewById(R.id.timer);

        //设置监听器
        btnZero.setOnClickListener(this);
        btnOne.setOnClickListener(this);
        btnTwo.setOnClickListener(this);
        btnThree.setOnClickListener(this);
        btnFour.setOnClickListener(this);
        btnFive.setOnClickListener(this);
        btnSix.setOnClickListener(this);
        btnSeven.setOnClickListener(this);
        btnEight.setOnClickListener(this);
        btnNine.setOnClickListener(this);
        btnPoint.setOnClickListener(this);

        //基本的运算
        btnDiv.setOnClickListener(this);
        btnMult.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnPercentage.setOnClickListener(this);

        //基本的操作
        btnAc.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnJump.setOnClickListener(this);
        btnEquals.setOnClickListener(this);
        displayPanel.setText(resultShow);
    }
    /**
     * @description:对每一个按钮按下的事件进行处理，得到算数结果
     * @author: CLJZ
     * @date: 2019/11/11  11:14
     * @param: [view]
     * @return: void
     */
    @Override
    public void onClick(View view) {
        try {
            //为每一个按钮进行事件处理，需要注意，除法符号无法识别，需要额外转换
            //并且加减乘除，百分号，小数点不可以连续输入
            if (view.getId() == R.id.btn_one) {
                result += "1";
                resultShow += "1";
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_two) {
                result += "2";
                resultShow += "2";
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_three) {
                result += "3";
                resultShow += "3";
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_four) {
                result += "4";
                resultShow += "4";
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_five) {
                result += "5";
                resultShow += "5";
                displayPanel.setText(resultShow);
            }else if (view.getId() == R.id.btn_six) {
                result += "6";
                resultShow += "6";
                displayPanel.setText(resultShow);
            }else if (view.getId() == R.id.btn_seven) {
                result += "7";
                resultShow += "7";
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_eight) {
                result += "8";
                resultShow += "8";
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_nine) {
                result += "9";
                resultShow += "9";
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_zero) {
                result += "0";
                resultShow += "0";
                //除法运算分母为0的情况
                if (resultShow.length() == 1) {
                    displayPanel.setText(resultShow);
                } else if (result.charAt(result.length()-2) == '/') {
                    displayPanel.setText("错误");
                } else {
                    displayPanel.setText(resultShow);
                }
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_add ) {
                if (!(result.endsWith("-") || result.endsWith("*") ||result.endsWith("/"))) {
                    duplicate("+","+");
                }
            } else if (view.getId() == R.id.btn_sub) {
                if (!(result.endsWith("*") || result.endsWith("+") ||result.endsWith("/"))) {
                    duplicate("-", "-");
                }
            } else if (view.getId() == R.id.btn_div) {
                if ( !(result.endsWith("+") || result.endsWith("*") ||result.endsWith("-"))) {
                    duplicate("÷", "/");
                }
            } else if (view.getId() == R.id.btn_sub) {
                if (!(result.endsWith("*") || result.endsWith("+") ||result.endsWith("/"))) {
                    duplicate("-", "-");
                }
            } else if (view.getId() == R.id.btn_mult) {
                if (!(result.endsWith("-") || result.endsWith("+") ||result.endsWith("/"))) {
                    duplicate("×", "*");
                }
            } else if (view.getId() == R.id.btn_percentage) {
                result += "%";
                resultShow += "%";
                displayPanel.setText(resultShow);
            } else if (view.getId() == R.id.btn_point) {
                result += ".";
                resultShow += ".";
                //对输入的小数进行判断，不允许输入不符合规定的小数
                String[] str = result.split(regular);
                for (String s : str) {
                    try {
                        Double.valueOf(s);
                        displayPanel.setText(resultShow);
                    } catch (NumberFormatException e) {
                        if (resultShow.length() > 0) {
                            StringBuilder sb = new StringBuilder();
                            resultShow = sb.append(resultShow).deleteCharAt(resultShow.length() - 1).toString();
                            displayPanel.setText(resultShow);
                        }
                        if (result.length() > 0) {
                            StringBuilder sb = new StringBuilder();
                            result = sb.append(result).deleteCharAt(result.length() - 1).toString();
                        }
                    }
                }
            } else if (view.getId() == R.id.btn_ac) {
                sum = "";
                result = "";
                resultShow ="";
                displayPanel.setText(resultShow);
                displayPanel.setBackgroundColor(Color.parseColor("#F4F4F4"));
                relativeLayout.setBackgroundColor(Color.parseColor("#F4F4F4"));
                timer.setText("");
                //time = 0;
            } else if (view.getId() == R.id.btn_delete) {
                displayPanel.setText(resultShow);
                if (resultShow.length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    resultShow = sb.append(resultShow).deleteCharAt(resultShow.length()-1).toString();
                    displayPanel.setText(resultShow);
                }
                if (result.length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    result = sb.append(result).deleteCharAt(result.length()-1).toString();
                }
            } else if (view.getId() == R.id.btn_equals) {
                if (resultShow.length() > 0) {
                    sum = CalculatorUtils.calculate(result);
                    displayPanel.setText(resultShow +"\n" +"="+sum);
                    countDown();
                }
                result = sum;
            } else if (view.getId() == R.id.btn_jump) {
                //设置横竖屏切换
                if( getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "输入格式错误", Toast.LENGTH_SHORT).show();
            result = "";
            resultShow ="";
            displayPanel.setText(result);
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "检测到非法输入", Toast.LENGTH_SHORT).show();
            result = "";
            resultShow ="";
            displayPanel.setText(result);
        }
    }
    /**
     * 功能描述: 对输入的字符串进行处理，并显示，去除连续运算符
     * @Param: [str1, str2]
     * @Return: void
     * @Author: CLJZ
     * @Date: 2019/10/21 15:35
     * @Verson 1.0.0
     */
    private static void duplicate(String str1, String str2) {
        resultShow += str1;
        result += str2;
        if (resultShow.endsWith(str1+str1)) {
            if (resultShow.length() >= 0) {
                StringBuilder sb = new StringBuilder();
                resultShow = sb.append(resultShow).deleteCharAt(resultShow.length() - 1).toString();
            }
        }
        if (result.endsWith(str2+str2)) {
            if (result.length() >= 0) {
                StringBuilder sb = new StringBuilder();
                result = sb.append(result).deleteCharAt(result.length() - 1).toString();
            }
        }
        displayPanel.setText(resultShow);
    }
    /**
     * @description:倒计时的方法，显示倒计时
     * @author: CLJZ
     * @date: 2019/11/20  14:21
     * @param: []
     * @return: void
     */
    private void countDown() {
        time = Double.valueOf(sum).intValue();
        Log.d("LINJUNFENG","countDown被调用");
        //调用矿工线程
        minerThread.start();

        if (c_trd == null || (c_trd != null  && !c_trd.isAlive())) {
            c_trd = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("LINJUNFENG","thread:"+Thread.currentThread().getId());
                    Message msg = Message.obtain();
                    msg.what = msgChild;
                    msg.arg1 = time;
                    parentHandler.sendMessage(msg);
                    Looper.prepare();
                    childHandler = new Handler() {
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            time--;
                            if (time >= 0) {
                                //Log.d("LINJUNFENG", "handleMessage: "+time);
                                Message message = Message.obtain();
                                message.what = msgChild;
                                message.arg1 = time;
                                parentHandler.sendMessageDelayed(message,1000);
                            }
                        }
                    };
                    Looper.loop();
                }
            });
            c_trd.start();
        }
    }
}