package com.google.sps.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AllowedLanguageCodes {
  private static final Language[] translationLangCodes = { new Language("English", "en", "en-US", 0),
      new Language("Romanian", "ro", "ro-RO", 1), new Language("German", "de", "de-DE", 2),
      new Language("French", "fr", "fr-FR", 3) };

  public static List<Language> getTranslationLanguageList() {
    return Collections.unmodifiableList(Arrays.asList(translationLangCodes));
  }

  public static String getTranslationCodeForId(int id) {
    if (id < 0 || id >= translationLangCodes.length)
      return null;
    return translationLangCodes[id].getTranslationCode();
  }

  public static String getSpeechCodeForId(int id) {
    if (id < 0 || id >= translationLangCodes.length)
      return null;
    return translationLangCodes[id].getSpeechCode();
  }
}
