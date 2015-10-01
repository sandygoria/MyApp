package com.example.myapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myapp.R;
import com.example.myapp.adapter.FeedAdapter;
import com.example.myapp.model.Message;
import com.example.myapp.utils.RssFeedParser;

import java.util.List;

/**
 * Created by mobility on 01/10/15.
 */
public class FeedViewFragment extends Fragment {


    private String urlToParse = "http://rss.jagran.com/naidunia/madhya-pradesh/ujjain.xml";
    private ListView listViewPosts;
    private List<Message> messages;
    private  ProgressDialog pdDialog;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed,null);

        pdDialog =  new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        pdDialog.setMessage("Please wait..");
        pdDialog.setTitle("Fetching latest news");

        listViewPosts = (ListView)view.findViewById(R.id.listViewPost);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefLayoutLayout);

        //likeView = (LikeView) findViewById(R.id.like_view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeeds();
            }
        });

        fetchFeeds();

        listViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), messages.get(position).toString(), Toast.LENGTH_SHORT).show();

                Intent viewMessage = new Intent(Intent.ACTION_VIEW, Uri.parse(messages.get(position).getLink()));
                startActivity(viewMessage);

                Intent inAppBroswer = new Intent(getActivity(), WebActivity.class);
                inAppBroswer.putExtra("URL", messages.get(position).getLink());
                inAppBroswer.putExtra("TITLE", messages.get(position).getTitle());
                startActivity(inAppBroswer);
//
//
//				ShareLinkContent content = new ShareLinkContent.Builder()
//						.setContentUrl(Uri.parse(messages.get(position).getLink()))
//						.setContentDescription("shared from facevook")
//						.build();

//				likeView.setObjectIdAndType(
//						messages.get(position).getLink(),
//						LikeView.ObjectType.PAGE);
            }
        });


        listViewPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"Long press "+messages.get(position).getDescription(), Toast.LENGTH_SHORT).show();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, messages.get(position).getDescription());
                startActivity(Intent.createChooser(shareIntent, "Send To.."));
                startActivity(shareIntent);

                return true;
            }
        });

        return view;
    }

    private void fetchFeeds() {

        if(isNetworkAvailable()) {
            FeedParseTask feedParseTask = new FeedParseTask(getActivity());
            feedParseTask.execute(urlToParse);
        } else {
            Toast.makeText(getActivity(), "Please check your network connection and try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private class FeedParseTask extends AsyncTask<String, Integer, List<Message>> {

        private Context context;
        public FeedParseTask(Activity activity) {
            this.context = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(pdDialog !=null && !pdDialog.isShowing()) {
                pdDialog.show();
            }
        }

        @Override
        protected List<Message> doInBackground(String... params) {
            String urlStr = params[0];

            RssFeedParser feedParser = new RssFeedParser(urlStr);
            messages = feedParser.parse();

            //Log.d("debug", " -- parsed message are :" + messages.toString());
            return messages;
        }


        @Override
        protected void onPostExecute(List<Message> result) {
            super.onPostExecute(result);

            if(result != null && result.size() > 0 ) {
                Log.e("Feed count", " list size is ::" + result.size());

                FeedAdapter feedAdapter = new FeedAdapter(context, result);
                listViewPosts.setAdapter(feedAdapter);

            } else {
                Log.e("Error ::", "-- in Error Getting list size");
            }

            swipeRefreshLayout.setRefreshing(false);

            if(pdDialog !=null && pdDialog.isShowing()) {
                pdDialog.dismiss();
            }
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
