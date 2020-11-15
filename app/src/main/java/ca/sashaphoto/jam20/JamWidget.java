package ca.sashaphoto.jam20;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class JamWidget extends AppWidgetProvider {
    private static final JamBackend backend = JamBackend.build();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.jam_widget);
        if(backend.hasSuggestion()) {
            AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... strings) {

                    return backend.getCurrentSuggestion();
                }

                @Override
                protected void onPostExecute(String s) {
                    views.setTextViewText(R.id.widget_text_view, s);
                }
            };
            task.execute();
        }else{

            AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... strings) {

                    return backend.fetchNewSuggestion();
                }

                @Override
                protected void onPostExecute(String s) {
                    views.setTextViewText(R.id.widget_text_view, s);
                }
            };
            task.execute();
        }
        //views.setOnClickResponse(R.);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            RemoteViews remoteV = new RemoteViews(context.getPackageName(), R.layout.jam_widget);

        /*    Intent intentSync = new Intent(context, JamWidget.class);
            intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE); //You need to specify the action for the intent. Right now that intent is doing nothing for there is no action to be broadcasted.
            PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT); //You need to specify a proper flag for the intent. Or else the intent will become deleted.
            remoteV.setOnClickPendingIntent(R.id.widget_text_view,pendingSync);*/

            appWidgetManager.updateAppWidget(appWidgetId, remoteV);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.jam_widget);
            if(backend.hasSuggestion()) {
                views.setTextViewText(R.id.widget_text_view, backend.getCurrentSuggestion());
                @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... strings) {

                        return backend.getCurrentSuggestion();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        views.setTextViewText(R.id.widget_text_view, s);
                    }
                };
                task.execute();
            }else{

                @SuppressLint("StaticFieldLeak") AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... strings) {

                        return backend.fetchNewSuggestion();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        views.setTextViewText(R.id.widget_text_view, s);
                    }
                };
                task.execute();
            }
            //views.setOnClickResponse(R.);

            try {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setComponent(new ComponentName(context.getPackageName(), MainActivity.class.getName()));
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, 0);
                views.setOnClickPendingIntent(R.id.widget_text_view, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(),
                        "There was a problem loading the application: ",
                        Toast.LENGTH_SHORT).show();
            }
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

        /*for (int appWidgetId : appWidgetIds) {


        }*/

    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void openApp(View view){

    }
}