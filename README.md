# olivot:올리봇
## 2018 OYIT OT&amp;OT Project

//### 선행작업

//    1. Clone repository 
//       https://github.com/Microsoft/botbuilder-java
//    2. mvn install to install the botbuilder SDK for Java

### 사용법

    1. mvn clean compile
    2. mvn package 
    3. deploy war
    
    Local) mvn clean compile > Application Add to Server > Start Server
    

### 카카오톡 플러스친구 적용 중

    https://center-pf.kakao.com/_rNxbtC
       
### 올리브영 온라인몰 내 적용 계획

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

### 머신 러닝
  
    deeplearning4j
    https://deeplearning4j.org/
