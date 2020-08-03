package com.google.sps.classes;

public class Language {
  private String languageName = "";
  private String languageTranslationCode = "";
  private String languageSpeechCode = "";
  private int languageIndex;

  public Language(String languageName, String languageTranslationCode, String languageSpeechCode, int languageIndex) {
    this.languageName = languageName;
    this.languageTranslationCode = languageTranslationCode;
    this.languageSpeechCode = languageSpeechCode;
    this.languageIndex = languageIndex;
  }

  public String getTranslationCode() {
    return this.languageTranslationCode;
  }

  public String getSpeechCode() {
    return this.languageSpeechCode;
  }
}
