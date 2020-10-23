package com.mbvelop.kfinance

internal operator fun DoubleArray.div(other: DoubleArray): DoubleArray {
    require(size == other.size)
    return DoubleArray(size) { this[it] / other[it] }
}
