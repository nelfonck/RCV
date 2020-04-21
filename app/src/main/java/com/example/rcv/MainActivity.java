package com.example.rcv;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView contenidojson ;
    private EditText from, to ;
    private static final String url  = "http://201.192.158.233:82/rcv/Controllers/functions.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contenidojson = findViewById(R.id.contenidojson);
        from = findViewById(R.id.from);
        to = findViewById(R.id.to);
        ImageButton btn = findViewById(R.id.btnFiltro);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarBusqueda(from.getText().toString(),to.getText().toString());
            }
        });
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(from);
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(to);
            }
        });
    }

    public void realizarBusqueda(String from, String to){
        if (from.equals("") || to.equals("")){
            Toast.makeText(this, "Se debe establecer el rango de fechas", Toast.LENGTH_SHORT).show();
        }else{
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Obteniendo información");
            progressDialog.setMessage("Porfavor espere..");
            progressDialog.show();

            StringRequest request = new StringRequest(Request.Method.GET, url +
                    "?from=" + from + "&to=" + to, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    contenidojson.setText(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    if (error.getMessage() != null)
                    if (!error.getMessage().equals(""))
                    {
                        byte[] htmlBodyBytes = error.networkResponse.data ;
                        Toast.makeText(MainActivity.this, new String(htmlBodyBytes), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
    }

    private void showDatePickerDialog(final EditText txt) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(),"Select date");

        newFragment.SetOnSelectedDate(new DatePickerFragment.OnSelectedDate() {
            @Override
            public void OnSelected(int year, int month, int dayOfMonth) {
                String fecha = dayOfMonth + "-"+  month + "-" + year ;
                txt.setText(fecha);
            }
        });

    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private OnSelectedDate onSelectedDate ;

        public void SetOnSelectedDate(OnSelectedDate onSelectedDate){
            this.onSelectedDate = onSelectedDate;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            onSelectedDate.OnSelected(year,month+1,dayOfMonth);
        }

        public interface OnSelectedDate{
            void OnSelected(int year, int month, int dayOfMonth);
        }
    }
}


