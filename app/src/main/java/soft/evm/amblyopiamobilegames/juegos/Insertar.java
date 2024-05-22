package soft.evm.amblyopiamobilegames.juegos;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import soft.evm.amblyopiamobilegames.MainActivity;
import soft.evm.amblyopiamobilegames.R;
import soft.evm.amblyopiamobilegames.model.PartidaContract;

import static soft.evm.amblyopiamobilegames.BuildConfig.URL_PARTIDAS;

public class Insertar {

    public static String correo = "NO";
    public static String nombre;
    public static String nombre_juego;
    public static int score1;
    public static int score2;
    public static long tiempo;
    public static boolean internet;

    //AsynTask
    static SQLiteDatabase db;
    public static final String updateURLPartidas = URL_PARTIDAS;

    public static boolean insertar = false;


    public static void insertar(Context context) throws SQLiteDatabaseLockedException {
        if(insertar && !correo.equals("NO")) {
            insertar = false;
            //Abrimos en modo escritura porque se va a insertar una fila
            SQLiteDatabase db = MainActivity.partidasDBHelper_historial.getWritableDatabase();

            //Creamos mapa de valores. Los nombres de las coumnas son las keys
            ContentValues values = new ContentValues();

            //DEVICE
            /*String info="Debug-infos:";
            info += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
            info += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
            info += "\n Device and Brand: " + android.os.Build.DEVICE + " - " + Build.BRAND;
            info += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";*/
            String info = android.os.Build.MODEL;

            //DATOS
            long now = new Date().getTime() - tiempo;
            values.put(PartidaContract.Partida.COLUMN_NAME_CORREO, correo);
            values.put(PartidaContract.Partida.COLUMN_NAME_NOMBRE, nombre);
            values.put(PartidaContract.Partida.COLUMN_NAME_DIA, now);
            values.put(PartidaContract.Partida.COLUMN_NAME_JUEGO, nombre_juego);
            values.put(PartidaContract.Partida.COLUMN_NAME_S1, score1);
            values.put(PartidaContract.Partida.COLUMN_NAME_S2, score2);
            values.put(PartidaContract.Partida.COLUMN_NAME_TIEMPO, tiempo);
            values.put(PartidaContract.Partida.COLUMN_NAME_DEVICE, info);
            values.put(PartidaContract.Partida.COLUMN_NAME_SUBIDO, 0);//por defecto

            //cronometrar tiempo demo
            SharedPreferences tiempo_demo_diario = context.getSharedPreferences("TiempoDemoDiario", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = tiempo_demo_diario.edit();
            editor.putLong("tiempo", tiempo_demo_diario.getLong("tiempo", 0) + tiempo);
            editor.apply();


            //Se inserta el valor y recogemos el primary key creado
            //db.insert(PartidaContract.Partida.TABLE_NAME, null, values);

            String params[] = new String[9];
            int i = 0;
            params[i++] = correo;
            params[i++] = nombre;
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", java.util.Locale.getDefault());
            params[i++] = format.format(now);
            params[i++] = nombre_juego;
            params[i++] = String.valueOf(score1);
            params[i++] = String.valueOf(score2);
            params[i++] = String.valueOf(tiempo);
            params[i] = info;

            if(internet) {
                new InsertarPartida(values, params).start();
            }
            else{
                try {
                    Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }catch (RuntimeException e){
                    e.printStackTrace();
                }//no meterlos en el mismo try/catch (pueden fallar indistintamente)
                try {
                    values.put(PartidaContract.Partida.COLUMN_NAME_SUBIDO, 0);
                    //Se inserta el valor y recogemos el primary key creado
                    db.insert(PartidaContract.Partida.TABLE_NAME_HISTORIAL, null, values);
                    db.close();
                }catch(NullPointerException e){
                    e.printStackTrace();
                }catch(RuntimeException e){
                    e.printStackTrace();
                }
            }
        }
    }


    private static class InsertarPartida extends Thread{

        private ContentValues values;
        String[] params;

        InsertarPartida(ContentValues values , String[] params){
            this.values = values;
            this.params = params;
        }

        public void run(){
            Charset UTF_8;
            Charset iso_8859_1;
            //standar parametros
            UTF_8 = StandardCharsets.UTF_8;
            iso_8859_1 = StandardCharsets.ISO_8859_1;

            try {
                int i = 0;
                correo = params[i++];
                String nombre = params[i++];
                String fecha = params[i++];
                String nombre_juego = params[i++];
                String score1 = params[i++];
                String score2 = params[i++];
                String tiempo = params[i++];
                String dispositivo = params[i];
                URL url = new URL(updateURLPartidas);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, UTF_8));
                String post_data;
                post_data = URLEncoder.encode("correo", "UTF-8") + "=" + URLEncoder.encode(correo, "UTF-8")
                        + "&"
                        + URLEncoder.encode("nombre", "UTF-8") + "=" + URLEncoder.encode(nombre, "UTF-8")
                        + "&"
                        + URLEncoder.encode("fecha", "UTF-8") + "=" + URLEncoder.encode(fecha, "UTF-8")
                        + "&"
                        + URLEncoder.encode("juego", "UTF-8") + "=" + URLEncoder.encode(nombre_juego, "UTF-8")
                        + "&"
                        + URLEncoder.encode("score1", "UTF-8") + "=" + URLEncoder.encode(score1, "UTF-8")
                        + "&"
                        + URLEncoder.encode("score2", "UTF-8") + "=" + URLEncoder.encode(score2, "UTF-8")
                        + "&"
                        + URLEncoder.encode("tiempo", "UTF-8") + "=" + URLEncoder.encode(tiempo, "UTF-8")
                        + "&"
                        + URLEncoder.encode("dispositivo", "UTF-8") + "=" + URLEncoder.encode(dispositivo, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, iso_8859_1));
                String result = "";
                String line;
                StringBuilder sb = new StringBuilder(result);
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                JSONObject myJsonjObject = new JSONObject(result);
                String myJsonString = myJsonjObject.getString("success");
                                /*String myJsonString = myJsonjObject.getString("message");
                                alertDialog.setMessage(myJsonString);
                                alertDialog.show();*/
                if (myJsonString.equals("1"))
                    values.put(PartidaContract.Partida.COLUMN_NAME_SUBIDO, 1);
                else
                    values.put(PartidaContract.Partida.COLUMN_NAME_SUBIDO, 0);
                //Se inserta el valor y recogemos el primary key creado
                db.insert(PartidaContract.Partida.TABLE_NAME_HISTORIAL, null, values);
                db.close();
                //se vuelve al valor por defecto
                Insertar.correo = "NO";

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
                Log.e("38", "Error");
            }

        }

    }
    
}
