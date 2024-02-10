package org.chatbot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawling {
    public static String crawling() {
        JSONObject jsonResponse = new JSONObject();
        JSONArray items = new JSONArray();
        Set<String> links = new HashSet<>();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));


        try {
            String baseUrl = "https://wwwn.kangwon.ac.kr"; // 웹사이트의 기본 URL
            String path = "https://wwwn.kangwon.ac.kr/www/selectBbsNttList.do?bbsNo=81&key=277&searchCtgry=%EC%A0%84%EC%B2%B4%40%40%EC%B6%98%EC%B2%9C&"; // 크롤링할 페이지의 경로

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

        } catch (IOException e) {
            // 타임아웃이나 다른 IOException 발생 시 처리
            System.err.println("네트워크 오류가 발생했습니다: " + e.getMessage());
            return new JSONObject().put("error", "Network error occurred: " + e.getMessage()).toString();
        } catch (Exception e) {
            // 기타 예외 처리
            System.err.println("오류가 발생했습니다: " + e.getMessage());
            return new JSONObject().put("error", "Error occurred: " + e.getMessage()).toString();
        }
    }
}
