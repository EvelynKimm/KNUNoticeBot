[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FEvelynKimm%2FKNUNoticeBot%2Fhit-counter&count_bg=%233DA6C8&title_bg=%23B268AF&icon=&icon_color=%23924A4A&title=welcome&edge_flat=false)](https://hits.seeyoufarm.com) <br/>

## 💻 KNU 공지사항을 전달해주는 카카오 챗봇 💻 
<br/>

## ✏️ 기능

이 프로젝트는 Jsoup 라이브러리를 사용하여 공지사항 웹페이지를 크롤링하여 새롭게 올라온 공지사항을 톡방에서 확인할 수 있도록 돕는 챗봇입니다. 이 챗봇을 통해 사용자는 학교 공지사항의 업데이트를 신속하게 확인할 수 있습니다.


|사진|설명|
|---|---|
|<img src="https://github.com/EvelynKimm/KNUNoticeBot/assets/108613992/f3b11585-aafc-4024-9290-98baffb2b37b.png" width="80%" height="80%"/>|▷ '오늘의 공지사항 알아보기' 버튼을 통해 현재까지 오늘 날짜로 업로드된 공지사항을 확인할 수 있습니다. <br/><br/>▷ 공지가 2개 이상인 경우, 커리셀 형태로 확인이 가능합니다.|
|<img src="https://github.com/EvelynKimm/KNUNoticeBot/assets/108613992/591f66df-6c04-4819-8661-90b1a4082f36.png" width="80%" height="80%"/>|▷ 오늘 날짜로 업로드된 게시물이 없다면 에외 메시지를 출력합니다.|

<br/>

## ✏️ 챗봇 사용하기 


[KNU 공지사항 알리미 플러스 친구 추가하기](http://pf.kakao.com/_kUxfSG)
<br/> 위 링크를 통해 카카오 플러스 친구 추가 후 사용 가능합니다.

<br/>

## ✏️ 파일 설명

<br/>

### ✔︎ chatbot/Crawling.java

Jsoup 라이브러리를 사용해 공지사항 웹페이지에서 특정 정보인, 공지사항 제목, 날짜, 해당 게시물 링크를 크롤링하여 JSON 형식으로 변환하는 파일입니다.

KNU 공지사항 웹페이지는 특정 공지사항이 ‘[공지]’ 태그와 업로드되는 경우, ‘[일반]’에 올라온 해당 게시물과 중복됩니다. 이런 중복을 방지하기 위해, 중복이 허용하지 않는 set 자료구조를 사용합니다. link를 set에 추가해 중복되지 않도록 합니다.

또한, 사용자가 공지사항을 확인하려 했지만 당일 공지사항이 아직 업로드되지 않았을 경우, 별도의 안내 메시지를 출력하도록 했습니다.(예시: ‘아직 2024.02.01 공지사항이 업로드 되지 않았습니다.’)

<br/>

### ✔︎ chatbot/SimpleHandler.java

AWS Lambda를 위한 핸들러 클래스입니다. Lambda 실행 환경에서 Map<String, Object> 형식의 입력을 받아 처리하고, 문자열 형식의 결과를 반환합니다. 

Crawling.crawling() 메서드를 호출하여 JSON 문자열 형태를 리턴받고 그를 AWS Lambda로 업로드할 수 있도록 제작한 클래스입니다.

<br/>

### ✔︎ target/KNUNoticeChatBotFinal-1.0-SNAPSHOT.jar

AWS Lambda에 배포하기 위해 mvn clean package 명령어를 통해 제작한 JAR 파일입니다. Lambda는 Java 파일 편집기를 제공하지 않으므로, JAR 혹은 ZIP 파일 형태로 코드를 업로드해야 합니다.

<br/>

### ✔︎ pom.xml

위 파일에서 해당 프로젝트의 의존성을 확인할 수 있습니다. AWS SDK(com.amazonaws), Jsoup(’org.jsoup) 및 JSON 처리를 위한 (org.json) 라이브러리를 의존성으로 추가했습니다.
