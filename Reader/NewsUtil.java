import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;



public class NewsUtil {

	public static List<NewsBean> getNewsList(int page) {
		
		long currentTimeMillis = System.currentTimeMillis();
		List<NewsBean> newsBeans = ParserUtil.findUrl(page);
		
		long timer = System.currentTimeMillis()-currentTimeMillis;
		
		System.out.println("Timer : "+timer / 1000);
		return newsBeans;
	}

	public static void main(String[] args) {
		List<NewsBean> newsList = getNewsList(1);
		for (NewsBean newsBean : newsList) {
			System.out.println(newsBean.getDate());
		}
	}
}
