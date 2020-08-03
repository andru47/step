package com.google.sps.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllowedLanguageCodes {
  private static final Language[] translationLangCodes = { new Language("English", "en", "en-US", 0),
      new Language("Romanian", "ro", "ro-RO", 1), new Language("German", "de", "de-DE", 2),
      new Language("French", "fr", "fr-FR", 3) };

  public static List<Language> getTranslationLanguageList() {
    List<Language> lst = new ArrayList<>();
    for (Language it : translationLangCodes)
      lst.add(it);
    return lst;
  }

  public static String getTranslationCodeForIndex(int index) {
    return translationLangCodes[index].getTranslationCode();
  }

  public static String getSpeechCodeForIndex(int index) {
    return translationLangCodes[index].getSpeechCode();
  }
}
