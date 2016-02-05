package edu.gatech.ppl.cycleatlanta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import edu.gatech.ppl.cycleatlanta.region.ObaRegionsLoader;
import edu.gatech.ppl.cycleatlanta.region.elements.ObaRegion;

public class FragmentUserInfo extends Fragment implements
        LoaderManager.LoaderCallbacks<ArrayList<ObaRegion>> {

	public final static int PREF_AGE = 1;
	public final static int PREF_ZIPHOME = 2;
	public final static int PREF_ZIPWORK = 3;
	public final static int PREF_ZIPSCHOOL = 4;
	public final static int PREF_EMAIL = 5;
	public final static int PREF_GENDER = 6;
	public final static int PREF_CYCLEFREQ = 7;
	public final static int PREF_ETHNICITY = 8;
	public final static int PREF_INCOME = 9;
	public final static int PREF_RIDERTYPE = 10;
	public final static int PREF_RIDERHISTORY = 11;

    private static final String RELOAD = ".reload";

    private Spinner regionSpinner;

    private List<ObaRegion> mObaRegions;

    private boolean mLoaderCheck = false;

	public FragmentUserInfo() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.activity_user_info,
				container, false);
		final Button GetStarted = (Button) rootView
				.findViewById(R.id.buttonGetStarted);
		GetStarted.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String tutorialUrl = Application.get().getCurrentRegion().getTutorialUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
                        .parse(tutorialUrl));
                startActivity(browserIntent);
            }
        });

		SharedPreferences settings = getActivity().getSharedPreferences(
				"PREFS", 0);
		Map<String, ?> prefs = settings.getAll();
		for (Entry<String, ?> p : prefs.entrySet()) {
			int key = Integer.parseInt(p.getKey());

			switch (key) {
			case PREF_AGE:
				((Spinner) rootView.findViewById(R.id.ageSpinner))
						.setSelection(((Integer) p.getValue()).intValue());
				break;
			case PREF_ETHNICITY:
				((Spinner) rootView.findViewById(R.id.ethnicitySpinner))
						.setSelection(((Integer) p.getValue()).intValue());
				break;
			case PREF_INCOME:
				((Spinner) rootView.findViewById(R.id.incomeSpinner))
						.setSelection(((Integer) p.getValue()).intValue());
				break;
			case PREF_RIDERTYPE:
				((Spinner) rootView.findViewById(R.id.ridertypeSpinner))
						.setSelection(((Integer) p.getValue()).intValue());
				break;
			case PREF_RIDERHISTORY:
				((Spinner) rootView.findViewById(R.id.riderhistorySpinner))
						.setSelection(((Integer) p.getValue()).intValue());
				break;
			case PREF_ZIPHOME:
				((EditText) rootView.findViewById(R.id.TextZipHome))
						.setText((CharSequence) p.getValue());
				break;
			case PREF_ZIPWORK:
				((EditText) rootView.findViewById(R.id.TextZipWork))
						.setText((CharSequence) p.getValue());
				break;
			case PREF_ZIPSCHOOL:
				((EditText) rootView.findViewById(R.id.TextZipSchool))
						.setText((CharSequence) p.getValue());
				break;
			case PREF_EMAIL:
				((EditText) rootView.findViewById(R.id.TextEmail))
						.setText((CharSequence) p.getValue());
				break;
			case PREF_CYCLEFREQ:
				((Spinner) rootView.findViewById(R.id.cyclefreqSpinner))
						.setSelection(((Integer) p.getValue()).intValue());
				break;
			case PREF_GENDER:
				((Spinner) rootView.findViewById(R.id.genderSpinner))
						.setSelection(((Integer) p.getValue()).intValue());
				break;
			}
		}

		final EditText edittextEmail = (EditText) rootView
				.findViewById(R.id.TextEmail);

		edittextEmail.setImeOptions(EditorInfo.IME_ACTION_DONE);

		setHasOptionsMenu(true);
		return rootView;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRegions();
    }

    private void initRegions() {
        regionSpinner = (Spinner) getActivity().findViewById(R.id.regionsSpinner);

        Bundle args = new Bundle();
        args.putBoolean(RELOAD, false);
        getLoaderManager().initLoader(0, args, this);

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLoaderCheck) {
                    mLoaderCheck = false;
                    return;
                }
                if (mObaRegions != null) {
                    ObaRegion selectedRegion = mObaRegions.get(position);
                    Application.get().setCurrentRegion(selectedRegion);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	public void savePreferences() {
		SharedPreferences settings = getActivity().getSharedPreferences(
				"PREFS", 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putInt("" + PREF_AGE,
				((Spinner) getActivity().findViewById(R.id.ageSpinner))
						.getSelectedItemPosition());
		editor.putInt("" + PREF_ETHNICITY, ((Spinner) getActivity()
				.findViewById(R.id.ethnicitySpinner)).getSelectedItemPosition());
		editor.putInt("" + PREF_INCOME,
				((Spinner) getActivity().findViewById(R.id.incomeSpinner))
						.getSelectedItemPosition());
		editor.putInt("" + PREF_RIDERTYPE, ((Spinner) getActivity()
				.findViewById(R.id.ridertypeSpinner)).getSelectedItemPosition());
		editor.putInt("" + PREF_RIDERHISTORY, ((Spinner) getActivity()
				.findViewById(R.id.riderhistorySpinner))
				.getSelectedItemPosition());

		editor.putString("" + PREF_ZIPHOME, ((EditText) getActivity()
				.findViewById(R.id.TextZipHome)).getText().toString());
		editor.putString("" + PREF_ZIPWORK, ((EditText) getActivity()
				.findViewById(R.id.TextZipWork)).getText().toString());
		editor.putString("" + PREF_ZIPSCHOOL, ((EditText) getActivity()
				.findViewById(R.id.TextZipSchool)).getText().toString());
		editor.putString("" + PREF_EMAIL, ((EditText) getActivity()
				.findViewById(R.id.TextEmail)).getText().toString());

		editor.putInt("" + PREF_CYCLEFREQ, ((Spinner) getActivity()
				.findViewById(R.id.cyclefreqSpinner)).getSelectedItemPosition());

		editor.putInt("" + PREF_GENDER,
				((Spinner) getActivity().findViewById(R.id.genderSpinner))
						.getSelectedItemPosition());

		// Don't forget to commit your edits!!!
		editor.commit();
		Toast.makeText(getActivity(), "User information saved.",
				Toast.LENGTH_SHORT).show();
	}

	/* Creates the menu items */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu items for use in the action bar
		inflater.inflate(R.menu.user_info, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/* Handles item selections */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_save_user_info:
			savePreferences();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public Loader<ArrayList<ObaRegion>> onCreateLoader(int id, Bundle args) {
        boolean refresh = args.getBoolean(RELOAD);
        return new ObaRegionsLoader(getActivity(), refresh);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<ObaRegion>> loader, ArrayList<ObaRegion> data) {
        mObaRegions = data;
        mLoaderCheck = true;

        ArrayList<String> arraySpinner = new ArrayList<String>();
        ObaRegion currentRegion = Application.get().getCurrentRegion();
        int selection = 0;
        int i = 0;

        for (ObaRegion r: data) {
            arraySpinner.add(r.getName());
            if (r.getId() == currentRegion.getId()) {
                selection = i;
            }
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, arraySpinner);
        regionSpinner.setAdapter(adapter);
        regionSpinner.setSelection(selection);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<ObaRegion>> loader) {
    }
}