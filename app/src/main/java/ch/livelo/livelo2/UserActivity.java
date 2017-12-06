package ch.livelo.livelo2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ch.livelo.livelo2.DB.UserDAO;

public class UserActivity extends AppCompatActivity {
    private TextView tv_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        tv_user = (TextView) findViewById(R.id.tv_user);

        UserDAO userDAO = new UserDAO(this);
        userDAO.open();
        tv_user.setText("Logged in as: " + userDAO.getEmail());
        userDAO.close();
    }

    public void goToLogout(View view) {
        UserDAO userDAO = new UserDAO(this);
        userDAO.open();
        userDAO.logOut();
        userDAO.close();
        startActivity(new Intent(this, LoginActivity.class));
        // TODO effacer aussi les capteurs et les data + preferences?
        finish();
    }
}