package br.com.santaconstacia.sta.vudroid;

import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.core.DocumentView;
import org.vudroid.core.ViewerPreferences;
import org.vudroid.core.models.CurrentPageModel;
import org.vudroid.core.models.DecodingProgressModel;
import org.vudroid.core.models.ZoomModel;
import org.vudroid.core.views.PageViewZoomControls;
import org.vudroid.pdfdroid.codec.PdfContext;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class PdfViewerFragment extends Fragment{

    public static final String PDF_URI = "PDF_URI";
    private static final int MENU_EXIT = 0;
    private static final int MENU_GOTO = 1;
    private static final int MENU_FULL_SCREEN = 2;
    private static final int DIALOG_GOTO = 0;
    private static final String DOCUMENT_VIEW_STATE_PREFERENCES = "DjvuDocumentViewState";
    private DecodeService decodeService;
    private DocumentView documentView;
    private ViewerPreferences viewerPreferences;
    private Toast pageNumberToast;
    private CurrentPageModel currentPageModel;

    /**
     * Called when the activity is first created.
     */
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        initDecodeService();
        final ZoomModel zoomModel = new ZoomModel();
        final DecodingProgressModel progressModel = new DecodingProgressModel();
        progressModel.addEventListener(getActivity());
        currentPageModel = new CurrentPageModel();
        currentPageModel.addEventListener(getActivity());
        documentView = new DocumentView(getActivity(), zoomModel, progressModel, currentPageModel);
        zoomModel.addEventListener(documentView);
        documentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        decodeService.setContentResolver(getActivity().getContentResolver());
        decodeService.setContainerView(documentView);
        documentView.setDecodeService(decodeService);
        Uri uri = Uri.parse(getArguments().getString(PDF_URI));
        decodeService.open(uri);

        viewerPreferences = new ViewerPreferences(getActivity());

        final FrameLayout frameLayout = createMainContainer();
        frameLayout.addView(documentView);
        frameLayout.addView(createZoomControls(zoomModel));

        documentView.goToPage(0);
        documentView.showDocument();

        viewerPreferences.addRecent(uri);
        
        return frameLayout;
    }

    public void decodingProgressChanged(final int currentlyDecoding)
    {
    	/*
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS, currentlyDecoding == 0 ? 10000 : currentlyDecoding);
            }
        });*/
    }

    public void currentPageChanged(int pageIndex)
    {
        final String pageText = (pageIndex + 1) + "/" + decodeService.getPageCount();
        if (pageNumberToast != null)
        {
            pageNumberToast.setText(pageText);
        }
        else
        {
            pageNumberToast = Toast.makeText(getActivity(), pageText, 300);
        }
        pageNumberToast.setGravity(Gravity.TOP | Gravity.LEFT,0,0);
        pageNumberToast.show();
    }

    /*
    private void setWindowTitle()
    {
       final String name = getIntent().getData().getLastPathSegment();
        getWindow().setTitle(name);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        setWindowTitle();
    }*/

    private PageViewZoomControls createZoomControls(ZoomModel zoomModel)
    {
        final PageViewZoomControls controls = new PageViewZoomControls(getActivity(), zoomModel);
        controls.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        zoomModel.addEventListener(controls);
        return controls;
    }

    private FrameLayout createMainContainer()
    {
        return new FrameLayout(getActivity());
    }

    private void initDecodeService()
    {
        if (decodeService == null)
        {
            decodeService = createDecodeService();
        }
    }

    protected DecodeService createDecodeService()
    {
        return new DecodeServiceBase(new PdfContext());
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        decodeService.recycle();
        decodeService = null;
        super.onDestroy();
    }

    private void setFullScreenMenuItemText(MenuItem menuItem)
    {
        menuItem.setTitle("Full screen " + (menuItem.isChecked() ? "on" : "off"));
    }
}
