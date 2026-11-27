package com.example

import com.example.data.model.DefaultSeedData
import com.example.data.model.WeeklyPlanEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun defaultSeedData_containsMenuAndWeeklyPlans() {
    assertEquals(12, DefaultSeedData.menuItems.size)
    assertEquals(7, DefaultSeedData.weeklyPlans.size)
    assertTrue(DefaultSeedData.menuItems.any { it.category == "Breakfast" })
    assertTrue(DefaultSeedData.menuItems.any { it.category == "Lunch" })
    assertTrue(DefaultSeedData.menuItems.any { it.category == "Snacks" })
  }

  @Test
  fun weeklyPlan_parsesDishIdsCorrectly() {
    val plan = WeeklyPlanEntity("Monday", "m1, m5, m9")
    assertEquals(listOf("m1", "m5", "m9"), plan.dishIds())
  }
}
