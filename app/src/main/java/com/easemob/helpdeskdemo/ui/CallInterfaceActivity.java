package com.easemob.helpdeskdemo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.easemob.helpdeskdemo.Constant;
import com.easemob.helpdeskdemo.Preferences;
import com.easemob.helpdeskdemo.R;
import com.hyphenate.chat.AgoraMessage;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.helpdesk.callback.ValueCallBack;
import com.hyphenate.helpdesk.util.Log;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class CallInterfaceActivity extends Activity {

    private TextView mWelcomeWordsTv;
    private TextView mMenuTv;
    private TextView mGetWelcomeMenuTv;
    private TextView mGetTwoWelcomeMenuTv;
    private String mName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_interface);
        findViewById(R.id.chatBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mName)) {
                    Toast.makeText(CallInterfaceActivity.this, "请先登录！", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(CallInterfaceActivity.this, TestLoginActivity.class);
                intent.putExtra("name", mName);
                startActivity(intent);
            }
        });

        findViewById(R.id.loginBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 登录
                Intent intent = new Intent(CallInterfaceActivity.this, TestLoginActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        EditText et = findViewById(R.id.et);
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = et.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    Toast.makeText(CallInterfaceActivity.this, "不能为空！", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(mName)) {
                    Toast.makeText(CallInterfaceActivity.this, "请先登录！", Toast.LENGTH_LONG).show();
                    return;
                }

                AgoraMessage.asyncInitLanguage(ChatClient.getInstance().tenantId(),
                        s, Preferences.getInstance().getAppKey(), mName, new ValueCallBack<String>() {
                            @Override
                            public void onSuccess(String value) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CallInterfaceActivity.this, "初始化语言成功！", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onError(int error, String errorMsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CallInterfaceActivity.this, "初始化语言失败！", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
            }
        });

        mWelcomeWordsTv = findViewById(R.id.welcomeWordsTv);
        findViewById(R.id.welcomeWordsBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mName)) {
                    Toast.makeText(CallInterfaceActivity.this, "请先登录！", Toast.LENGTH_LONG).show();
                    return;
                }

                ChatManager.getInstance().getEnterpriseWelcome(mName, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        if (isFinishing()) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWelcomeWordsTv.setText(value);
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mWelcomeWordsTv.setText("errorCode = " + error + ", errorMsg = " + errorMsg);
                            }
                        });
                    }
                });
            }
        });

        mMenuTv = findViewById(R.id.menuTv);
        findViewById(R.id.menuBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mName)) {
                    Toast.makeText(CallInterfaceActivity.this, "请先登录！", Toast.LENGTH_LONG).show();
                    return;
                }

                AgoraMessage.getSkillGroupMenuWithVisitorUserName(ChatClient.getInstance().tenantId(), mName, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        if (isFinishing()) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMenuTv.setText(value);
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mMenuTv.setText("errorCode = " + error + ", errorMsg = " + errorMsg);
                            }
                        });
                    }
                });
            }
        });


        // 访客端获取欢迎语菜单
        mGetWelcomeMenuTv = findViewById(R.id.getWelcomeMenuTv);
        findViewById(R.id.getWelcomeMenuBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mName)) {
                    Toast.makeText(CallInterfaceActivity.this, "请先登录！", Toast.LENGTH_LONG).show();
                    return;
                }

                String target = Preferences.getInstance().getCustomerAccount();
                Log.e("ppppppppppppp", "getCustomerAccount = " + target);

                ChatClient.getInstance().chatManager().getAppRelevanceSkillGroupMenuWithVisitorUserName(target, mName, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Log.e("ppppppppppppp", "value = " + value);

                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mGetWelcomeMenuTv.setText(value);
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mGetWelcomeMenuTv.setText("errorCode = " + error + ", errorMsg = " + errorMsg);
                            }
                        });

                    }
                });

            }
        });

        // 访客端获取企业欢迎语
        mGetTwoWelcomeMenuTv = findViewById(R.id.getTwoWelcomeMenuTv);
        findViewById(R.id.getTwoWelcomeBt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mName)) {
                    Toast.makeText(CallInterfaceActivity.this, "请先登录！", Toast.LENGTH_LONG).show();
                    return;
                }

                String target = Preferences.getInstance().getCustomerAccount();
                ChatClient.getInstance().chatManager().getAppRelevanceEnterpriseWelcomeWithVisitorUserName(target, mName, new ValueCallBack<String>() {
                    @Override
                    public void onSuccess(String value) {
                        Log.e("pppppppppppp", "value = " + value);
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mGetTwoWelcomeMenuTv.setText(value);
                            }
                        });
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mGetTwoWelcomeMenuTv.setText("errorCode = " + error + ", errorMsg = " + errorMsg);
                            }
                        });

                    }
                });

            }
        });
    }

    private String getRandomAccount() {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {// 字符串
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) {// 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val.toLowerCase(Locale.getDefault());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            mName = data.getStringExtra("name");
        }
    }
}
