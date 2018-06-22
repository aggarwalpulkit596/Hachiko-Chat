package me.dats.com.datsme.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.Transition;
import com.transitionseverywhere.TransitionManager;
import com.transitionseverywhere.TransitionSet;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import me.dats.com.datsme.Activities.ProfileActivity;
import me.dats.com.datsme.Animation.Rotate;
import me.dats.com.datsme.Animation.TextSizeTransition;
import me.dats.com.datsme.R;

import static android.content.ContentValues.TAG;

public class SignUpFragment extends AuthFragment {
    @BindViews(value = {R.id.email_input_edit,
            R.id.password_input_edit,
            R.id.confirm_password_edit})
    protected List<TextInputEditText> views;
    private FirebaseAuth mAuth;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            mAuth = FirebaseAuth.getInstance();
            caption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signUp();
                }
            });
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_sign_up));
            caption.setText(getString(R.string.sign_up_label));
            for (final TextInputEditText editText : views) {
                if (editText.getId() == R.id.password_input_edit) {
                    final TextInputLayout inputLayout = ButterKnife.findById(view, R.id.password_input);
                    final TextInputLayout confirmLayout = ButterKnife.findById(view, R.id.confirm_password);
                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    inputLayout.setTypeface(boldTypeface);
                    confirmLayout.setTypeface(boldTypeface);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            inputLayout.setPasswordVisibilityToggleEnabled(s.length() > 0);

                        }
                    });
                }
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            boolean isEnabled = editText.getText().length() > 0;
                            editText.setSelected(isEnabled);
                        }
                    }
                });
            }
            caption.setVerticalText(true);
            foldStuff();
            caption.setTranslationX(getTextPadding());
        } else {
            caption.setOnClickListener(null);
        }
    }

    public boolean validate() {
        boolean valid = true;
        if (views.get(0).getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(views.get(0).getText().toString()).matches()) {
            views.get(0).setError("enter a valid email address");
            valid = false;
        } else {
            views.get(0).setError(null);
        }


        if (views.get(1).getText().toString().isEmpty() || views.get(1).length() < 4 || views.get(1).length() > 10) {
            views.get(1).setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            views.get(1).setError(null);
        }

        if (!(views.get(2).getText().toString().equals(views.get(1).getText().toString()))) {
            views.get(2).setError(views.get(2).getText().toString());

            valid = false;
        }

        return valid;
    }


    public void signUp() {


        if (!validate()) {
            Toast.makeText(getContext(), "SignUp failed", Toast.LENGTH_LONG).show();
            return;
        } else {
            caption.setEnabled(false);
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(views.get(0).getText().toString(), views.get(1).getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        progressDialog.dismiss();
                        user.sendEmailVerification();
                        startActivity(new Intent(getActivity(), ProfileActivity.class));
                        getActivity().finish();
                    } else {
                        progressDialog.dismiss();

                        Toast.makeText(getActivity(), "Could not create account. Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int authLayout() {
        return R.layout.sign_up;
    }

    @Override
    public void clearFocus() {
        for (View view : views) view.clearFocus();
    }

    @Override
    public void fold() {
        lock = false;
        Rotate transition = new Rotate();
        transition.setEndAngle(-90f);
        transition.addTarget(caption);
        TransitionSet set = new TransitionSet();
        set.setDuration(300);
        ChangeBounds changeBounds = new ChangeBounds();
        set.addTransition(changeBounds);
        set.addTransition(transition);
        TextSizeTransition sizeTransition = new TextSizeTransition();
        sizeTransition.addTarget(caption);
        set.addTransition(sizeTransition);
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set.addListener(new Transition.TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                caption.setTranslationX(getTextPadding());
                caption.setRotation(0);
                caption.setVerticalText(true);
                caption.requestLayout();

            }
        });
        TransitionManager.beginDelayedTransition(parent, set);
        foldStuff();
        caption.setTranslationX(-caption.getWidth() / 8 + getTextPadding());
    }

    private void foldStuff() {
        caption.setTextSize(TypedValue.COMPLEX_UNIT_PX, caption.getTextSize() / 2f);
        caption.setTextColor(Color.WHITE);
        ConstraintLayout.LayoutParams params = getParams();
        params.rightToRight = ConstraintLayout.LayoutParams.UNSET;
        params.verticalBias = 0.5f;
        caption.setLayoutParams(params);
    }

    private float getTextPadding() {
        return getResources().getDimension(R.dimen.folded_label_padding) / 2.1f;
    }
}
