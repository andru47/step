package com.google.sps.classes;

public class Language {
  private String languageName = "";
  private String languageTranslationCode = "";
  private String languageSpeechCode = "";
  private int languageId;

  public Language(String languageName, String languageTranslationCode, String languageSpeechCode, int languageId) {
    this.languageName = languageName;
    this.languageTranslationCode = languageTranslationCode;
    this.languageSpeechCode = languageSpeechCode;
    this.languageId = languageId;
  }

  public String getTranslationCode() {
    return this.languageTranslationCode;
  }

  public String getSpeechCode() {
    return this.languageSpeechCode;
  }
}
