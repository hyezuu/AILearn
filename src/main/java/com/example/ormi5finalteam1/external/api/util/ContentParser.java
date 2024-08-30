package com.example.ormi5finalteam1.external.api.util;

public class ContentParser {

  public String parseGrammarExamplesQueryResponse(String contentValue) {
    // "INSERT INTO"로 시작하는 부분의 인덱스를 찾음
    int startIndex = contentValue.indexOf("INSERT INTO");

    // 마지막 세미콜론의 인덱스를 찾음
    int endIndex = contentValue.lastIndexOf(";") + 1;

    // 쿼리를 추출하여 반환
    if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
      return contentValue.substring(startIndex, endIndex);
    } else {
      return null;  // INSERT 쿼리를 찾지 못한 경우
    }
  }
}
