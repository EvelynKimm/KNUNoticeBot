package org.chatbot;

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

public class Test {
    public static String test() {
        JSONObject jsonResponse = new JSONObject();
        JSONArray items = new JSONArray();
        Set<String> links = new HashSet<>();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));


        try {
            // 공지사항 페이지의 URL
//            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            String baseUrl = "https://wwwk.kangwon.ac.kr"; // 웹사이트의 기본 URL
            String path = "https://wwwk.kangwon.ac.kr/www/selectBbsNttList.do?bbsNo=81&key=277&searchCtgry=%EC%A0%84%EC%B2%B4%40%40%EC%B6%98%EC%B2%9C&"; // 크롤링할 페이지의 경로

            // 웹 페이지에서 HTML 문서를 가져옴
            Document doc = Jsoup.connect(path).get();

            // 각 공지사항을 나타내는 'tr' 태그를 선택함
            Elements noticeRows = doc.select("tbody.tb > tr");

            StringBuilder result = new StringBuilder();

            // 각 공지사항에 대해 반복
            for (Element row : noticeRows) {
                // 공지사항 날짜 추출 ('td.date' 셀렉터 사용)
                String date = row.select("td.date").text();

                if (date.equals(today)) {
                    // 공지사항 제목 추출 ('td.subject > a' 셀렉터 사용)
                    Element linkElement = row.select("td.subject > a").first();
                    String title = linkElement.text();

                    // 공지사항의 상대 링크 추출 ('href' 속성 값)
                    String relativeLink = linkElement.attr("href");

                    // 상대 링크를 절대 경로로 변환
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
                // 버튼 확인

                JSONObject textCard = new JSONObject();
                textCard.put("textCard", textCardContent);

                items.put(textCard);
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

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject().put("error", "Error occurred: " + e.getMessage()).toString();
        }
    }
}
