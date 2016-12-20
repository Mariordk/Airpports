package com.example.mario.airpports;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by Mario on 20/12/2016.
 */

public class FlightFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = FlightFragment.class.getSimpleName();

    private SimpleCursorAdapter mAdapter;

    private static final String[] FROM = {FlightContract.Column.NUMERO_VUELO,
            FlightContract.Column.FECHA_VUELO, FlightContract.Column.FECHA_CONSULTA};
    private static final int[] TO = {R.id.list_item_numero_vuelo,
            R.id.list_item_fecha_vuelo, R.id.list_item_fecha_consulta};

    private static final int LOADER_ID = 42;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item, null, FROM, TO, 0);
        mAdapter.setViewBinder(new TimelineViewBinder());
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id != LOADER_ID)
            return null;
        Log.d(TAG, "onCreateLoader");
        return new CursorLoader(getActivity(), FlightContract.CONTENT_URI, null,
                null, null, FlightContract.DEFAULT_SORT);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished with cursor: " + cursor.getCount());
        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    /** Inner class. */
    class TimelineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int
                columnIndex) {
            if (view.getId() != R.id.list_item_fecha_consulta)
                return false;
            // Convertimos timestamp a tiempo relativo
            long timestamp = cursor.getLong(columnIndex);
            CharSequence relativeTime =
                    DateUtils.getRelativeTimeSpanString(timestamp);
            ((TextView) view).setText(relativeTime);
            return true;
        }
    }
}

