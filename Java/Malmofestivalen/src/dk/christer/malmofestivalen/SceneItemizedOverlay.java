package dk.christer.malmofestivalen;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;


public class SceneItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	private Context _context;
	private ArrayList<OverlayItem> _items = new ArrayList<OverlayItem>();
	private MapView _view;
	private IOnTapListener _tapListener = null;
	
	public SceneItemizedOverlay(Drawable arg0, Context context, MapView view) {
		super(boundCenterBottom(arg0));
		_context = context;
		_view = view;
	}

	public void addOverlay(OverlayItem overlay) {
		_items.add(overlay);
	}	

	public void PopulateNow() {
	    populate();
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// TODO Auto-generated method stub
		super.draw(canvas, mapView, false);
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return (OverlayItem)_items.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return _items.size();
	}
	
	public ArrayList<OverlayItem> GetOverlayItems()
	{
		return _items;
	}
	
	@Override
	protected boolean onTap(int index) {
		if (_tapListener != null) {
			_tapListener.onTap((SceneOverlayItem)_items.get(index));
		}
		return true;
	}
	public void SetTapListener(IOnTapListener taplistener) {
		_tapListener = taplistener;
	}
	
	public interface IOnTapListener {
		public void onTap(SceneOverlayItem item);
	}
}
