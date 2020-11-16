# olivot:올리봇
## 2018 OYIT OT&amp;OT Project

### 선행작업

    1. Clone repository 
       https://github.com/Microsoft/botbuilder-java
    2. mvn install to install the botbuilder SDK for Java

### 사용법

    1. java -jar training/target/training-1.0-SNAPSHOT.jar to generate vectorizer/classifier.bin from q&a csv
    2. mvn clean compile
    3. mvn package 
    4. deploy war
    
    Local) Application Add to Server > Start Server
    

### 카카오톡 플러스친구 적용

    https://center-pf.kakao.com/_rNxbtC
       
### 올리브영 온라인몰 테스트 적용

    http://oliveyoung.co.kr

### 한글 처리
  
    twitter-korean-text v4.4
    https://github.com/twitter/twitter-korean-text
    
    - 정규화(Normalization)
       : 입니닼ㅋㅋ -> 입니다
    - 토큰화(Tokenization) 
       : 한국어 처리하는 예시입니다 -> 한국어Noun,처리Noun,하는Verb,예시Noun,입Adjective,니다Eomi
    - 어근화(Stemming) 
       : 입니다 -> 이다
    - 어구 추출(Phrase Extraction)
       : 한국어 처리하는 예시입니다 -> 한국어,처리,예시,처리하는 예시

### 머신 러닝 / API
  
    deeplearning4j
    https://deeplearning4j.org/
    네이버 검색 API


![olivot_1](https://user-images.githubusercontent.com/40586079/99204093-e8bc6c00-27f7-11eb-9f66-87496687ea47.png)

![olivot_2](https://user-images.githubusercontent.com/40586079/99204090-e65a1200-27f7-11eb-9d5d-4b7a43957ffc.png)

![olivot_3](https://user-images.githubusercontent.com/40586079/99204092-e8bc6c00-27f7-11eb-86e3-75e363d48bcb.png)
