import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtil {

	public static List<NewsBean> findUrl(int page) {
		Set<String> newsUrl = new TreeSet<String>();
		HttpURLConnection con;
		InputStream inStr;
		InputStreamReader istreamReader;
		BufferedReader buffStr;
		Pattern pattern = Pattern.compile("(<a href=')(.*?)('>)");
		for (int i = 1; i <= page; i++) {
			try {
				con = (HttpURLConnection) new URL(
						"http://www.shanghaidaily.com/article/list.aspx?&p="
								+ i + "&s=7").openConnection();
				con.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
				con.setConnectTimeout(60000);
				con.setReadTimeout(60000);
				
				int responseCode = con.getResponseCode();
				if (responseCode != 200) {
				
					continue;
				}

				inStr = con.getInputStream();
				istreamReader = new InputStreamReader(inStr, "UTF-8");
				buffStr = new BufferedReader(istreamReader);

				String str = "";
				while ((str = buffStr.readLine()) != null) {
					Matcher matcher = pattern.matcher(str);
					if (matcher.find()) {
						newsUrl.add("http://www.shanghaidaily.com"
								+ matcher.group(2));
						// System.out.println("http://www.shanghaidaily.com"
						// + matcher.group(2));
					}
				}
				inStr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		List<NewsBean> newsBeans = new ArrayList<NewsBean>();
		for (String url : newsUrl) {
			NewsBean newsBean = findContent(url);
			if (newsBean != null) {
				newsBeans.add(newsBean);
			}
		}
		return newsBeans;
	}

	public static NewsBean findContent(String url) {
		
		NewsBean bean = new NewsBean();
		
		bean.setUrl(url);
		HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			con.setConnectTimeout(60000);
			con.setReadTimeout(60000);
			
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				
				return null;
			}

			InputStream inStr = con.getInputStream();
			InputStreamReader istreamReader = new InputStreamReader(inStr,
					"UTF-8");
			BufferedReader buffStr = new BufferedReader(istreamReader);

			String str = null;
			
			Pattern pattern = Pattern
					.compile("(<div class=\"detail_byline border_bottom\">)[\\s\\S]*?(<img src=\"/images/icon_PE.png\")");
			StringBuilder builder = new StringBuilder();
			while ((str = buffStr.readLine()) != null) {
				builder.append(str);
			}

			Matcher matcher = pattern.matcher(builder.toString());

			if (matcher.find()) {
				String replace = matcher.group(0).replace(matcher.group(1), "")
						.replace(matcher.group(2), "").replace("<p>", "")
						.replace("</p>", "").trim();
				bean.setDate(replace.split("\\|")[1].trim());
			}

			
			Pattern pattern1 = Pattern
					.compile("(title|summary|pic)(: \")[\\s\\S]*?(\",)");
			
			matcher = pattern1.matcher(builder.toString());
			while (matcher.find()) {
				
				String content = matcher.group(0).replace(matcher.group(1), "")
						.replace(matcher.group(2), "")
						.replace(matcher.group(3), "").trim();
				

				if ("title".equals(matcher.group(1))) {
					bean.setTitle(content);
				} else if ("summary".equals(matcher.group(1))) {
					bean.setSummary(content);
				} else if ("pic".equals(matcher.group(1))) {
					bean.setPic(content);
				}

			}
			inStr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
}
