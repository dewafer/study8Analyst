import groovy.sql.Sql

/**
 * Created by dewafer on 16/2/17.
 */
class ChineseCharacterUtils {

    /// utf-8 ///
    static def start = 0x4e00;
    static def end = 0x9fa5;
    static def delta = end - start + 1;
    static Random rand = new Random();

    static char genChar(int offset) {
        assert offset >= 0
        assert offset <= delta
        start + offset
    }

    static char genChar() {
        return genChar(rand.nextInt(delta))
    }

    static String genChinese(int count) {
        def sb = new StringBuilder();
        count.times { sb << genChar() }
        return sb.toString()
    }

    static void main(String[] args) {
        (2..5).each { println genGeneralChinese(it) }
    }

    /// gbk ///

    static def highStart = 176
    static def highDelta = 39
    static def lowStart = 161
    static def lowDelta = 93

    static def maxOffsetGBK = (highDelta + 1) * lowDelta - 5

    static String genGeneralChineseChar(int offset) {
        assert offset >= 0
        assert offset <= maxOffsetGBK

        byte[] b = new byte[2]
        b[0] = highStart + offset / lowDelta
        b[1] = lowStart + offset % lowDelta
        return new String(b, 'GBK')
    }

    static String genGeneralChineseChar() {
        return genGeneralChineseChar(rand.nextInt(maxOffsetGBK) + 1)
    }

    static String genGeneralChinese(int count) {
        def sb = new StringBuilder();
        count.times { sb << genGeneralChineseChar() }
        return sb.toString()
    }

}

