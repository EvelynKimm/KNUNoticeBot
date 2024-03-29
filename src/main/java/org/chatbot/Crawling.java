package org.chatbot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawling {
    public static String crawling() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new Callable<String>() {
            public String call() throws Exception {
                return fetchNotices();
            }
        });

        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            return new JSONObject().put("message", "공지사항을 불러오는 중입니다. 잠시 후 다시 시도해주세요.").toString();
        } catch (Exception e) {
            return new JSONObject().put("error", "An error occurred: " + e.getMessage()).toString();
        } finally {
            executor.shutdown();
        }
    }

    private static String fetchNotices() throws IOException {
        JSONObject jsonResponse = new JSONObject();
        JSONArray items = new JSONArray();
        Set<String> links = new HashSet<>();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String baseUrl = "https://wwwk.kangwon.ac.kr";
        String path = "https://wwwk.kangwon.ac.kr/www/selectBbsNttList.do?bbsNo=81&key=277&searchCtgry=%EC%A0%84%EC%B2%B4%40%40%EC%B6%98%EC%B2%9C&";
        Document doc = Jsoup.connect(path).timeout(7000).get();
        Elements noticeRows = doc.select("tbody.tb > tr");

        for (Element row : noticeRows) {
            String date = row.select("td.date").text();

            if (date.equals(today)) {
                Element linkElement = row.select("td.subject > a").first();
                String title = linkElement.text();
                String relativeLink = linkElement.attr("href");
                String absoluteLink = baseUrl + relativeLink.replaceFirst("\\.", "/www");

                if (!links.contains(absoluteLink)) {
                    links.add(absoluteLink);

                    JSONObject button = new JSONObject();
                    button.put("action", "webLink");
                    button.put("label", "공지사항 보러가기");
                    button.put("webLinkUrl", absoluteLink);

                    JSONArray buttons = new JSONArray();
                    buttons.put(button);

                    JSONObject item = new JSONObject();
                    item.put("title", title);
                    item.put("description", date);
                    item.put("buttons", buttons);

                    items.put(item);
                }
            }
        }

        if (items.length() == 0) {
            JSONObject textCardContent = new JSONObject();
            textCardContent.put("title", "아직 " + today + " 공지사항이 업로드 되지 않았습니다.");
            textCardContent.put("description", today);

            items.put(textCardContent);
        }

        JSONObject carousel = new JSONObject();
        carousel.put("type", "textCard");
        carousel.put("items", items);

        JSONArray outputs = new JSONArray();
        JSONObject output = new JSONObject();
        output.put("carousel", carousel);
        outputs.put(output);

        JSONObject template = new JSONObject();
        template.put("outputs", outputs);

        jsonResponse.put("version", "2.0");
        jsonResponse.put("template", template);

        return jsonResponse.toString();
    }
}

