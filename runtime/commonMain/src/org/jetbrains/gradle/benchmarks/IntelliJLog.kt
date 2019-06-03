package org.jetbrains.gradle.benchmarks

internal fun ijSuiteStart(parent: String, id: String) = buildString {
    append("<ijLog>")
    append("<event type='beforeSuite'>")
    append("<test id='$id' parentId='$parent'>")
    append("<descriptor name='$id'/>")
    append("</test>")
    append("</event>")
    append("</ijLog>")
}

internal fun ijSuiteFinish(
    parent: String, id: String, status: BenchmarkReporter.FinishStatus,
    startTime: Long = 0, endTime: Long = startTime
) = buildString {
    append("<ijLog>")
    append("<event type='afterSuite'>")
    append("<test id='$id' parentId='$parent'>")
    append("<result resultType='${status.toString().toUpperCase()}' startTime='$startTime' endTime='$endTime'/>")
    append("</test>")
    append("</event>")
    append("</ijLog>")
}

internal fun ijBenchmarkStart(parent: String, className: String, methodName: String) = buildString {
    append("<ijLog>")
    append("<event type='beforeTest'>")
    append("<test id='$className.$methodName' parentId='$parent'>")
    append("<descriptor name='$methodName' className='$className' />")
    append("</test>")
    append("</event>")
    append("</ijLog>")
}

internal fun ijBenchmarkFinish(
    parent: String, id: String, status: BenchmarkReporter.FinishStatus,
    startTime: Long = 0, endTime: Long = startTime
) = buildString {
    append("<ijLog>")
    append("<event type='afterTest'>")
    append("<test id='$id' parentId='$parent'>")
    append("<result resultType='${status.toString().toUpperCase()}' startTime='$startTime' endTime='$endTime'/>")
    append("</test>")
    append("</event>")
    append("</ijLog>")
}

internal fun ijBenchmarkFinishException(
    parent: String, id: String, error: String, stacktrace: String,
    startTime: Long = 0, endTime: Long = startTime
) = buildString {
    append("<ijLog>")
    append("<event type='afterTest'>")
    append("<test id='$id' parentId='$parent'>")
    append("<result resultType='FAILURE' startTime='$startTime' endTime='$endTime'>")
    append("<errorMsg>")
    append("<![CDATA[${error.toByteArrayUtf8().encodeBase64()}]]>")
    append("</errorMsg>")
    append("<stackTrace>")
    append("<![CDATA[${stacktrace.toByteArrayUtf8().encodeBase64()}]]>")
    append("</stackTrace>")
    append("</result>")
    append("</test>")
    append("</event>")
    append("</ijLog>")
}

internal fun ijLogOutput(parent: String, id: String, info: String) = buildString {
    append("<ijLog>")
    append("<event type='onOutput'>")
    append("<test id='$id' parentId='$parent'>")
    append("<event destination='StdOut'>")
    append("<![CDATA[${info.toByteArrayUtf8().encodeBase64()}]]>")
    append("</event>")
    append("</test>")
    append("</event>")
    append("</ijLog>")
}

private val BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
private val BASE64_MASK: Byte = 0x3f
private val BASE64_PAD = '='

private fun Int.toBase64(): Char = BASE64_ALPHABET[this]

private fun ByteArray.encodeBase64(): String {
    fun ByteArray.getOrZero(index: Int): Int = if (index >= size) 0 else get(index).toInt() and 0xFF

    val result = ArrayList<Char>(4 * size / 3)
    var index = 0
    while (index < size) {
        val symbolsLeft = size - index
        val padSize = if (symbolsLeft >= 3) 0 else (3 - symbolsLeft) * 8 / 6
        val chunk = (getOrZero(index) shl 16) or (getOrZero(index + 1) shl 8) or getOrZero(index + 2)
        index += 3

        for (i in 3 downTo padSize) {
            val char = (chunk shr (6 * i)) and BASE64_MASK.toInt()
            result.add(char.toBase64())
        }

        repeat(padSize) { result.add(BASE64_PAD) }
    }

    return String(result.toCharArray())
}

expect fun String.toByteArrayUtf8(): ByteArray