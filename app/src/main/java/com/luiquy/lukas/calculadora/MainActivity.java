package com.luiquy.lukas.calculadora;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etidttextdatos)
    EditText etidttextdatos;
    @BindView(R.id.relativeuno)
    RelativeLayout relativeuno;

    private boolean isEditinginProgres = false;
    private int minLeght;
    private int textsize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        minLeght = getResources().getInteger(R.integer.main_min_length);
        textsize = getResources().getInteger(R.integer.main_input_textSize);
        noabrirteclado();
    }

    private void noabrirteclado() {
        /*para que no salga el teclado*/
       /* etidttextdatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });*/
        etidttextdatos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    if (event.getRawX() >= (etidttextdatos.getRight() - etidttextdatos.getCompoundDrawables()
                            [Constantes.DRAWABLE_RIGHT].getBounds().width())) {
                        if (etidttextdatos.length() > 0) {
                            final int lenght = etidttextdatos.getText().length();
                            etidttextdatos.getText().delete(lenght - 1, lenght);

                        }
                    }
                return true;
            }
                return false;
            }
        });
        etidttextdatos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isEditinginProgres && Metodos.canReplaceOperator(s)){
                    isEditinginProgres = true;
                    etidttextdatos.getText().delete(etidttextdatos.getText().length()-2 ,
                            etidttextdatos.getText().length()-1);
                }
                if (s.length() >minLeght){
                    etidttextdatos.setTextSize(TypedValue.COMPLEX_UNIT_SP, textsize - (((s.length()
                            -minLeght *2 )+ (s.length()-minLeght))));
                }else {
                    etidttextdatos.setTextSize(TypedValue.COMPLEX_UNIT_SP, textsize);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                isEditinginProgres = false;
            }
        });
    }


    @OnClick({R.id.btnsiete, R.id.btncuatro, R.id.btnuno, R.id.btnocho, R.id.btncinco, R.id.btdos, R.id.btnueve, R.id.btseis, R.id.bttres, R.id.btnpunto, R.id.btncero})
    public void onViewNumeros(View view) {
        final String valoresBTR = ((Button) view).getText().toString();
        switch (view.getId()) {
            case R.id.btnsiete:
            case R.id.btncuatro:
            case R.id.btnuno:
            case R.id.btnocho:
            case R.id.btncinco:
            case R.id.btdos:
            case R.id.btnueve:
            case R.id.btseis:
            case R.id.bttres:
            case R.id.btncero:
                etidttextdatos.getText().append(valoresBTR);
                break;
            case R.id.btnpunto:
                final String operacion = etidttextdatos.getText().toString();
                final String operador = Metodos.getOperator(operacion);
                final int count = operacion.length() - operacion.replace(".", "").length();
                if (!operacion.contains(Constantes.POINT) ||
                        count < 2 && (!operador.equals(Constantes.OPERATOR_NULL))) {
                    etidttextdatos.getText().append(valoresBTR);
                }

                break;
        }
    }

    @OnClick({R.id.btlimpiar, R.id.btndividir, R.id.btmultiplicar, R.id.btnmenos, R.id.btsuma, R.id.btresultado})
    public void onViewControl(View view) {
        switch (view.getId()) {
            case R.id.btlimpiar:
                etidttextdatos.setText("");
                break;
            case R.id.btndividir:
            case R.id.btmultiplicar:
            case R.id.btnmenos:
            case R.id.btsuma:
                resolver(false);
                final String operador= ((Button)view).getText().toString();
                final String operacion=etidttextdatos.getText().toString();

                final String ultimocaracter = operacion.isEmpty()? "" : operacion.substring(operacion.length()-1);
                if (operador.equals(Constantes.OPERATOR_SUB)){
                    if (operacion.isEmpty()||
                            (!(ultimocaracter.equals(Constantes.OPERATOR_SUB))&&
                            !(ultimocaracter.equals(Constantes.POINT)))){
                        etidttextdatos.getText().append(operador);
                    }
                }else {
                    if (!operacion.isEmpty()&&
                            !(ultimocaracter.equals(Constantes.OPERATOR_SUB))&&
                            !(ultimocaracter.equals(Constantes.POINT))){
                        etidttextdatos.getText().append(operador);
                    }
                }
                break;
            case R.id.btresultado:
                resolver(true);
                break;
        }
    }

    private void resolver(boolean fueRestaurado) {
            Metodos.tryResolve(fueRestaurado, etidttextdatos, new OnResolveCallback() {
                @Override
                public void onShowMessage(int errorRes) {
                    showmessage(errorRes);
                }

                @Override
                public void onIsEditing() {
                    isEditinginProgres = true;
                }
            });
    }

    private void showmessage(int errorRes) {
        Snackbar.make(relativeuno,errorRes,Snackbar.LENGTH_SHORT).show();
    }
}
