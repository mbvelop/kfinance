package com.mbvelop.kfinance

import com.mbvelop.kfinance.enums.PaymentSchedule.BEGIN
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class KFinanceKtTest {

    @Test
    fun testPmt() {
        val result = pmt(0.08 / 12, 5 * 12.0, 15000.0)
        result shouldBe (-304.145914 plusOrMinus 0.001)
    }

    @Test
    fun testPmtRate0() {
        val result = pmt(0.0, 5 * 12.0, 15000.0)
        result shouldBe (-250.0 plusOrMinus 0.001)
    }

    @Test
    fun testPmtPaymentScheduleBegin() {
        val result = pmt(0.08 / 12, 5 * 12.0, 15000.0, paymentSchedule = BEGIN)
        result shouldBe (-302.1317029 plusOrMinus 0.001)
    }
}
