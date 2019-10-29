package jp.redmine.redmineclient.fragment.form;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import jp.redmine.redmineclient.R;
import jp.redmine.redmineclient.entity.RedmineAttachment;
import jp.redmine.redmineclient.entity.TypeConverter;
import jp.redmine.redmineclient.form.helper.FormHelper;
import jp.redmine.redmineclient.provider.Attachment;

public class DownloadForm extends FormHelper {
	public TextView textSize;
	public TextView textSubject;
	public TextView textCreated;
	public TextView textAuthor;
	public Button buttonDownload;
	public Button buttonBrowser;
	public ImageView imageView;
	private ScaleGestureDetector mScaleGestureDetector;

	public DownloadForm(Context context, View view){
		this.setup(view);
		this.setupEvents(context, view);
	}

	private static class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		private ImageView view;
		private float mScaleFactor = 1.0f;
		public ScaleListener(ImageView img){
			view = img;
		}
		@Override
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public boolean onScale(ScaleGestureDetector scaleGestureDetector){
			mScaleFactor *= scaleGestureDetector.getScaleFactor();
			mScaleFactor = Math.max(0.1f,Math.min(mScaleFactor, 10.0f));
			view.setScaleX(mScaleFactor);
			view.setScaleY(mScaleFactor);
			return true;
		}

	}

	public void setup(View view){
		textSize = view.findViewById(R.id.textSize);
		textSubject = view.findViewById(R.id.textSubject);
		textCreated = view.findViewById(R.id.textCreated);
		textAuthor = view.findViewById(R.id.textAuthor);
		buttonDownload = view.findViewById(R.id.buttonDownload);
		buttonBrowser = view.findViewById(R.id.buttonBrowser);
		imageView = view.findViewById(R.id.imageView);
	}

	@SuppressLint("ClickableViewAccessibility")
	public void setupEvents(Context context, View view){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			return;
		mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener(imageView));
		view.setOnTouchListener((v, event) -> {
			boolean ret = mScaleGestureDetector.onTouchEvent(event);
			if (onMove(imageView, event))
				return true;
			return ret;
		});
	}

	public void setValue(RedmineAttachment rd){
		textSubject.setText(rd.getFilename());
		textSize.setText(String.valueOf(rd.getFilesize()));
		setDateTime(textCreated, rd.getCreated());
		setMasterName(textAuthor, rd.getUser());

		String mime = TypeConverter.getMimeType(rd.getFilenameExt());
		if (mime.startsWith("image/")) {
			Uri uri = Attachment.getUrl(rd.getId());
			imageView.setImageURI(uri);
			imageView.setVisibility(View.VISIBLE);
			buttonDownload.setVisibility(View.GONE);
		} else {
			imageView.setVisibility(View.GONE);
			buttonDownload.setVisibility(View.VISIBLE);
		}
	}


	private float imageViewPosX, imageViewPosY;
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private boolean onMove(View view, MotionEvent event){
		// Thank you for the solution: https://stackoverflow.com/questions/14814542/moving-imageview-with-touch-event
		switch (event.getActionMasked() ){
			case MotionEvent.ACTION_DOWN:
				imageViewPosX = view.getX() - event.getRawX();
				imageViewPosY = view.getY() - event.getRawY();
				return true;
			case MotionEvent.ACTION_MOVE:
				if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					onMoveAction11(view, event);
				else
					onMoveAction14(view, event);
			default:
				return false;
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void onMoveAction11(View view, MotionEvent event){
			view.setX(event.getRawX() + imageViewPosX);
			view.setY(event.getRawY() + imageViewPosY);
	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void onMoveAction14(View view, MotionEvent event){
			view.animate()
					.x(event.getRawX() + imageViewPosX)
					.y(event.getRawY() + imageViewPosY)
					.setDuration(0).start();
	}

}

