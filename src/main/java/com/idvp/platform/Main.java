package com.idvp.platform;

import com.idvp.platform.journal.Journal;

public class Main {

  public static void main(String[] args) {
    final Journal<String> journal = new Journal<>("SomeJournal", String.class);
    journal.open();
    journal.write("Hello world");
    journal.close();
  }
}
