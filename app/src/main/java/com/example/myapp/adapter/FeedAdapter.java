package com.example.myapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.myapp.R;
import com.example.myapp.model.Message;
import com.example.volley.helper.ApplicationLoader;

public class FeedAdapter extends ArrayAdapter<Message>{

	private Context context;
	private List<Message> feedList = new ArrayList<Message>();
	ImageLoader imageLoader = ApplicationLoader.getInstance().getImageLoader();

	public FeedAdapter(Context context, List<Message> objects) {
		super(context, R.layout.view_post, objects);
		this.context = context;
		this.feedList = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		ViewHolder holder;

		if (imageLoader == null) {
			imageLoader = ApplicationLoader.getInstance().getImageLoader();
		}

		if(row == null) {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			row = inflater.inflate(R.layout.view_post, null);
			
			holder = new ViewHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
			holder.txtDescription = (TextView)row.findViewById(R.id.txtDescription);
			//holder.txtLink = (TextView)row.findViewById(R.id.txtLink);
			holder.txtPubDate = (TextView)row.findViewById(R.id.txtPubDate);
			holder.imgViewLink = (NetworkImageView)row.findViewById(R.id.imgLink);
			row.setTag(holder);
		} 
		
		holder = (ViewHolder) row.getTag();
		
		Message msgRow = feedList.get(position);
		
		holder.txtTitle.setText(msgRow.getTitle());
		//holder.txtLink.setText(msgRow.getLink());
		holder.txtDescription.setText(msgRow.getDescription());
		holder.txtPubDate.setText(msgRow.getDate());

		if (msgRow.getImageLink() != null) {
			holder.imgViewLink.setImageUrl(msgRow.getImageLink(), imageLoader);
		} else {
			holder.imgViewLink.setImageResource(R.drawable.no_image_available);
		}

		return row;
	}
	
	private class ViewHolder {
		//TextView txtLink;
		TextView txtPubDate;
		TextView txtDescription;
		TextView txtTitle;
		NetworkImageView imgViewLink;
	}
}
