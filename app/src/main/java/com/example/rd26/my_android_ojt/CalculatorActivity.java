package com.example.rd26.my_android_ojt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorActivity extends AppCompatActivity {

    private Button calculatorAdd, calculatorSub, calculatorMul, calculatorDiv;
    private TextView calculatorText;
    private String numOne, numTwo, operationChar;
    private boolean pressOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        processView();
        processController();
        initVariable();
    }

    // 初始化變數
    private void initVariable() {
        numOne = "";
        numTwo = "";
        operationChar = "";
        pressOperation = false;
    }

    // findViewById
    private void processView() {
        calculatorText = (TextView) findViewById(R.id.calculator_result);
        calculatorAdd = (Button)findViewById(R.id.calculator_add);
        calculatorSub = (Button)findViewById(R.id.calculator_sub);
        calculatorMul = (Button)findViewById(R.id.calculator_mul);
        calculatorDiv = (Button)findViewById(R.id.calculator_div);
    }

    // 處理監聽事件或其他初始條件
    private void processController() {

        // 四則運算監聽物件
        View.OnClickListener operationListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();

                if (! pressOperation && ! operationChar.equals("")) {
                    // 如果運算符號未被按過且已經儲存了所按過的運算符號
                    // 進到此判斷會計算之前所按過的數值
                    numTwo = calculatorText.getText().toString();
                    numOne = calNumber(numOne, numTwo, operationChar);
                }else {
                    numOne = calculatorText.getText().toString();
                }
                switch (id) {
                    case R.id.calculator_add:
                        operationChar = "+";
                        break;
                    case R.id.calculator_sub:
                        operationChar = "-";
                        break;
                    case R.id.calculator_mul:
                        operationChar = "*";
                        break;
                    case R.id.calculator_div:
                        operationChar = "/";
                        break;
                }
                pressOperation = true;
            }
        };

        // 設定監聽事件
        calculatorAdd.setOnClickListener(operationListener);
        calculatorSub.setOnClickListener(operationListener);
        calculatorMul.setOnClickListener(operationListener);
        calculatorDiv.setOnClickListener(operationListener);
    }


    // 數字按鈕處理
    public void numberClick(View v) {
        int id = v.getId();
        // 如果初始值為0,清空
        if (calculatorText.getText().toString().equals("0")) {
            calculatorText.setText("");
        }else if(pressOperation) {
            // 如果有點過運算式，清空
            calculatorText.setText("");
            pressOperation = false;
        }
        switch (id) {
            case R.id.calculator_num_one:
                calculatorText.append("1");
                break;
            case R.id.calculator_num_two:
                calculatorText.append("2");
                break;
            case R.id.calculator_num_three:
                calculatorText.append("3");
                break;
            case R.id.calculator_num_four:
                calculatorText.append("4");
                break;
            case R.id.calculator_num_five:
                calculatorText.append("5");
                break;
            case R.id.calculator_num_six:
                calculatorText.append("6");
                break;
            case R.id.calculator_num_seven:
                calculatorText.append("7");
                break;
            case R.id.calculator_num_eight:
                calculatorText.append("8");
                break;
            case R.id.calculator_num_nine:
                calculatorText.append("9");
                break;
            case R.id.calculator_num_zero:
                calculatorText.append("0");
                break;
        }
    }

    // 清除資料
    public void numberClear(View v) {
        calculatorText.setText("0");
        initVariable();
    }

    // 取得資料
    public void numberResult(View v) {
        if (! pressOperation) {
            numTwo = calculatorText.getText().toString();
            numOne = calNumber(numOne, numTwo, operationChar);
            pressOperation = true;
        }
    }

    // 計算結果
    private String calNumber(String numberOne, String numberTwo, String numOperation) {
        float resultNumber = 0f;
        switch (numOperation) {
            case "+":
                resultNumber = Float.parseFloat(numberOne) + Float.parseFloat(numberTwo);
                break;
            case "-":
                resultNumber = Float.parseFloat(numberOne) - Float.parseFloat(numberTwo);
                break;
            case "*":
                resultNumber = Float.parseFloat(numberOne) * Float.parseFloat(numberTwo);
                break;
            case "/":
                resultNumber = Float.parseFloat(numberOne) / Float.parseFloat(numberTwo);
                break;
        }
        calculatorText.setText(String.valueOf(resultNumber));
        return String.valueOf(resultNumber);
    }
}
