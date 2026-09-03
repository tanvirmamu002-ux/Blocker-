package com.example

import com.example.util.AppLanguage
import com.example.util.getStrings
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testBengaliDynamicGreetings() {
    val b = getStrings(AppLanguage.BENGALI)
    
    // Morning (5..11)
    assertEquals("শুভ সকাল, তানভীর", b.getDynamicGreeting("তানভীর", 8))
    // Noon (12..15)
    assertEquals("শুভ দুপুর, তানভীর", b.getDynamicGreeting("তানভীর", 13))
    // Afternoon (16..17)
    assertEquals("শুভ বিকাল, তানভীর", b.getDynamicGreeting("তানভীর", 17))
    // Evening (18..20)
    assertEquals("শুভ সন্ধ্যা, তানভীর", b.getDynamicGreeting("তানভীর", 19))
    // Night (21..4)
    assertEquals("শুভ রাত্রি, তানভীর", b.getDynamicGreeting("তানভীর", 22))
    assertEquals("শুভ রাত্রি, তানভীর", b.getDynamicGreeting("তানভীর", 2))

    // Boss filtering
    assertEquals("শুভ সকাল, তানভীর", b.getDynamicGreeting("তানভীর (Boss)", 8))
    assertEquals("শুভ সকাল", b.getDynamicGreeting("John Doe (Boss)", 8))
  }

  @Test
  fun testEnglishDynamicGreetings() {
    val e = getStrings(AppLanguage.ENGLISH)
    assertEquals("Good morning, Tanvir", e.getDynamicGreeting("Tanvir", 8))
    assertEquals("Good noon, Tanvir", e.getDynamicGreeting("Tanvir", 13))
    assertEquals("Good afternoon, Tanvir", e.getDynamicGreeting("Tanvir", 17))
    assertEquals("Good evening, Tanvir", e.getDynamicGreeting("Tanvir", 19))
    assertEquals("Good night, Tanvir", e.getDynamicGreeting("Tanvir", 23))
  }
}


