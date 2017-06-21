package br.com.santaconstacia.sta.colecoes;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.com.santaconstacia.sta.R;

public class ColecaoGrupoFragment extends Fragment {

	public static final String TITLE = "TITLE";
	public static final String ITEMS = "ITEMS";
	public static final String ITEMS_ID = "ITEMS_ID";
	
	public static interface OnSelectionListener{
		public void onSelect(int item);
		public void onSelect(String item);
	}
	
	private OnSelectionListener onSelectionListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		String title = getArguments().getString(TITLE);
		String[] items = getArguments().getStringArray(ITEMS);
		int[] ids = getArguments().getIntArray(ITEMS_ID);
		
		View view = inflater.inflate(R.layout.fragment_colecao, container, false);
		
		TextView titleTextView = (TextView) view.findViewById(R.id.title);
		titleTextView.setText(toLabel(title));
		
		LinearLayout list = (LinearLayout) view.findViewById(android.R.id.list);
		
		for (int i=0; i < items.length; i++) {
			final String item = items[i];
			final int idItem = ids[i];
			inflater.inflate(R.layout.fragment_colecoes_item, list);
			Button button = (Button) list.getChildAt(i);
//			button.setText(toLabel(item));
			button.setText(item.replaceAll("_", " "));
			button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onSelectionListener.onSelect(item);
						onSelectionListener.onSelect(idItem);
					}
				}		
			);
		}

		return view;
	}
	
	private String toLabel(String text){
		String result = text;
		
		int index = result.indexOf("val_");
		
		if(index != -1)
			result = result.substring(index + 4);
		
		return result.replace('_', ' ');
	}

	public OnSelectionListener getOnSelectionListener() {
		return onSelectionListener;
	}

	public void setOnSelectionListener(OnSelectionListener onSelectionListener) {
		this.onSelectionListener = onSelectionListener;
	}
}
