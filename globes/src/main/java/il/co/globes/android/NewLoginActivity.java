package il.co.globes.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.AppEventsLogger;
import il.co.globes.android.fragments.PortFolioFragment;
import il.co.globes.android.objects.DataContext;
import il.co.globes.android.objects.GlobesURL;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewLoginActivity extends Activity {

    Context context;
    private static final String USER_NAME = "user_name";
    private static final String NEW_USER_NAME = "new_user_name";

    private static final String PREFS_NAME = "preferences";
    private String userLogin = "";
    private String userPassword = "";
    private String accessKey = "";
    private String eMail = "";
    private String remoteErrorMessage = null;

    private enum UserType {
        REGISTERED,
        NEW
    }


    private Handler handler;
    private HttpResponse response;
    Resources myResources;

    TextView text_new_user, text_existing_user, textview_connect_new_user, text_remember_me, textview_connect;
    LinearLayout layout_existing_User, layout_new_User;
    View header_left_bottom_view, header_right_bottom_view;
    Typeface almoni_aaa_regular, almoni_aaa_light, almoni_aaa_blod, almoni_aaa_black;

    EditText editText_mail, editText_username, editText__new_user_mail, editText__new_user_username, editText__new_user_password,
            editText__new_user__comfirm_password;
    CheckBox check_box_accept_term_of_use, check_box_remember_me;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.a_new_login);
        myResources = getResources();
        handler = new Handler();
        almoni_aaa_regular = Typeface.createFromAsset(myResources.getAssets(), "almoni-dl-aaa-regular.otf");
        almoni_aaa_blod = Typeface.createFromAsset(myResources.getAssets(), "almoni-dl-aaa-bold.otf");

        setExistingUserUI();

    }

    private void setExistingUserUI() {
        check_box_remember_me = (CheckBox) findViewById(R.id.check_box_remember_me);

        check_box_remember_me.setChecked(true);
        check_box_accept_term_of_use = (CheckBox) findViewById(R.id.check_box_accept_term_of_use);
        text_existing_user = (TextView) findViewById(R.id.text_existing_user);
        textview_connect_new_user = (TextView) findViewById(R.id.textview_connect_new_user);
        text_remember_me = (TextView) findViewById(R.id.text_remember_me);
        text_remember_me.setTypeface(almoni_aaa_regular);
        textview_connect = (TextView) findViewById(R.id.textview_connect);
        textview_connect.setTypeface(almoni_aaa_regular);
        header_left_bottom_view = (View) findViewById(R.id.header_left_bottom_view);
        header_right_bottom_view = (View) findViewById(R.id.header_right_bottom_view);
        header_right_bottom_view.setVisibility(View.GONE);
        text_new_user = (TextView) findViewById(R.id.text_new_user);

        editText_mail = (EditText) findViewById(R.id.editText_mail);
        editText_mail.setText(Utils.getStringFromShardPref(this, USER_NAME));
        editText_mail.setTypeface(almoni_aaa_regular);

        editText_username = (EditText) findViewById(R.id.editText_username);
        editText_username.setTypeface(almoni_aaa_regular);

        editText__new_user_mail = (EditText) findViewById(R.id.editText__new_user_mail);
        editText__new_user_mail.setTypeface(almoni_aaa_regular);
        editText__new_user_username = (EditText) findViewById(R.id.editText__new_user_username);
        editText__new_user_username.setTypeface(almoni_aaa_regular);
        editText__new_user_password = (EditText) findViewById(R.id.editText__new_user_password);
        editText__new_user_password.setTypeface(almoni_aaa_regular);
        editText__new_user__comfirm_password = (EditText) findViewById(R.id.editText__new_user__comfirm_password);

        editText__new_user__comfirm_password.setTypeface(almoni_aaa_regular);
        layout_existing_User = (LinearLayout) findViewById(R.id.layout_existing_User);
        layout_new_User = (LinearLayout) findViewById(R.id.layout_new_User);
        layout_new_User.setVisibility(View.GONE);
        layout_existing_User.setVisibility(View.VISIBLE);
        text_existing_user.setTextColor(myResources.getColor(R.color.Black));
        text_existing_user.setTypeface(almoni_aaa_regular);
        text_new_user.setTextColor(myResources.getColor(R.color.grey_header_text_log_in));
        text_new_user.setTypeface(almoni_aaa_regular);

        text_new_user.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                header_left_bottom_view.setVisibility(View.GONE);
                header_right_bottom_view.setVisibility(View.VISIBLE);
                layout_new_User.setVisibility(View.VISIBLE);
                layout_existing_User.setVisibility(View.GONE);
                text_new_user.setTextColor(myResources.getColor(R.color.Black));
                text_existing_user.setTextColor(myResources.getColor(R.color.grey_header_text_log_in));
                setNewUserUI();

            }
        });

        text_existing_user.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                header_left_bottom_view.setVisibility(View.VISIBLE);
                header_right_bottom_view.setVisibility(View.GONE);
                layout_new_User.setVisibility(View.GONE);
                layout_existing_User.setVisibility(View.VISIBLE);
                text_existing_user.setTextColor(myResources.getColor(R.color.Black));
                text_new_user.setTextColor(myResources.getColor(R.color.grey_header_text_log_in));
            }
        });

        textview_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.putStringInShardPref(NewLoginActivity.this, USER_NAME, editText_mail.getText().toString());

                if (existingUserInputValid()) {
                    attemptLogIn();

                    // if(accessKey.length() > 2)/////////////////////////////
                    // {
                    // setAccessKey();
                    // setRememberUserPrefValue();
                    // backToActivity();
                    // }
                }
            }

            private boolean existingUserInputValid() {
                boolean isInputValid = false;

                userLogin = editText_mail.getText().toString();

                if (userLogin.length() == 0) {
                    editText_mail.setError(myResources.getString(R.string.login_email_error));
                } else {

                    userPassword = editText_username.getText().toString();
                    if (userPassword.length() == 0) {
                        editText_username.setError(myResources.getString(R.string.login_password_fill));
                    } else {
                        isInputValid = true;
                    }
                }

                return isInputValid;
            }

        });

    }

    protected void setNewUserUI() {

        // etNewUserName.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // LinearLayout loAcceptTerms = (LinearLayout)
        // findViewById(R.id.loAcceptTerms);
        // loAcceptTerms.setVisibility(View.VISIBLE);

        textview_connect_new_user.setOnClickListener(new View.OnClickListener() {

            int ErrMsgIndx = 0;

            @Override
            public void onClick(View v) {
                Utils.putStringInShardPref(NewLoginActivity.this, USER_NAME, editText__new_user_mail.getText().toString());

                if (newUserInputValid()) {

                    String nameOfUser = editText__new_user_password.getText().toString();

                    // boolean registerSuccess = false;
                    // registerSuccess = registerNewUser(nameOfUser);

                    registerNewUser(nameOfUser);

                    // if(registerSuccess)
                    // {
                    // notifyUser("äåãòú îòøëú",
                    // "áëãé ìñééí àú úäìéê äøéùåí ìàúø âìåáñ " +
                    // "àðà òáåø ìúéáú äãåà\"ì ùìê åìçõ òì ä÷éùåø äîåôéò áäåãòä ùðùìçä àìéê "
                    // +
                    // "áîéãä åìà ÷éáìú äåãòú îééì îâìåáñ ìúéáú äãåàø ùìê, éù ìäú÷ùø ìîçì÷ú äùéøåú "
                    // +
                    // "áèìôåï 03-9538666 " +
                    // "\nàå ìðñåú åìäéøùí ìùéøåú áùðéú\n " +
                    // "\núåãä " +
                    // "\n\nöååú âìåáñ ", true);
                    // }
                    // else
                    // {
                    // if(remoteErrorMessage != null)
                    // {
                    // notifyUser("ùâéàä áîòøëú", remoteErrorMessage, false);
                    // }
                    // else
                    // {
                    // notifyUser("àøòä ùâéàä áîòøëú", "", false);
                    // }
                    //
                    // resetInputs(UserType.NEW);
                    // }
                }
            }

            private boolean newUserInputValid() {
                boolean resultOfTest = false;
                ErrMsgIndx = 0;

                // EditText etNewUserPwd1 =
                // (EditText)findViewById(R.id.etNewUserPwd);

                eMail = editText__new_user_mail.getText().toString();

                String[] ErrMsgs =
                        {"", myResources.getString(R.string.login_email_error), myResources.getString(R.string.login_password_fill),
                                myResources.getString(R.string.login_password_guide), myResources.getString(R.string.login_password_error),
                                myResources.getString(R.string.login_terms_of_use_error), myResources.getString(R.string.login_system_error)};

                if (!isValidEmail(eMail)) {
                    ErrMsgIndx = 1;
                    setEditTextMessage(editText__new_user_mail, ErrMsgs[ErrMsgIndx]);
                }

                if (ErrMsgIndx == 0) {

                    userPassword = editText__new_user_password.getText().toString();

                    if (userPassword.length() == 0) {
                        ErrMsgIndx = 2;
                        setEditTextMessage(editText__new_user_password, ErrMsgs[ErrMsgIndx]);
                    }

                    if (ErrMsgIndx == 0) {
                        if (!isValidPassword(userPassword)) {
                            ErrMsgIndx = 3;
                            setEditTextMessage(editText__new_user_password, ErrMsgs[ErrMsgIndx]);
                        }

                        if (ErrMsgIndx == 0) {

                            String password2 = editText__new_user__comfirm_password.getText().toString();

                            if (!userPassword.equalsIgnoreCase(password2)) {
                                ErrMsgIndx = 4;
                                setEditTextMessage(editText__new_user__comfirm_password, ErrMsgs[ErrMsgIndx]);
                            }
                        }
                    }
                }

                if (ErrMsgIndx == 0) {

                    if (!check_box_accept_term_of_use.isChecked()) {
                        ErrMsgIndx = 5;
                        notifyUser(ErrMsgs[ErrMsgIndx], "", false);
                    }
                }

                if (ErrMsgIndx == 0) {
                    resultOfTest = true;
                }

                return resultOfTest;
            }

            // private boolean registerNewUser(String nameOfUser)
            // {
            // boolean userWasRegistered = false;
            // HttpClient client = new DefaultHttpClient();
            // HttpPost post = new
            // HttpPost("http://www.globes.co.il/data/webservices/login.asmx/register");
            // try
            // {
            // List<NameValuePair> nameValuePairs = new
            // ArrayList<NameValuePair>(1);
            //
            // nameValuePairs.add(new BasicNameValuePair("email", eMail));
            // nameValuePairs.add(new BasicNameValuePair("password",
            // userPassword));
            // nameValuePairs.add(new BasicNameValuePair("name", nameOfUser));
            // nameValuePairs.add(new BasicNameValuePair("application", ""));
            //
            // post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // HttpResponse response = client.execute(post);
            //
            // XmlPullParser parser = Xml.newPullParser();
            // getRegistrationResult(parser, response.getEntity().getContent());
            //
            // if (remoteErrorMessage == null)
            // {
            // if (accessKey != null)
            // {
            // userWasRegistered = true;
            // }
            // }
            // }
            // catch (IOException e)
            // {
            // e.printStackTrace();
            // }
            //
            // return userWasRegistered;
            // }
        });

        final TextView descriptionTextView = (TextView) findViewById(R.id.tvAcceptTerms);
        final Spannable span = Spannable.Factory.getInstance()
                .newSpannable(myResources.getString(R.string.login_terms_of_use_confirmation));

        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent showTOU = new Intent(NewLoginActivity.this, TermsOfUse.class);
                startActivity(showTOU);
            }
        }, 13, 24, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // 13 and 24 are indices
        // of start and end of
        // the link
        descriptionTextView.setText(span);
        descriptionTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected void setEditTextMessage(EditText target, String message) {
        target.setFocusableInTouchMode(true);
        target.requestFocus();
        target.setError(message);
    }

    protected void logOutUser() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("globesAccessKey", "");
        editor.commit();
        DataContext.Instance().setAccessKey("");

        startActivity(new Intent(this, NewLoginActivity.class));
        finish();
    }

    public void registerNewUser(final String nameOfUser) {
        Runnable runnable = new Runnable() {
            boolean userWasRegistered = false;
            final HttpClient client = new DefaultHttpClient();
            final HttpPost post = new HttpPost("http://www.globes.co.il/data/webservices/login.asmx/register");

            @Override
            public void run() {
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                    nameValuePairs.add(new BasicNameValuePair("email", eMail));
                    nameValuePairs.add(new BasicNameValuePair("password", userPassword));
                    nameValuePairs.add(new BasicNameValuePair("name", nameOfUser));
                    nameValuePairs.add(new BasicNameValuePair("application", ""));
                    String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

                    nameValuePairs.add(new BasicNameValuePair("unique_id", android_id));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = client.execute(post);

                    XmlPullParser parser = Xml.newPullParser();
                    getRegistrationResult(parser, response.getEntity().getContent());

                    if (remoteErrorMessage == null) {
                        if (accessKey != null) {
                            userWasRegistered = true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (userWasRegistered) {
                            String notifyUser = myResources.getString(R.string.login_in_order_to_end_registartion_part_a)
                                    + myResources.getString(R.string.login_in_order_to_end_registartion_part_b);
                            notifyUser = notifyUser.replace("*", "\n");

                            notifyUser(myResources.getString(R.string.login_system_notice), notifyUser, true);
                        } else {
                            if (remoteErrorMessage != null) {
                                notifyUser(myResources.getString(R.string.system_error), remoteErrorMessage, false);
                            } else {
                                notifyUser(myResources.getString(R.string.login_system_error), "", false);
                            }

                            resetInputs(UserType.NEW);
                        }
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    protected void getRegistrationResult(XmlPullParser parser, InputStream content) {
        try {
            parser.setInput(content, null);
            int eventType = parser.getEventType();
            boolean done = false;

            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();

                        if (name.equalsIgnoreCase("error")) {
                            remoteErrorMessage = parser.nextText();

                            done = true;
                        } else if (name.equalsIgnoreCase("key")) {
                            accessKey = parser.nextText();

                            done = true;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        done = true;

                        break;
                }

                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // private void setRememberUserCheckBox()
    // {
    // SharedPreferences prefs =
    // PreferenceManager.getDefaultSharedPreferences(this);
    // boolean rememberUser = prefs.getBoolean("rememberUser", false);
    // if(rememberUser)
    // {
    // CheckBox chkRememberUser = (CheckBox)findViewById(R.id.chkRememberUser);
    // chkRememberUser.setChecked(true);
    // }
    // }

    public void attemptLogIn() {
        Runnable runnable = new Runnable() {
            final HttpClient client = new DefaultHttpClient();
            final HttpPost post = new HttpPost(GlobesURL.URLloginSignIn);

            @Override
            public void run() {
                try {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                    // nameValuePairs.add(new BasicNameValuePair("op",
                    // "sign_in"));
                    nameValuePairs.add(new BasicNameValuePair("login_id", userLogin));
                    nameValuePairs.add(new BasicNameValuePair("password", userPassword));
                    nameValuePairs.add(new BasicNameValuePair("application", ""));
                    String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

                    nameValuePairs.add(new BasicNameValuePair("unique_id", android_id));
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    response = client.execute(post);

                    // extractAccessKey(response.getEntity().getContent());
                } catch (IOException e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            extractAccessKey(response.getEntity().getContent());

                            if (accessKey.length() > 2) {
                                setAccessKey();
                                setRememberUserPrefValue();
                                backToActivity();
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    private void extractAccessKey(InputStream is) {
        final String KEY = "key";
        final String ERROR_MESSAGE = "message";

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            boolean done = false;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if (name.equalsIgnoreCase(KEY)) {
                            this.accessKey = parser.getAttributeValue(null, "access_key");

                            done = true;
                        } else if (name.equalsIgnoreCase(ERROR_MESSAGE)) {
                            notifyUser("דואר אלקטרוני/סיסמה שגויים", "", false);
                            resetInputs(UserType.REGISTERED);

                            done = true;
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        done = true;

                        break;
                }

                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyUser(String messageTitle, String messageContent, final boolean doLogout) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(messageTitle).setCancelable(false).setMessage(messageContent)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        if (doLogout) {
                            logOutUser(); // cancel previous user cookie, if
                            // any; return to existing user
                            // login page
                        }

                    }

                });
        // builder.create().show();
        AlertDialog dialog = builder.show();
        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.RIGHT);
    }

    protected void resetInputs(UserType user) {
//		EditText editTxtUserEmail = (EditText) findViewById(R.id.editText_mail);
//		editTxtUserEmail.setText("");
        switch (user) {
            case REGISTERED:
                EditText editTextUserPwd = (EditText) findViewById(R.id.editText_username);
                editTextUserPwd.setText("");

                break;
            case NEW:

                EditText etNewUserPwd = (EditText) findViewById(R.id.editText__new_user_password);
                etNewUserPwd.setText("");

                EditText etNewUserPwd2 = (EditText) findViewById(R.id.editText__new_user__comfirm_password);
                etNewUserPwd2.setText("");

                EditText etNewUserName = (EditText) findViewById(R.id.editText__new_user_mail);
                etNewUserName.setText("");

                break;
        }
    }

    public void setAccessKey() {
        DataContext.Instance().setAccessKey(accessKey);
        // CheckBox chkRememberUser = (CheckBox)
        // findViewById(R.id.chkRememberUser);

        if (check_box_remember_me.isChecked()) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("globesAccessKey", accessKey);

            editor.commit();
        }
    }

    protected void setRememberUserPrefValue() {
        // CheckBox chkRememberUser = (CheckBox)
        // findViewById(R.id.chkRememberUser);
        if (check_box_remember_me.isChecked()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Editor editor = prefs.edit();
            editor.putBoolean("rememberUser", true);

            editor.commit();
        }
    }

    protected void backToActivity() {
        String shareID = "";
        Intent openCallingActivity;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            shareID = extras.getString("shareId");
        }
        if (shareID.length() == 0) {
            String globesAccessKey = DataContext.Instance().getAccessKey();
            // fix to replace all + with %2B to prevent login error
            while (globesAccessKey.indexOf('+') > -1) {
                globesAccessKey = globesAccessKey.replace("+", "%2B");
            }
            // openCallingActivity = new Intent().setClass(this,
            // PortfolioActivity.class);
            // openCallingActivity.putExtra("URI",
            // GlobesURL.URLPortfolio.replace("XXXX", globesAccessKey));
            //
            // openCallingActivity.putExtra(Definitions.CALLER,
            // Definitions.MAINSCREEN);
            // openCallingActivity.putExtra(Definitions.PARSER,
            // Definitions.PORTFOLIO);

            // create the fragment
            PortFolioFragment fragment = new PortFolioFragment();

            // set bundle to fragment
            Bundle args = new Bundle();
            args.putString(Definitions.URI_TO_PARSE, GlobesURL.URLPortfolio.replace("XXXX", globesAccessKey));
            args.putString(Definitions.CALLER, Definitions.MAINSCREEN);
            args.putString(Definitions.PARSER, Definitions.PORTFOLIO);
            args.putString(Definitions.HEADER, "תיק אישי");
            fragment.setArguments(args);

            MainSlidingActivity.getMainSlidingActivity().switchContent(fragment, PortFolioFragment.class.getSimpleName(), true, false, false);
            setResult(-2);
            NewLoginActivity.this.finish();

        } else {
            String feederID = extras.getString("feederId");
            if (feederID == null) feederID = "";

            // String uri = GlobesURL.URLSharePage.replace("XXXX",
            // feederID).replace("YYYY", extras.getString("shareId"));
            //
            // openCallingActivity = new Intent(this, ShareActivity.class);
            // openCallingActivity.putExtra("URI", uri);
            //
            // openCallingActivity.putExtra(Definitions.CALLER,
            // Definitions.PORTFOLIO);
            // openCallingActivity.putExtra(Definitions.PARSER,
            // Definitions.SHARES);
            //
            // openCallingActivity.putExtra(Definitions.ISINSTRUMENT,
            // extras.getBoolean(Definitions.ISINSTRUMENT, false));
            // openCallingActivity.putExtra("feederId",
            // extras.getString("feederId"));
            // openCallingActivity.putExtra("shareId",
            // extras.getString("shareId"));
            // openCallingActivity.putExtra("name", extras.getString("name"));
            // openCallingActivity.putExtra("isPfItem",
            // extras.getString("isPfItem"));

            // //test////////

            // PortFolioFragment fragment = new PortFolioFragment();
            //
            // String uri = GlobesURL.URLSharePage.replace("XXXX",
            // feederID).replace("YYYY", extras.getString("shareId"));
            //
            // // set bundle to fragment
            // Bundle args = new Bundle();
            // args.putString(Definitions.URI_TO_PARSE, uri);
            // args.putString(Definitions.CALLER, Definitions.PORTFOLIO);
            // args.putString(Definitions.PARSER, Definitions.SHARES);
            // args.putString(Definitions.HEADER, "תיק אישי");
            //
            // fragment.setArguments(args);
            //
            // MainSlidingActivity.getMainSlidingActivity().switchContent(fragment,
            // PortFolioFragment.class.getSimpleName(), true, false);
            // NewLoginActivity.this.finish();
            // /////end test///////

            String globesAccessKey = DataContext.Instance().getAccessKey();
            // fix to replace all + with %2B to prevent login error
            while (globesAccessKey.indexOf('+') > -1) {
                globesAccessKey = globesAccessKey.replace("+", "%2B");
            }
            PortFolioFragment fragment = new PortFolioFragment();

            // set bundle to fragment
            Bundle args = new Bundle();
            args.putString(Definitions.URI_TO_PARSE, GlobesURL.URLPortfolio.replace("XXXX", globesAccessKey));
            args.putString(Definitions.CALLER, Definitions.MAINSCREEN);
            args.putString(Definitions.PARSER, Definitions.PORTFOLIO);
            args.putString(Definitions.HEADER, "תיק אישי");
            fragment.setArguments(args);

            MainSlidingActivity.getMainSlidingActivity().switchContent(fragment, PortFolioFragment.class.getSimpleName(), true, false, false);
            setResult(-2);

            NewLoginActivity.this.finish();
        }

        // startActivity(openCallingActivity);
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = pattern.matcher(email);

        if (!matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidPassword(String password) {
        boolean passedTest = true;

        if (password.length() < 6) {
            passedTest = false;
        } else {
            String rexHasOnlyAbcAndNumbers = "^[a-zA-Z0-9]+$";
            String rexHasNoAbc = "^[^a-zA-Z]*$";
            String rexHasNoNumbers = "^[^0-9]*$";

            Pattern pattern = Pattern.compile(rexHasOnlyAbcAndNumbers);
            Matcher matcher = pattern.matcher(password);

            if (!matcher.find()) {
                passedTest = false;
            }

            if (passedTest) {
                pattern = Pattern.compile(rexHasNoAbc);
                if (matcher.find()) {
                    passedTest = false;
                }
            }

            if (passedTest) {
                pattern = Pattern.compile(rexHasNoNumbers);
                if (matcher.find()) {
                    passedTest = false;
                }
            }
        }

        return passedTest;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UtilsWebServices.checkInternetConnection(context)) {
            AppEventsLogger.activateApp(context, Definitions.FACEBOOK_APP_ID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
//        EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }
}
