package com.saaranga.wikikannada;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saaranga.wikitrack.utilities.Constants;
import com.saaranga.wikitrack.utilities.DateFormatter;
/**
 * Adapter class - <br>
 * PURPOSE: supplies adapter to the list view in acitivyt lists
 * 
 * @author supreeth
 * @version 1.0 30-05-2012
 * 
 *          Copyright Saaranga Infotech
 */
public class BoxListAdapter extends BaseAdapter {

	private static final String tag = "BoxList Adapter class";

	private Typeface tf;
	private int itemCount = 0;
	private String feedType;
	private FeedDBAdapter feedDBAdapter;
	private List<String> titleList, authorList, pubDateList ;
	private ArrayList<Integer> favList, markReadList, pressedList;
	private LayoutInflater inflater;
	private Context mContext;

	private String DEBUG_TAG ="BoxList Adapter class" ;

	private DateFormatter df;

	public BoxListAdapter(Context context,String fdType) {
		mContext = context;
		this.feedType = fdType;
		feedDBAdapter = new FeedDBAdapter(mContext);
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		try {
			//get data from database
			titleList = feedDBAdapter.getTitleList(feedType);
			itemCount = titleList.size();
			authorList = feedDBAdapter.getAuthorList(feedType);
			favList = feedDBAdapter.getFavoriteList(feedType);
			markReadList = feedDBAdapter.getMarkReadList(feedType);
			pressedList = feedDBAdapter.getMarkPressedList(feedType);
			pubDateList = feedDBAdapter.getPubDateList(feedType);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(tag, "Error in getting data from database");
		}
		
		df = new DateFormatter("yyyy-MM-dd hh:mm:ss");
		
		tf = Constants.getTypeface(mContext);
	}

	@Override
	public int getCount() {
		return itemCount;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mlog("getView called at: "+position);
		ViewHolder holder;
		if(convertView== null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_item_layout, parent,false);
			holder.itemLayout = (LinearLayout)convertView.findViewById(R.id.item_layout);
			holder.title = (TextView)convertView.findViewById(R.id.list_item_title);
			holder.author = (TextView)convertView.findViewById(R.id.list_item_author);
			holder.pubDate = (TextView)convertView.findViewById(R.id.list_item_pubdate);
			holder.favorite = (ImageView)convertView.findViewById(R.id.list_item_favorite);
			holder.mark_read = (ImageView)convertView.findViewById(R.id.list_item_mark_read);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		try {

			if(pressedList.get(position)==1) { //because list position index starts from 1
				holder.title.setTextColor(R.color.grey);
//				holder.itemLayout.setBackgroundResource(R.drawable.lis_items_background_selected);	
			} else {
//				holder.itemLayout.setBackgroundResource(R.drawable.list_item_unselected);
			}
			
			//set typeace
			if(tf!=null) {
				//font preference present
				holder.title.setTypeface(tf);
				holder.author.setTypeface(tf);
				holder.pubDate.setTypeface(tf);
			} else {
				//dont set typeface
			}
			
			holder.title.setText(titleList.get(position));
			holder.author.setText(authorList.get(position));
			
			//get formatted date
			String unformatted  = pubDateList.get(position);
			mlog("unformatted date: "+unformatted);
			String formattedDate = df.getFormattedDate((unformatted.replace("T", " ")).replace("Z", ""));
			mlog("formatted date: "+formattedDate);
			
			holder.pubDate.setText(formattedDate);
			int fav = favList.get(position);
			int markRead = markReadList.get(position);
			if(fav==0){
				holder.favorite.setImageResource(R.drawable.btn_star_big_off_005);
			}else {
				holder.favorite.setImageResource(R.drawable.btn_star_big_on_006);
			}

			if(markRead==0) {
				holder.mark_read.setImageResource(R.drawable.btn_check_off_005);
			} else {
				holder.mark_read.setImageResource(R.drawable.btn_check_on_005);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		return convertView;
	}


	class ViewHolder {
		TextView title ;
		TextView author;
		TextView pubDate;
		ImageView favorite;
		ImageView mark_read;
		LinearLayout itemLayout;
	}

	private void mlog(String msg) {
		Log.d(DEBUG_TAG  , msg);
	}

}


